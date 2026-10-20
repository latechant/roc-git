"""黑盒：只通过公开 HTTP 契约断言，不读数据库。"""
import pytest
from testing.support import case

pytestmark = pytest.mark.remote


@pytest.mark.parametrize("path", ["/user/profile", "/group/my-list", "/project/list?groupId=1", "/file/list", "/task/my-tasks", "/message/list", "/project/template/list", "/announcement/list"])
@case("BB-AUTH", "接口鉴权", "未登录访问受保护接口", "无Authorization", "HTTP200且业务码401", goal="安全")
def test_missing_authorization(api, path):
    assert api.call("GET", path)["code"] == 401


@pytest.mark.parametrize("token", ["bad", "a.b.c", "", "Bearer forged"])
@case("BB-TOKEN", "接口鉴权", "无效Token不可访问", "错误Token或空Token", "空Token返回401；错误Token返回10003", goal="安全")
def test_invalid_token(api, token):
    assert api.call("GET", "/user/profile", token=token)["code"] == (10003 if token else 401)


@pytest.mark.parametrize("field,value", [("code", None), ("code", ""), ("code", "  "), ("nickname", None), ("nickname", ""), ("nickname", "  "), ("avatarUrl", None), ("avatarUrl", "")])
@case("BB-LOGIN-VALID", "登录", "必填字段等价类校验", "code/nickname/avatarUrl为null或空白", "业务码400", goal="边界与分支")
def test_login_validation(api, field, value):
    payload = {"code": "test-invalid", "nickname": "测试", "avatarUrl": "https://example.invalid/a", "source": "h5"}
    payload[field] = value
    assert api.call("POST", "/user/login", json=payload)["code"] == 400


@pytest.mark.parametrize("path", ["/health", "/config"])
@case("BB-PUBLIC", "公共接口", "公共接口无需登录", "无Token", "code0且data非空", goal="接口")
def test_public_endpoints(api, path):
    assert api.ok("GET", path)


@case("BB-LOGIN-REPEAT", "登录", "相同设备重复登录复用用户", "相同code登录两次", "userId不变；第二次isNewUser=false")
def test_login_repeat(api):
    import uuid
    payload = {"code": uuid.uuid4().hex, "nickname": "重复测试", "avatarUrl": "https://example.invalid/a", "source": "h5"}
    a = api.ok("POST", "/user/login", json=payload)
    b = api.ok("POST", "/user/login", json=payload)
    assert a["isNewUser"] is True and b["isNewUser"] is False
    assert a["userId"] == b["userId"] and b["token"]


@case("BB-PROFILE", "用户", "更新昵称后重新查询", "nickname=测试新昵称", "返回新昵称且userId不变")
def test_profile_update(api, world):
    api.ok("PUT", "/user/profile", json={"nickname": "测试新昵称"})
    p = api.ok("GET", "/user/profile")
    assert p["nickname"] == "测试新昵称" and p["userId"] == world["leader"]["userId"]


@case("BB-GROUP", "小组", "创建加入及重复加入不增加成员", "组长创建；组员使用邀请码加入两次", "成员总数2，leaderId正确")
def test_group_membership(api, world):
    g = api.ok("GET", f'/group/{world["gid"]}')
    assert g["leaderId"] == world["leader"]["userId"] and len(g["inviteCode"]) == 6
    api.ok("POST", "/group/join", token=world["member"]["token"], json={"inviteCode": g["inviteCode"]})
    assert api.ok("GET", f'/group/{world["gid"]}/members')["total"] == 2


@case("BB-GROUP-ROLE", "小组", "组员不能修改小组", "组员Token修改名称", "业务码403；名称不变", goal="安全")
def test_group_update_forbidden(api, world):
    body = api.call("PUT", f'/group/{world["gid"]}', token=world["member"]["token"], json={"name": "非法修改"})
    assert body["code"] == 403
    assert api.ok("GET", f'/group/{world["gid"]}')["name"] == world["group"]["name"]


@case("BB-GROUP-LEAVE", "小组", "组长不能退出；成员可退出", "leader和member分别退出", "组长403；组员退出后成员总数1")
def test_leave_group(api, world):
    assert api.call("POST", f'/group/{world["gid"]}/leave')["code"] == 403
    api.ok("POST", f'/group/{world["gid"]}/leave', token=world["member"]["token"])
    assert api.ok("GET", f'/group/{world["gid"]}/members')["total"] == 1


@case("BB-INVITE", "邀请码", "刷新后旧码失效新码可用", "组长刷新邀请码", "新旧码不同；旧码valid=false，新码true")
def test_invite_rotation(api, world):
    old = world["group"]["inviteCode"]
    new = api.ok("POST", f'/group/{world["gid"]}/invite-code')["inviteCode"]
    assert new != old
    assert api.ok("POST", "/group/validate-invite-code", token="", json={"inviteCode": old})["valid"] is False
    assert api.ok("POST", "/group/validate-invite-code", token="", json={"inviteCode": new})["valid"] is True


