"""白盒：用 Node 执行项目原有工具函数及请求层各分支。"""
import json
import os
import shutil
import subprocess
from pathlib import Path
import pytest
from testing.support import case

ROOT = Path(__file__).parent
FORMAT = "src/utils-new/format.js"
AVATAR = "src/utils/avatar.js"
API = "src/utils/api.js"


def probe(request, module, fn, args=None, **options):
    if not shutil.which("node"):
        pytest.fail("白盒 JS 用例需要 Node.js")
    env = dict(os.environ, TZ="Asia/Shanghai")
    proc = subprocess.run(["node", str(ROOT / "testing/js_probe.mjs")], input=json.dumps(
        dict(module=module, fn=fn, args=args or [], **options), ensure_ascii=False),
        encoding="utf-8", capture_output=True, env=env, timeout=15)
    assert proc.returncode == 0, proc.stderr
    result = json.loads(proc.stdout)
    request.node.observed = [result]
    return result


VALUES = [
    (FORMAT, "formatFileSize", [None], "-"),
    (FORMAT, "formatFileSize", [0], "0 B"),
    (FORMAT, "formatFileSize", [1], "1 B"),
    (FORMAT, "formatFileSize", [1023], "1023 B"),
    (FORMAT, "formatFileSize", [1024], "1.0 KB"),
    (FORMAT, "formatFileSize", [1536], "1.5 KB"),
    (FORMAT, "formatFileSize", [1048576], "1.0 MB"),
    (FORMAT, "formatFileSize", [52428800], "50.0 MB"),
    (FORMAT, "formatFileSize", [1073741824], "1.0 GB"),
    (FORMAT, "formatDate", [None], ""),
    (FORMAT, "formatDate", ["invalid"], "invalid"),
    (FORMAT, "formatDate", ["2026-10-20T08:09:00+08:00"], "2026-10-20"),
    (FORMAT, "formatDate", ["2026-10-20T08:09:00+08:00", "MM-DD HH:mm"], "10-20 08:09"),
    (FORMAT, "fileExt", [None], ""),
    (FORMAT, "fileExt", ["README"], ""),
    (FORMAT, "fileExt", ["REPORT.PDF"], "pdf"),
    (FORMAT, "fileExt", ["a.tar.zip"], "zip"),
    (FORMAT, "fileExt", ["a."], ""),
    (AVATAR, "avatarChar", [None], ""),
    (AVATAR, "avatarChar", ["  "], ""),
    (AVATAR, "avatarChar", ["  alice "], "A"),
    (AVATAR, "avatarChar", ["张三"], "张"),
    (AVATAR, "avatarChar", [123], "1"),
]
for status, badge, text in [("pending", "default", "待开始"), ("ongoing", "primary", "进行中"), ("completed", "success", "已完成"), ("unknown", "default", "unknown")]:
    VALUES += [(FORMAT, "stageStatusBadge", [status], badge), (FORMAT, "stageStatusText", [status], text)]
for status, badge, text in [("pending", "warning", "待审核"), ("submitted", "warning", "待审核"), ("approved", "success", "已通过"), ("rejected", "danger", "已驳回"), ("unknown", "default", "unknown")]:
    VALUES += [(FORMAT, "fileStatusBadge", [status], badge), (FORMAT, "fileStatusText", [status], text)]


@pytest.mark.whitebox
@pytest.mark.parametrize("module,fn,args,expected", [pytest.param(*v, id=f"WB-F{i:03d}",
    marks=case(f"WB-F{i:03d}", "前端工具", f"{v[1]}({v[2]})", repr(v[2]), repr(v[3]), "白盒", "边界与分支", "单元测试"))
    for i, v in enumerate(VALUES, 1)])
def test_utility_branches(request, module, fn, args, expected):
    result = probe(request, module, fn, args)
    assert not result.get("error"), result
    assert result["value"] == expected


@pytest.mark.parametrize("date,expected", [(None, ""), ("bad", "bad"),
    ("2026-10-20T11:59:01+08:00", "刚刚"), ("2026-10-20T11:59:00+08:00", "1 分钟前"),
    ("2026-10-20T11:00:00+08:00", "1 小时前"), ("2026-10-19T12:00:00+08:00", "1 天前"),
    ("2026-10-13T12:00:00+08:00", "2026-10-13")])
@case("WB-TIME", "时间显示", "相对时间临界值", "冻结时钟 2026-10-20 12:00；一分钟/一小时/一天/七天", "分别显示正确时间桶", "白盒", "边界与分支", "单元测试")
def test_time_boundaries(request, date, expected):
    assert probe(request, FORMAT, "timeAgo", [date], now="2026-10-20T12:00:00+08:00")["value"] == expected


