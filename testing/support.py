import json
import pytest


def case(id, module, name, inputs, expected, method="黑盒", goal="功能", stage="集成测试"):
    return pytest.mark.case(id=id, module=module, name=name, inputs=inputs,
                            expected=expected, method=method, goal=goal, stage=stage)


def compact(value):
    """报告不记录登录凭证。"""
    if isinstance(value, dict):
        return {k: "<已脱敏>" if any(s in k.lower() for s in ("token", "password", "secret", "openid"))
                else compact(v) for k, v in value.items()}
    if isinstance(value, (list, tuple)):
        return [compact(x) for x in value]
    return value


def describe(value):
    return json.dumps(compact(value), ensure_ascii=False, default=str)[:6000]
