"""项目根目录执行：python run_tests.py [pytest 参数]。失败原样返回非零退出码。"""
import sys
import os
from pathlib import Path
import pytest

if __name__ == "__main__":
    root = Path(__file__).resolve().parent
    os.chdir(root)
    for stream in (sys.stdout, sys.stderr):
        if hasattr(stream, "reconfigure"):
            stream.reconfigure(encoding="utf-8", errors="replace")
    sys.exit(pytest.main(["-c", str(root / "pytest.ini"), *(sys.argv[1:] or ["-q"])]))
