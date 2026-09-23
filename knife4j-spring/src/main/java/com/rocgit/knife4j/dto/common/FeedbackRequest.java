package com.rocgit.knife4j.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 用户反馈请求
 */
@Data
@Schema(description = "反馈请求")
public class FeedbackRequest {

    @NotBlank(message = "反馈类型不能为空")
    @Schema(description = "反馈类型", example = "bug",
            allowableValues = {"bug", "suggestion", "complaint"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @NotBlank(message = "反馈内容不能为空")
    @Size(min = 5, max = 500, message = "反馈内容长度需在 5-500 字之间")
    @Schema(description = "反馈内容", example = "登录页面偶尔会闪退",
            minLength = 5, maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "截图URL列表，最多 3 张",
            maxLength = 3)
    private List<String> images;

    @Schema(description = "联系方式（可选）", example = "QQ: 123456")
    private String contact;
}
