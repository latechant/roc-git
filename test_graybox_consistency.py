"""灰盒：了解表结构和状态机，经HTTP触发行为，再核对真实MySQL与文件。"""
import hashlib
import json
import shlex
import pytest
from testing.support import case

pytestmark = pytest.mark.remote


def observe(request, sql, actual):
    if not hasattr(request.node, "observed"):
        request.node.observed = []
    request.node.observed.append({"SQL": sql, "actual": actual})
    return actual


@case("GB-SCHEMA", "数据库结构", "仓库23表和17条模板种子初始化", "在独立库执行仓库schema+seed", "23张表、17个阶段模板；生产库不参与", "灰盒", "数据一致性", "集成测试")
def test_schema_seed(lab, request):
    tables = int(lab.scalar("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=DATABASE()"))
    seeds = int(lab.scalar("SELECT COUNT(*) FROM stage_template"))
    observe(request, "information_schema.tables / stage_template count", {"tables": tables, "seed_rows": seeds})
    assert tables == 23 and seeds == 17


@case("GB-LOGIN", "用户持久化", "注册自动创建默认订阅", "H5测试设备登录", "user行存在；订阅五项全1且仅1行", "灰盒", "数据一致性", "集成测试")
def test_registration_persistence(api, world, lab, request):
    uid = int(world["leader"]["userId"])
    query = f"SELECT JSON_OBJECT('user_id',u.id,'nickname',u.nickname,'audit',s.audit_notify,'task',s.task_remind,'member',s.member_notify,'stage',s.stage_notify,'mention',s.mention_notify) FROM user u JOIN user_subscription s ON s.user_id=u.id WHERE u.id={uid}"
    data = json.loads(observe(request, query, lab.scalar(query)))
    assert data == {"user_id": uid, "nickname": "测试_leader", "audit": 1, "task": 1, "member": 1, "stage": 1, "mention": 1}


@case("GB-MEMBERS", "小组持久化", "重复加入不产生重复成员关系", "相同userId和groupId重复join", "2个active关系且1个leader", "灰盒", "数据一致性", "集成测试")
def test_unique_membership(api, world, lab, request):
    api.ok("POST", "/group/join", token=world["member"]["token"], json={"inviteCode": world["group"]["inviteCode"]})
    query = f"SELECT JSON_OBJECT('total',COUNT(*),'active',SUM(status='active'),'leaders',SUM(role='leader')) FROM group_member WHERE group_id={int(world['gid'])}"
    value = json.loads(observe(request, query, lab.scalar(query)))
    assert value == {"total": 2, "active": 2, "leaders": 1}


@case("GB-ROLLBACK", "事务", "非法项目成员触发事务全量回滚", "members包含非小组用户", "400；project、stage、project_member均未留下记录", "灰盒", "可靠性", "集成测试")
def test_project_transaction_rollback(api, world, lab, request):
    import uuid
    name = "rollback_" + uuid.uuid4().hex[:10]
    before = lab.scalar("SELECT CONCAT((SELECT COUNT(*) FROM project),':',(SELECT COUNT(*) FROM stage),':',(SELECT COUNT(*) FROM project_member))")
    body = api.call("POST", "/project/create", json={"groupId": world["gid"], "name": name,
        "stages": [{"name": "应回滚"}], "members": [{"userId": world["outsider"]["userId"]}]})
    assert body["code"] == 400
    after = lab.scalar("SELECT CONCAT((SELECT COUNT(*) FROM project),':',(SELECT COUNT(*) FROM stage),':',(SELECT COUNT(*) FROM project_member))")
    observe(request, "project/stage/project_member总数前后对比", {"before": before, "after": after})
    assert before == after


