"""预先加载CLI参数，避免SSH密钥的外部文件路径影响pytest的conftest发现。"""
import os


def pytest_addoption(parser):
    parser.addoption("--remote", action="store_true", help="创建隔离的远程测试数据库及实例")
    parser.addoption("--ssh-host", default=os.getenv("ROCGIT_TEST_SSH_HOST", "116.62.81.113"))
    parser.addoption("--ssh-key", default=os.getenv("ROCGIT_TEST_SSH_KEY", ""))
    parser.addoption("--ssh-user", default=os.getenv("ROCGIT_TEST_SSH_USER", "root"))
    parser.addoption("--report-dir", default="测试结果/latest")
    parser.addoption("--java", action="store_true", help="运行 Maven 白盒测试")
    parser.addoption("--load", action="store_true", help="隔离实例运行并发性能采样")
    parser.addoption("--build", action="store_true", help="运行微信小程序正式构建验证")