@case("BB-TRANSFER", "小组", "转让组长后权限交换", "组长转让给组员", "leaderId变更；原组长不能再改名")
def test_transfer_leader(api, world):
    api.ok("POST", f'/group/{world["gid"]}/transfer-leader', json={"newLeaderId": world["member"]["userId"]})
    assert api.ok("GET", f'/group/{world["gid"]}')["leaderId"] == world["member"]["userId"]
    assert api.call("PUT", f'/group/{world["gid"]}', json={"name": "旧组长修改"})["code"] == 403


@case("BB-PROJECT", "项目", "创建项目阶段成员初始化", "2阶段、2成员", "项目active；首阶段active；其余pending；成员2")
def test_project_initialization(api, world):
    p = api.ok("GET", f'/project/{world["pid"]}')
    stages = api.ok("GET", f'/project/{world["pid"]}/stage')
    assert p["status"] == "active" and p["progress"] == 0
    assert [s["status"] for s in stages] == ["active", "pending"]
    assert p["currentStageId"] == stages[0]["stageId"]
    assert len(api.ok("GET", f'/project/{world["pid"]}/members')) == 2


@case("BB-TEMPLATE", "模板", "按实际种子生成9个软件工程阶段", "选择软件工程课程设计模板", "生成9阶段而非报告中的5阶段")
def test_project_template(api, world):
    templates = api.ok("GET", "/project/template/list")
    template = next(t for t in templates if t["name"] == "软件工程课程设计")
    assert template["stageCount"] == 9
    p = api.ok("POST", "/project/create", json={"name": "模板项目", "groupId": world["gid"], "templateId": template["templateId"]})
    assert len(api.ok("GET", f'/project/{p["projectId"]}/stage')) == 9


@case("BB-STAGE-CRUD", "阶段", "新增重命名排序删除阶段", "新增第3阶段；改名；调整顺序；删除", "查询反映全部变更")
def test_stage_crud(api, world):
    base = f'/project/{world["pid"]}/stage'
    added = api.ok("POST", base, json={"name": "新增阶段"})
    sid = added["stageId"]
    api.ok("PUT", f"{base}/{sid}", json={"name": "修改阶段"})
    assert api.ok("GET", f"{base}/{sid}")["name"] == "修改阶段"
    ids = [s["stageId"] for s in api.ok("GET", base)][::-1]
    api.ok("PUT", base + "/order", json={"stageIds": ids})
    assert [s["stageId"] for s in api.ok("GET", base)] == ids
    api.ok("DELETE", f"{base}/{sid}")
    assert api.call("GET", f"{base}/{sid}")["code"] == 10201


@case("BB-STAGE-ADVANCE", "阶段", "逐阶段推进并清空当前阶段", "推进第1和第2阶段", "首次allCompleted=false；末次true；currentStageId=null", stage="系统测试")
def test_stage_advance(api, world):
    base = f'/project/{world["pid"]}'
    stages = api.ok("GET", base + "/stage")
    a = api.ok("POST", f'{base}/stage/{stages[0]["stageId"]}/advance')
    assert a["allCompleted"] is False and a["currentStageId"] == stages[1]["stageId"]
    b = api.ok("POST", f'{base}/stage/{stages[1]["stageId"]}/advance')
    assert b["allCompleted"] is True and b["currentStageId"] is None
    assert api.ok("GET", base)["currentStageId"] is None


@case("BB-STAGE-FORBIDDEN", "阶段", "非成员不可推进项目阶段", "外组用户Token", "业务码10102且阶段仍active", goal="安全")
def test_stage_nonmember(api, world):
    sid = api.ok("GET", f'/project/{world["pid"]}/stage')[0]["stageId"]
    assert api.call("POST", f'/project/{world["pid"]}/stage/{sid}/advance', token=world["outsider"]["token"])["code"] == 10102
    assert api.ok("GET", f'/project/{world["pid"]}/stage/{sid}')["status"] == "active"


@case("BB-COMPLETE", "结项归档", "结项清空当前阶段并禁止推进", "结项后尝试推进，再归档", "completed/progress100/currentStageId=null；推进400；归档archived")
def test_complete_archive(api, world):
    base = f'/project/{world["pid"]}'
    sid = api.ok("GET", base + "/stage")[0]["stageId"]
    api.ok("POST", base + "/complete")
    p = api.ok("GET", base)
    assert p["status"] == "completed" and p["progress"] == 100 and p["currentStageId"] is None
    assert api.call("POST", f'{base}/stage/{sid}/advance')["code"] == 400
    api.ok("PUT", base + "/archive")
    assert api.ok("GET", base)["status"] == "archived"