@case("WB-BUCKET", "消息分组", "今天昨天更早及自定义字段", "三个时间桶；字段=t", "按今天、昨天、更早顺序分组", "白盒", "功能", "单元测试")
def test_time_buckets(request):
    rows = [{"t": "2026-10-20T00:00:00+08:00"}, {"t": "2026-10-19T23:59:59+08:00"}, {"t": "2026-10-18T23:59:59+08:00"}]
    value = probe(request, FORMAT, "groupByTimeBucket", [rows, "t"], now="2026-10-20T12:00:00+08:00")["value"]
    assert [v["label"] for v in value] == ["今天", "昨天", "更早"]
    assert [v["items"] for v in value] == [[x] for x in rows]


@pytest.mark.parametrize("value", [None, [], {}, "bad"])
@case("WB-EMPTY", "消息分组", "非列表或空列表", "null / [] / {} / 字符串", "空数组", "白盒", "健壮性", "单元测试")
def test_empty_buckets(request, value):
    assert probe(request, FORMAT, "groupByTimeBucket", [value])["value"] == []


@case("WB-COLOR", "头像", "颜色稳定且来自既有色板", "相同昵称调用两次", "同昵称同色，文字正确", "白盒", "功能", "单元测试")
def test_avatar_color(request):
    a = probe(request, AVATAR, "textAvatar", ["Alice"])["value"]
    b = probe(request, AVATAR, "textAvatar", ["Alice"])["value"]
    assert a == b and a["char"] == "A"
    assert a["color"] in ["#3b82f6", "#6366f1", "#8b5cf6", "#ec4899", "#ef4444", "#f59e0b", "#10b981", "#14b8a6"]


@pytest.mark.parametrize("code", [401, 40101, 1001, 10002, 10003])
@case("WB-AUTH", "请求鉴权", "失效业务码清除凭证并跳转", "后端业务码 401/40101/1001/10002/10003", "reject；清缓存；只跳转一次", "白盒", "安全", "回归测试")
def test_api_unauthorized_codes(request, code):
    result = probe(request, API, "getProfile", storage={"rocgit_token": "old", "rocgit_user": {"userId": 1}},
                   body={"code": code, "message": "凭证失效"})
    assert result.get("error")
    assert "rocgit_token" not in result["storage"]
    assert sum(e["kind"] == "redirect" for e in result["events"]) == 1


@case("WB-QUERY", "请求封装", "查询编码和保留零值", "中文、&、null、空串、0、false", "正确编码；忽略null空串；保留0与false", "白盒", "接口", "单元测试")
def test_query_encoding(request):
    result = probe(request, API, "request", [{"url": "/v1/file/list", "params": {"keyword": "中文&?", "n": 0, "enabled": False, "skip": None, "empty": ""}}])
    url = result["events"][0]["url"]
    assert "keyword=%E4%B8%AD%E6%96%87%26%3F" in url
    assert "n=0" in url and "enabled=false" in url and "skip=" not in url and "empty=" not in url


@pytest.mark.parametrize("opts", [dict(status=503), dict(networkFail=True), dict(body={"code": 400, "message": "参数错误"})])
@case("WB-ERR", "请求封装", "HTTP业务网络异常拒绝Promise", "HTTP503 / 网络失败 / code400", "reject；保留有效登录态", "白盒", "健壮性", "单元测试")
def test_request_failures(request, opts):
    result = probe(request, API, "getProfile", storage={"rocgit_token": "valid"}, **opts)
    assert result.get("error")
    assert result["storage"]["rocgit_token"] == "valid"
    assert not any(e["kind"] == "redirect" for e in result["events"])


@case("WB-LOGIN", "登录缓存", "登录成功缓存用户和Token", "模拟接口返回合法用户", "缓存用户并自动给请求带Bearer", "白盒", "接口", "单元测试")
def test_login_cache(request):
    result = probe(request, API, "login", [{"code": "sample", "nickname": "测试"}],
                   body={"code": 0, "data": {"userId": 9, "token": "new-token"}})
    assert result["storage"]["rocgit_token"] == "new-token"
    assert result["storage"]["rocgit_user"]["userId"] == 9


@case("WB-LOGOUT", "登录缓存", "退出清理用户缓存", "已有Token和用户", "两个缓存项被移除", "白盒", "安全", "单元测试")
def test_logout(request):
    result = probe(request, API, "logout", storage={"rocgit_token": "old", "rocgit_user": {"userId": 1}})
    assert result["storage"] == {}
