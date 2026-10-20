"""隔离后端小规模性能采样，不等同真机、Nginx或生产容量验收。"""
import concurrent.futures
import statistics
import time
import json
from pathlib import Path
import requests
import pytest
from testing.support import case

pytestmark = pytest.mark.remote


@pytest.mark.parametrize("concurrency,requests_count", [(1, 20), (10, 50), (100, 200)])
@case("PERF-HTTP", "接口性能", "健康接口并发响应采样", "并发1/10/100；请求20/50/200", "全部HTTP200且业务码0；记录TPS及P50/P95，不预设报告未证实指标", goal="性能", stage="系统测试")
def test_health_concurrency(lab, request, pytestconfig, concurrency, requests_count):
    if not pytestconfig.getoption("--load"):
        pytest.skip("性能采样需 --load")
    def one(_):
        start = time.perf_counter()
        with requests.Session() as session:
            session.trust_env = False
            response = session.get(lab.base + "/api/v1/health", timeout=20)
            return (time.perf_counter()-start)*1000, response.status_code == 200 and response.json().get("code") == 0
    one(0)
    start = time.perf_counter()
    with concurrent.futures.ThreadPoolExecutor(max_workers=concurrency) as pool:
        results = list(pool.map(one, range(requests_count)))
    seconds = time.perf_counter()-start
    times = sorted(t for t, _ in results)
    metrics = {"concurrency": concurrency, "requests": requests_count, "successful": sum(ok for _, ok in results),
        "seconds": round(seconds, 3), "TPS": round(requests_count/seconds, 2),
        "mean_ms": round(statistics.mean(times), 2), "P50_ms": round(statistics.median(times), 2),
        "P95_ms": round(times[max(0, int(len(times)*0.95)-1)], 2), "max_ms": round(max(times), 2),
        "scope": "健康接口，经SSH隧道，含网络及隧道延迟；不是业务接口容量结论"}
    request.node.observed = [metrics]
    out = Path(pytestconfig.getoption("--report-dir"))
    out.mkdir(parents=True, exist_ok=True)
    (out / f"performance-{concurrency}.json").write_text(json.dumps(metrics, ensure_ascii=False, indent=2), encoding="utf-8")
    assert metrics["successful"] == requests_count


@pytest.mark.parametrize("concurrency", [10, 100])
@case("PERF-PROJECT", "业务接口性能", "带鉴权项目详情并发采样", "并发10/100；200次真实项目详情查询", "200次业务响应均成功且projectId正确；记录TPS及P95", goal="性能", stage="系统测试")
def test_project_concurrency(api, world, lab, request, pytestconfig, concurrency):
    if not pytestconfig.getoption("--load"):
        pytest.skip("性能采样需 --load")
    url = lab.base + f'/api/v1/project/{world["pid"]}'
    headers = {"Authorization": "Bearer " + world["leader"]["token"]}
    count = 200
    def one(_):
        start = time.perf_counter()
        with requests.Session() as session:
            session.trust_env = False
            response = session.get(url, headers=headers, timeout=30)
            body = response.json()
            valid = response.status_code == 200 and body.get("code") == 0 and body.get("data", {}).get("projectId") == world["pid"]
            return (time.perf_counter()-start)*1000, valid
    one(0)
    start = time.perf_counter()
    with concurrent.futures.ThreadPoolExecutor(max_workers=concurrency) as pool:
        results = list(pool.map(one, range(count)))
    duration = time.perf_counter()-start
    times = sorted(t for t, _ in results)
    data = {"endpoint": "GET /api/v1/project/{projectId}", "concurrency": concurrency,
        "requests": count, "successful": sum(v for _, v in results), "seconds": round(duration, 3),
        "TPS": round(count/duration, 2), "P50_ms": round(statistics.median(times), 2),
        "P95_ms": round(times[int(count*0.95)-1], 2), "mean_ms": round(statistics.mean(times), 2),
        "scope": "实际JWT鉴权+MySQL项目详情查询；含SSH及网络延迟；单轮采样"}
    request.node.observed = [data]
    out = Path(pytestconfig.getoption("--report-dir"))
    (out / f"performance-project-{concurrency}.json").write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")
    assert data["successful"] == count