@pytest.mark.parametrize("module,code", [("group", 10101), ("project", 10201), ("file", 10301), ("task", 10401)])
@case("BB-NOTFOUND", "资源错误", "不存在资源返回明确业务码", "ID=9223372036854770000", "对应资源不存在业务码；不返回成功", goal="接口")
def test_missing_resource(api, world, module, code):
    assert api.call("GET", f"/{module}/9223372036854770000")["code"] == code


@pytest.mark.parametrize("ext", "md txt pdf doc docx xls xlsx ppt pptx png jpg jpeg zip".split())
@case("BB-UPLOAD-TYPE", "文件", "白名单扩展名允许上传", "13种白名单后缀；小字节样本（不代表内容解码兼容性）", "文件ID、版本1、pending、类型正确", goal="边界与分支")
def test_upload_allowed_types(api, world, ext):
    f = api.upload(world, "test." + ext)
    assert f["fileId"] > 0 and f["version"] == 1 and f["status"] == "pending" and f["type"] == ext


@pytest.mark.parametrize("name", ["danger.exe", "script.js", "none", "report.pdf.exe"])
@case("BB-UPLOAD-DENY", "文件", "不支持的文件类型拒绝上传", "exe/js/无后缀/双后缀exe", "非0业务码且包含不支持提示", goal="安全")
def test_upload_denied_types(api, world, name):
    body = api.call("POST", "/file/upload", data={"projectId": world["pid"]}, files={"file": (name, b"x")})
    assert body["code"] != 0 and "不支持" in body["message"]


@pytest.mark.parametrize("override,content", [("danger.exe", b"x"), ("safe.txt", b"")])
@case("BB-FILENAME-VALIDATE", "上传校验", "自定义文件名不能绕过类型和空文件校验", "fileName=danger.exe或空文件+safe.txt", "拒绝上传且返回具体校验原因", goal="安全", stage="回归测试")
def test_filename_override_validation(api, world, override, content):
    body = api.call("POST", "/file/upload", data={"projectId": world["pid"], "fileName": override}, files={"file": ("tmp_sample", content)})
    assert body["code"] != 0
    assert "不支持" in body["message"] if content else "文件为空" in body["message"]


@case("BB-FILENAME-PATH", "上传校验", "自定义文件名剥离路径", "fileName=../escape.txt", "仅保留escape.txt，URL不含../", goal="安全", stage="回归测试")
def test_override_path_sanitized(api, world):
    f = api.upload(world, "tmp_sample", fileName="../escape.txt")
    assert f["name"] == "escape.txt" and "../" not in f["url"]


@case("BB-VERSION", "文件版本", "上传驳回重传回滚保留版本链", "v1文本；驳回；v2文本；回滚v1", "生成v3且内容指向v1；版本列表1,2,3", stage="系统测试")
def test_file_version_lifecycle(api, world):
    f = api.upload(world)
    fid = f["fileId"]
    api.ok("POST", f"/file/{fid}/reject", json={"reason": "需要补充完整内容"})
    assert api.ok("GET", f"/file/{fid}")["status"] == "rejected"
    f2 = api.ok("POST", f"/file/{fid}/resubmit", files={"file": ("report.txt", b"version two\n")})
    assert f2["version"] == 2 and f2["status"] == "pending"
    api.ok("POST", f"/file/{fid}/approve")
    rollback = api.ok("POST", f"/version/{fid}/rollback", json={"targetVersion": 1})
    assert rollback["newVersion"] == 3
    current = api.ok("GET", f"/file/{fid}")
    assert current["url"] == f["url"] and current["version"] == 3
    versions = api.ok("GET", "/version/list", params={"fileId": fid})
    assert sorted(v["version"] for v in versions) == [1, 2, 3]


@case("BB-FAVORITE", "文件", "收藏切换及软删除不可见", "收藏两次；删除文件", "true后false；删除后10301")
def test_file_favorite_delete(api, world):
    fid = api.upload(world)["fileId"]
    assert api.ok("POST", f"/file/{fid}/favorite")["isFavorite"] is True
    assert api.ok("POST", f"/file/{fid}/favorite")["isFavorite"] is False
    api.ok("DELETE", f"/file/{fid}")
    assert api.call("GET", f"/file/{fid}")["code"] == 10301


