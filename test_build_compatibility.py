"""编译层兼容性验证；不等同微信真机兼容性验收。"""
import json
import shutil
import subprocess
from pathlib import Path
import pytest
from testing.support import case

ROOT = Path(__file__).parent


@pytest.fixture(scope="module")
def mp_build(pytestconfig):
    if not pytestconfig.getoption("--build"):
        pytest.skip("小程序构建需 --build 和 npm ci")
    npm = shutil.which("npm.cmd") or shutil.which("npm")
    assert npm, "需要Node/npm"
    result = subprocess.run([npm, "run", "build:mp-weixin"], cwd=ROOT,
        capture_output=True, encoding="utf-8", errors="replace", timeout=300)
    out = ROOT / pytestconfig.getoption("--report-dir")
    out.mkdir(parents=True, exist_ok=True)
    (out / "build-mp-weixin.log").write_text(result.stdout + result.stderr, encoding="utf-8")
    assert result.returncode == 0, result.stdout[-3000:] + result.stderr[-2000:]
    return ROOT / "dist/build/mp-weixin"


@case("BUILD-MP-01", "小程序构建", "正式构建成功且页面注册一致", "npm run build:mp-weixin", "构建成功；7个页面注册一致；关键编译产物存在", "灰盒", "兼容性", "系统测试")
def test_mp_build_routes(mp_build, request):
    source = json.loads((ROOT / "src/pages.json").read_text(encoding="utf-8"))
    actual = json.loads((mp_build / "app.json").read_text(encoding="utf-8"))
    expected = [p["path"] for p in source["pages"]]
    request.node.observed = [{"pages": actual["pages"], "output": str(mp_build)}]
    assert actual["pages"] == expected
    for page in expected:
        assert (mp_build / (page + ".wxml")).exists()
        assert (mp_build / (page + ".js")).exists()


@case("BUILD-MP-02", "消息页更新", "最新消息页按钮及图标进入产物", "GitHub27f0db7的标题行已读按钮", "wxml含标题行；JS含check-check图标属性；编译成功", "灰盒", "兼容性", "回归测试")
def test_latest_message_ui_compiled(mp_build, request):
    template = (mp_build / "pages/messages/messages.wxml").read_text(encoding="utf-8")
    script = (mp_build / "pages/messages/messages.js").read_text(encoding="utf-8")
    request.node.observed = [{"title_row": "msg-header__title-row" in template, "new_icon": "check-check" in script}]
    assert "msg-header__title-row" in template
    assert "check-check" in script
