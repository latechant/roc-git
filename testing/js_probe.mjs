// Python 驱动实际 JS 模块；仅替换 uni 平台和网络边界，不复制业务函数。
import fs from 'node:fs'
const job = JSON.parse(fs.readFileSync(0, 'utf8'))
if (job.now) {
  const OriginalDate = Date
  globalThis.Date = class extends OriginalDate {
    constructor(...args) { super(...(args.length ? args : [job.now])) }
    static now() { return new OriginalDate(job.now).getTime() }
  }
}
let source = fs.readFileSync(new URL('../' + job.module, import.meta.url), 'utf8')
// 模拟 uni-app 的 MP-WEIXIN 条件编译，H5 分支不参与小程序单元测试。
const active = [true]
source = source.split('\n').filter(line => {
  const match = line.match(/\/\/\s*#(ifdef|ifndef)\s+(\S+)/)
  if (match) {
    const matches = match[2] === 'MP-WEIXIN'
    active.push(active.at(-1) && (match[1] === 'ifdef' ? matches : !matches))
    return false
  }
  if (/\/\/\s*#endif/.test(line)) { active.pop(); return false }
  return active.at(-1)
}).join('\n')
const storage = { ...(job.storage || {}) }
const events = []
globalThis.uni = {
  getStorageSync: k => storage[k],
  setStorageSync: (k, v) => { storage[k] = v },
  removeStorageSync: k => { delete storage[k] },
  reLaunch: v => events.push({ kind: 'redirect', ...v }),
  showToast: v => events.push({ kind: 'toast', ...v }),
  request: opts => {
    events.push({ kind: 'request', url: opts.url, method: opts.method, data: opts.data, header: opts.header })
    if (job.networkFail) opts.fail({ errMsg: 'network unavailable' })
    else opts.success({ statusCode: job.status ?? 200, data: job.body ?? { code: 0, data: { ok: true } } })
  }
}
const mod = await import('data:text/javascript;base64,' + Buffer.from(source).toString('base64'))
const target = job.module.endsWith('api.js') ? mod.default : mod
let value, error
try { value = await target[job.fn](...(job.args || [])) } catch (e) { error = e.message || e.errMsg }
console.log(JSON.stringify({ value: value ?? null, error, storage, events }))