@case("BB-FILTER", "文件检索", "关键词类型及分页筛选", "上传alpha.txt和beta.pdf；keyword=alpha&type=txt", "仅返回alpha.txt；超过末页为空且total不变")
def test_file_filters(api, world):
    api.upload(world, "alpha.txt")
    api.upload(world, "beta.pdf")
    p = {"projectId": world["pid"], "keyword": "alpha", "type": "txt"}
    data = api.ok("GET", "/file/list", params=p)
    assert data["total"] == 1 and data["list"][0]["name"] == "alpha.txt"
    empty = api.ok("GET", "/file/list", params={**p, "pageNum": 99, "pageSize": 1})
    assert empty["total"] == 1 and empty["list"] == []


@pytest.mark.parametrize("progress,status", [(0, "pending"), (1, "in_progress"), (99, "in_progress"), (100, "completed")])
@case("BB-TASK-PROGRESS", "任务", "进度边界自动流转", "progress=0/1/99/100", "pending/in_progress/in_progress/completed", goal="边界与分支")
def test_task_progress(api, world, progress, status):
    task = api.create_task(world, name="进度测试", assigneeId=world["member"]["userId"])
    tid = task["taskId"]
    api.ok("POST", f"/task/{tid}/progress", json={"progress": progress})
    detail = api.ok("GET", f"/task/{tid}")
    assert detail["progress"] == progress and detail["status"] == status


@pytest.mark.parametrize("progress", [-1, 101, None])
@case("BB-TASK-INVALID", "任务", "非法进度拒绝且不改状态", "progress=-1/101/null", "400；任务progress仍0", goal="边界与分支")
def test_task_invalid_progress(api, world, progress):
    tid = api.create_task(world, name="非法进度")["taskId"]
    assert api.call("POST", f"/task/{tid}/progress", json={"progress": progress})["code"] == 400
    assert api.ok("GET", f"/task/{tid}")["progress"] == 0


@case("BB-TASK-FLOW", "任务", "转交完成并从待办消失", "组长任务转给组员；完成", "assignee更新；completed+100；我的待办不含此任务", stage="系统测试")
def test_task_transfer_complete(api, world):
    tid = api.create_task(world, name="转交测试")["taskId"]
    api.ok("POST", f"/task/{tid}/transfer", json={"newAssigneeId": world["member"]["userId"]})
    assert api.ok("GET", f"/task/{tid}")["assigneeId"] == world["member"]["userId"]
    api.ok("POST", f"/task/{tid}/complete", json={"remark": "已完成"})
    assert api.ok("GET", f"/task/{tid}")["status"] == "completed"
    mine = api.ok("GET", "/task/my-tasks", token=world["member"]["token"])
    assert tid not in [t["taskId"] for t in mine["tasks"]]


@case("BB-MESSAGE", "消息", "任务通知及全部已读", "给成员分配任务；成员全部已读", "未读增加；read-all后未读0", stage="系统测试")
def test_message_read_all(api, world):
    api.create_task(world, name="通知测试", assigneeId=world["member"]["userId"])
    api.token = world["member"]["token"]
    assert api.ok("GET", "/message/unread-count")["task"] >= 1
    api.ok("POST", "/message/read-all", json={"type": "all"})
    assert api.ok("GET", "/message/unread-count")["total"] == 0


@case("BB-SUBSCRIPTION", "订阅", "五个订阅设置可更新", "auditNotify=false，taskRemind=false", "查询关闭项为false；其余维持true")
def test_subscription_settings(api, world):
    api.ok("PUT", "/message/subscription", json={"auditNotify": False, "taskRemind": False})
    sub = api.ok("GET", "/message/subscription")
    assert sub["auditNotify"] is False and sub["taskRemind"] is False
    assert sub["memberNotify"] is True


@pytest.mark.parametrize("size", [50*1024*1024-1, 50*1024*1024, 50*1024*1024+1])
@case("BB-UPLOAD-SIZE", "文件大小", "真实HTTP上传50MiB上下边界", "50MiB-1、50MiB、50MiB+1字节txt", "前两个成功；超限拒绝；数据库大小正确", goal="边界与分支")
def test_upload_size_boundaries(api, world, size):
    payload = b"x" * size
    body = api.call("POST", "/file/upload", data={"projectId": world["pid"]}, files={"file": ("boundary.txt", payload)})
    if size <= 50*1024*1024:
        assert body["code"] == 0 and body["data"]["size"] == size
    else:
        assert body["code"] != 0


@pytest.mark.parametrize("reason,valid", [("", False), ("四个汉字", False), ("五个字原因", True), ("字"*200, True), ("字"*201, False)])
@case("BB-REJECT-REASON", "审核输入", "驳回原因5到200字边界", "长度0/4/5/200/201", "5和200通过；其他400", goal="边界与分支")
def test_reject_reason_boundaries(api, world, reason, valid):
    fid = api.upload(world)["fileId"]
    body = api.call("POST", f"/file/{fid}/reject", json={"reason": reason})
    assert body["code"] == (0 if valid else 400)
