package com.rocgit.knife4j.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocgit.knife4j.common.BusinessException;
import com.rocgit.knife4j.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 微信小程序服务端接口
 *
 * 用 wx.login 拿到的 code 调用 jscode2session 换取 openid / session_key。
 * AppID 与 AppSecret 通过环境变量 ROCGIT_WECHAT_APPID / ROCGIT_WECHAT_SECRET 注入，
 * 不写入代码仓库。
 * 文档：https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
 */
@Service
public class WechatService {

    private static final Logger log = LoggerFactory.getLogger(WechatService.class);
    private static final String JSCODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${rocgit.wechat.appid:}")
    private String appid;

    @Value("${rocgit.wechat.secret:}")
    private String secret;

    /**
     * 用前端 wx.login 的 code 换取 openid
     *
     * @param code 小程序临时登录凭证（5 分钟内有效，只能用一次）
     * @return 微信 openid
     */
    public String code2openid(String code) {
        if (appid == null || appid.trim().isEmpty()
                || secret == null || secret.trim().isEmpty()) {
            throw new BusinessException(ResultCode.FAIL, "微信登录未配置：请在服务器设置 ROCGIT_WECHAT_APPID / ROCGIT_WECHAT_SECRET");
        }

        String url = JSCODE2SESSION_URL
                + "?appid=" + URLEncoder.encode(appid.trim(), StandardCharsets.UTF_8)
                + "&secret=" + URLEncoder.encode(secret.trim(), StandardCharsets.UTF_8)
                + "&js_code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";

        try {
            String body = httpGet(url);
            JsonNode node = objectMapper.readTree(body);
            // 成功返回 openid/session_key；失败返回 errcode/errmsg
            JsonNode errcode = node.get("errcode");
            if (errcode != null && errcode.asInt() != 0) {
                String errmsg = node.has("errmsg") ? node.get("errmsg").asText() : "unknown";
                log.warn("微信 code2session 失败: errcode={}, errmsg={}", errcode.asInt(), errmsg);
                throw new BusinessException(ResultCode.BAD_REQUEST,
                        "微信登录失败(" + errcode.asInt() + ")：" + errmsg);
            }
            JsonNode openidNode = node.get("openid");
            if (openidNode == null || openidNode.asText().isEmpty()) {
                log.warn("微信 code2session 未返回 openid: {}", body);
                throw new BusinessException(ResultCode.FAIL, "微信登录失败：未获取到 openid");
            }
            return openidNode.asText();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信 code2session 异常", e);
            throw new BusinessException(ResultCode.FAIL, "微信登录服务暂不可用：" + e.getMessage());
        }
    }

    private String httpGet(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
        if (is == null) {
            throw new IllegalStateException("微信接口无响应，HTTP " + status);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