@case("GB-DISK", "文件存储", "上传内容与数据库及磁盘一致", "UTF-8文本文件", "DB大小正确；磁盘SHA256和下载字节一致；版本快照1条", "灰盒", "数据一致性", "系统测试")
def test_upload_disk_consistency(api, world, lab, request):
    content = "ROC-git测试文件\n中文内容\n".encode()
    f = api.upload(world, content=content)
    fid = int(f["fileId"])
    query = f"SELECT JSON_OBJECT('size',size,'version',version,'url',url) FROM file WHERE id={fid}"
    row = json.loads(observe(request, query, lab.scalar(query)))
    assert row["size"] == len(content) and row["version"] == 1
    assert row["url"] == f["url"]
    disk = lab.remote + row["url"]
    digest = lab.command("sha256sum " + shlex.quote(disk)).split()[0]
    assert digest == hashlib.sha256(content).hexdigest()
    response = api.session.get(lab.base + row["url"], timeout=15)
    assert response.status_code == 200 and response.content == content
    assert int(lab.scalar(f"SELECT COUNT(*) FROM file_version WHERE file_id={fid} AND version=1")) == 1
    observe(request, "磁盘/HTTP字节校验", {"sha256": digest, "bytes": len(response.content), "http": response.status_code})


@case("GB-DELETE", "软删除", "文件删除保留记录但列表不可见", "DELETE已上传文件", "DB deleted=1；版本保留；接口不可见", "灰盒", "数据一致性", "集成测试")
def test_soft_delete(api, world, lab, request):
    fid = api.upload(world)["fileId"]
    api.ok("DELETE", f"/file/{fid}")
    query = f"SELECT deleted FROM file WHERE id={int(fid)}"
    assert observe(request, query, lab.scalar(query)) == "1"
    assert api.call("GET", f"/file/{fid}")["code"] == 10301
    assert int(lab.scalar(f"SELECT COUNT(*) FROM file_version WHERE file_id={int(fid)}")) == 1


@case("GB-AUDIT", "文件审核", "审核同时写入审核记录通知和操作日志", "上传后审核通过", "file approved；file_review、message、operation_log均存在", "灰盒", "数据一致性", "系统测试")
def test_audit_side_effects(api, world, lab, request):
    fid = api.upload(world)["fileId"]
    api.ok("POST", f"/file/{fid}/approve")
    counts = {
        "approved": lab.scalar(f"SELECT status FROM file WHERE id={fid}"),
        "reviews": int(lab.scalar(f"SELECT COUNT(*) FROM file_review WHERE file_id={fid} AND action='approved'")),
        "logs": int(lab.scalar(f"SELECT COUNT(*) FROM operation_log WHERE target_type='file' AND target_id={fid} AND action='approve'")),
        "messages": int(lab.scalar(f"SELECT COUNT(*) FROM message WHERE project_id={int(world['pid'])} AND type='audit' AND user_id={int(world['leader']['userId'])}")),
    }
    observe(request, "审核相关四表", counts)
    assert counts["approved"] == "approved" and counts["reviews"] == 1 and counts["logs"] == 1 and counts["messages"] >= 1


@case("GB-TASK", "任务完成", "完成任务状态进度时间日志一致", "complete任务", "status=completed,progress=100,completed_at非空；日志1条", "灰盒", "数据一致性", "集成测试")
def test_task_complete_consistency(api, world, lab, request):
    tid = api.create_task(world)["taskId"]
    api.ok("POST", f"/task/{tid}/complete", json={"remark": "完成"})
    query = f"SELECT JSON_OBJECT('status',status,'progress',progress,'timed',completed_at IS NOT NULL) FROM task WHERE id={int(tid)}"
    assert json.loads(observe(request, query, lab.scalar(query))) == {"status": "completed", "progress": 100, "timed": 1}
    assert int(lab.scalar(f"SELECT COUNT(*) FROM operation_log WHERE target_type='task' AND target_id={int(tid)} AND action='complete'")) == 1


@case("GB-STAGE", "项目阶段", "推进阶段更新当前阶段外键", "完成首阶段", "首阶段completed；次阶段active；project.current_stage_id指向次阶段", "灰盒", "数据一致性", "集成测试")
def test_stage_db_consistency(api, world, lab, request):
    pid = world["pid"]
    stages = api.ok("GET", f"/project/{pid}/stage")
    api.ok("POST", f'/project/{pid}/stage/{stages[0]["stageId"]}/advance')
    state = lab.sql(f"SELECT status FROM stage WHERE project_id={int(pid)} ORDER BY order_num")
    observe(request, "stage状态与project.current_stage_id", state)
    assert state.splitlines() == ["completed", "active"]
    assert int(lab.scalar(f"SELECT current_stage_id FROM project WHERE id={int(pid)}")) == stages[1]["stageId"]


