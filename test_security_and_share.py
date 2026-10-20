"""安全负向测试：保留真实失败，不能把越权成功当预期成功。分享仅测预留后端。"""
import pytest
from testing.support import case

pytestmark = pytest.mark.remote


@case("SEC-PROJECT-READ", "项目权限", "外组用户不可读取项目详情", "outsider读取其他小组项目", "403/10102/404/10201拒绝访问", goal="安全")
def test_outsider_project_read(api, world):
    body = api.call("GET", f'/project/{world["pid"]}', token=world["outsider"]["token"])
    assert body["code"] in (403, 10102, 404, 10201), "项目缺少资源归属校验，非成员读到了项目"


@case("SEC-FILE-AUDIT", "文件权限", "普通成员不可冒充组长审核", "成员Token审核文件", "403/10102拒绝；状态仍pending", goal="安全")
def test_member_cannot_approve(api, world):
    fid = api.upload(world)["fileId"]
    body = api.call("POST", f"/file/{fid}/approve", token=world["member"]["token"])
    assert body["code"] in (403, 10102), "审核接口缺少审核人角色校验"
    assert api.ok("GET", f"/file/{fid}")["status"] == "pending"


@case("SEC-MESSAGE", "消息权限", "其他用户不可查看或删除我的通知", "outsider操作member的messageId", "404；原通知仍可查询", goal="安全")
def test_message_ownership(api, world):
    api.create_task(world, assigneeId=world["member"]["userId"])
    mid = api.ok("GET", "/message/list", token=world["member"]["token"])["list"][0]["messageId"]
    assert api.call("GET", f"/message/{mid}", token=world["outsider"]["token"])["code"] == 404
    assert api.call("DELETE", f"/message/{mid}", token=world["outsider"]["token"])["code"] == 404
    assert api.ok("GET", f"/message/{mid}", token=world["member"]["token"])["messageId"] == mid


@case("BB-SHARE-LIFE", "分享预留接口", "创建访客读取刷新撤销分享", "flow_download分享；刷新；撤销", "新Token可用；旧Token失效；撤销后404")
def test_share_lifecycle(api, world):
    share = api.ok("POST", "/share/create", json={"projectId": world["pid"], "permissionLevel": "flow_download"})
    sid, old = share["shareId"], share["shareToken"]
    assert api.ok("GET", f"/share/p/{old}", token="")
    new = api.ok("POST", f"/share/{sid}/regenerate")["shareToken"]
    assert new != old and api.call("GET", f"/share/p/{old}", token="")["code"] == 404
    api.ok("DELETE", f"/share/{sid}")
    assert api.call("GET", f"/share/p/{new}", token="")["code"] == 404


@case("SEC-SHARE-PERM", "分享权限", "仅流程查看不可下载文件", "permissionLevel=flow_only；访客请求download", "业务码403拒绝", goal="安全")
def test_readonly_share_download(api, world):
    fid = api.upload(world)["fileId"]
    api.ok("POST", f"/file/{fid}/approve")
    share = api.ok("POST", "/share/create", json={"projectId": world["pid"], "permissionLevel": "flow_only"})
    body = api.call("GET", f'/share/p/{share["shareToken"]}/file/{fid}/download', token="")
    assert body["code"] == 403, "分享下载接口未执行permissionLevel校验"


@case("BB-SHARE-EXPIRED", "分享预留接口", "过期分享禁止访客访问", "expiresAt=2020-01-01", "业务码10501", goal="安全")
def test_share_expired(api, world):
    share = api.ok("POST", "/share/create", json={"projectId": world["pid"], "permissionLevel": "flow_download", "expiresAt": "2020-01-01 00:00:00"})
    assert api.call("GET", f'/share/p/{share["shareToken"]}', token="")["code"] == 10501


@pytest.mark.parametrize("permission,preview,download", [("flow_only", False, False), ("flow_preview", True, False), ("flow_download", True, True)])
@case("SEC-SHARE-MATRIX", "分享权限", "三种权限预览下载矩阵", "flow_only/flow_preview/flow_download", "分别只读、可预览、可下载", goal="安全", stage="回归测试")
def test_share_permission_matrix(api, world, permission, preview, download):
    fid = api.upload(world)["fileId"]
    api.ok("POST", f"/file/{fid}/approve")
    share = api.ok("POST", "/share/create", json={"projectId": world["pid"], "permissionLevel": permission})
    base = f'/share/p/{share["shareToken"]}/file/{fid}'
    assert api.call("GET", base + "/preview", token="")["code"] == (0 if preview else 403)
    assert api.call("GET", base + "/download", token="")["code"] == (0 if download else 403)