@case("GB-MSG-DETAIL", "消息已读", "消息详情响应与持久化已读同步", "读取一条未读消息详情", "接口read=true；DB is_read=1", "灰盒", "数据一致性", "回归测试")
def test_message_detail_consistency(api, world, lab, request):
    api.create_task(world, assigneeId=world["member"]["userId"])
    api.token = world["member"]["token"]
    mid = api.ok("GET", "/message/list")["list"][0]["messageId"]
    data = api.ok("GET", f"/message/{mid}")
    query = f"SELECT is_read FROM message WHERE id={int(mid)}"
    assert observe(request, query, lab.scalar(query)) == "1"
    assert data["read"] is True


@case("GB-SUB-OFF", "订阅生效", "关闭任务通知后不再写入通知", "taskRemind=false；新建指派任务", "该用户该项目task消息数量不增加", "灰盒", "功能", "回归测试")
def test_subscription_enforced(api, world, lab, request):
    uid = world["member"]["userId"]
    api.ok("PUT", "/message/subscription", token=world["member"]["token"], json={"taskRemind": False})
    query = f"SELECT COUNT(*) FROM message WHERE user_id={int(uid)} AND project_id={int(world['pid'])} AND type='task'"
    before = int(lab.scalar(query))
    api.create_task(world, assigneeId=uid)
    after = int(lab.scalar(query))
    observe(request, query, {"before": before, "after": after})
    assert before == after


@case("GB-ACHIEVEMENT", "成果关联", "成果关联3文件并级联清理", "relatedFileIds=3个已上传文件", "中间表3条；删除成果后关联0条，原文件仍存在", "灰盒", "数据一致性", "集成测试")
def test_achievement_relations(api, world, lab, request):
    fids = [api.upload(world, f"report{i}.txt")["fileId"] for i in range(3)]
    stage = api.ok("GET", f'/project/{world["pid"]}/stage')[0]["stageId"]
    aid = api.ok("POST", "/achievement/create", json={"projectId": world["pid"], "stageId": stage,
        "title": "成果测试", "description": "测试成果关联文件", "relatedFileIds": fids})["achievementId"]
    query = f"SELECT COUNT(*) FROM achievement_file WHERE achievement_id={int(aid)}"
    assert int(observe(request, query, lab.scalar(query))) == 3
    api.ok("DELETE", f"/achievement/{aid}")
    assert int(lab.scalar(query)) == 0
    assert all(api.ok("GET", f"/file/{fid}")["fileId"] == fid for fid in fids)


@case("GB-FOREIGN-KEY", "数据库约束", "无效外键插入被MySQL拒绝", "测试库中向project_member插入不存在项目ID", "数据库拒绝；记录数不变", "灰盒", "可靠性", "集成测试")
def test_foreign_key_constraint(api, world, lab, request):
    before = lab.scalar("SELECT COUNT(*) FROM project_member")
    query = f"INSERT INTO project_member(project_id,user_id,role) VALUES(9223372036854770000,{int(world['leader']['userId'])},'member')"
    with pytest.raises(RuntimeError, match="远程命令失败"):
        lab.sql(query)
    after = lab.scalar("SELECT COUNT(*) FROM project_member")
    observe(request, "MySQL外键校验", {"before": before, "after": after, "rejected": True})
    assert before == after


@case("GB-UNIQUE", "数据库约束", "重复成员关系被唯一约束拒绝", "重复插入同group_id,user_id", "数据库拒绝；成员仍2人", "灰盒", "可靠性", "集成测试")
def test_unique_constraint(api, world, lab, request):
    query = f"INSERT INTO group_member(group_id,user_id,role,status) VALUES({int(world['gid'])},{int(world['leader']['userId'])},'member','active')"
    with pytest.raises(RuntimeError):
        lab.sql(query)
    count = int(lab.scalar(f"SELECT COUNT(*) FROM group_member WHERE group_id={int(world['gid'])}"))
    observe(request, "MySQL唯一约束", {"rejected": True, "members": count})
    assert count == 2
