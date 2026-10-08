import { reactive } from 'vue'
import classMap from '../data/classes.js'

// Small state adapters retain the prototype's interaction semantics on both platforms.
// These objects are Vue data models, not DOM elements; all rendering uses native templates.
export function createPageController(data) {
  const ui = reactive(Object.fromEntries(data.records.map(record => [record.id, { ...JSON.parse(JSON.stringify(record)), appended: [] }])))
  const cache = new Map()
  const timers = new Set()
  let handlers = {}
  let nextId = 0
  const classes = value => (value || '').split(/\s+/).map(c => classMap[c] || c).join(' ')
  const liveRecords = () => Object.values(ui).filter(n => !n.removed)
  function matches(node, selector) {
    const tag = selector.match(/^[\w-]+/)?.[0]
    if (tag && node.tag !== tag) return false
    const id = selector.match(/#([\w-]+)/)?.[1]
    if (id && node.attrs.id !== id) return false
    return [...selector.matchAll(/\.([\w-]+)/g)].every(m => node.className.split(/\s+/).includes(m[1]))
  }
  function isDescendant(node, parentId) {
    let parent = node.parent
    while (parent) { if (parent === parentId) return true; parent = ui[parent]?.parent }
    return false
  }
  function query(selector, parentId = null) {
    const parts = selector.trim().split(/\s+/)
    return liveRecords().filter(node => {
      if (parentId && !isDescendant(node, parentId)) return false
      if (!matches(node, parts.at(-1))) return false
      let parent = node.parent
      for (let i = parts.length - 2; i >= 0; i--) {
        while (parent && !matches(ui[parent], parts[i])) parent = ui[parent].parent
        if (!parent) return false
        parent = ui[parent].parent
      }
      return true
    }).map(node => element(node.id))
  }
  function element(id) {
    if (!id || !ui[id]) return null
    if (cache.has(id)) return cache.get(id)
    const n = ui[id]
    const hasClass = c => n.className.split(/\s+/).includes(c)
    const toggleClass = (c, force) => {
      const enabled = force === undefined ? !hasClass(c) : force
      const tokens = new Set(n.className.split(/\s+/).filter(Boolean))
      if (enabled) tokens.add(c); else tokens.delete(c)
      n.className = [...tokens].join(' ')
      return enabled
    }
    const adapter = {
      get className() { return n.className }, set className(v) { n.className = v },
      get textContent() { return n.textContent ?? n.children.map(c => c.text || '').join('') }, set textContent(v) { n.textContent = String(v) },
      get value() { return n.value }, set value(v) { n.value = v },
      get style() { return n.style },
      get parentElement() { return element(n.parent) },
      get dataset() { return Object.fromEntries(Object.entries(n.attrs).filter(([k]) => k.startsWith('data-')).map(([k, v]) => [k.slice(5), v])) },
      classList: {
        add(...tokens) { tokens.forEach(c => toggleClass(c, true)) },
        remove(...tokens) { tokens.forEach(c => toggleClass(c, false)) },
        contains: hasClass, toggle: toggleClass
      },
      querySelector(s) { return query(s, id)[0] || null },
      querySelectorAll(s) { return query(s, id) },
      closest(s) { let current = n; while (current) { if (matches(current, s)) return element(current.id); current = ui[current.parent] } return null },
      remove() { n.removed = true },
      insertAdjacentHTML(position, html) {
        if (position !== 'beforeend') throw new Error('Unsupported insertion position')
        appendFragment(id, html)
      }
    }
    cache.set(id, adapter)
    return adapter
  }
  // Only the prototype's added form rows pass through this tokenizer. There is no
  // web-view, HTML renderer or executable string evaluation in the mini program.
  function appendFragment(parentId, html) {
    const stack = [parentId]
    for (const token of html.match(/<[^>]+>|[^<]+/g) || []) {
      if (token.startsWith('</')) { stack.pop(); continue }
      if (!token.startsWith('<')) { if (token.trim()) ui[stack.at(-1)].children.push({ text: token }); continue }
      const tag = token.match(/^<([\w-]+)/)?.[1]
      if (!tag) continue
      const attrs = Object.fromEntries([...token.matchAll(/([\w-]+)="([^"]*)"/g)].map(m => [m[1], m[2]]))
      // 解析内联 style="k:v;k:v" → 驼峰 style 对象（动态行进度条等需要）
      const style = {}
      if (attrs.style) {
        attrs.style.split(';').filter(Boolean).forEach(pair => {
          const idx = pair.indexOf(':')
          if (idx < 0) return
          const k = pair.slice(0, idx).trim(), v = pair.slice(idx + 1).trim()
          if (k) style[k.replace(/-([a-z])/g, (_, c) => c.toUpperCase())] = v
        })
      }
      const id = `added${nextId++}`, parent = stack.at(-1)
      ui[id] = { id, parent, tag, attrs, className: attrs.class || '', style, value: attrs.value || '', checked: false, removed: false, children: [], appended: [] }
      if (parent === parentId) ui[parentId].appended.push(id)
      else ui[parent].children.push({ id })
      if (!['input', 'br', 'img'].includes(tag)) stack.push(id)
    }
  }
  const document = {
    getElementById(id) { return element(liveRecords().find(n => n.attrs.id === id)?.id) },
    querySelectorAll: query,
    querySelector(s) { return query(s)[0] || null }
  }
  function navigate(href) {
    const name = href.replace(/\.html.*$/, '')
    const url = `/pages/${name}/${name}`
    if (['home', 'messages', 'profile'].includes(name)) uni.reLaunch({ url })
    else uni.navigateTo({ url })
  }
  const location = { set href(value) { navigate(value) } }
  const navigator = { clipboard: { writeText(text) { uni.setClipboardData({ data: text, showToast: false }) } } }
  function scheduled(callback, delay) {
    const id = globalThis.setTimeout(() => { timers.delete(id); callback() }, delay)
    timers.add(id)
    return id
  }
  function tap(id, nativeEvent) {
    let stopped = false, prevented = false
    const event = { nativeEvent, stopPropagation() { stopped = true }, preventDefault() { prevented = true } }
    let current = ui[id]
    while (current) {
      handlers[current.id]?.call(element(current.id), event)
      if (current.attrs.href && !prevented) navigate(current.attrs.href)
      if (stopped) break
      current = ui[current.parent]
    }
  }
  function iconSrc(id) {
    const n = ui[id]
    if (n.attrs.icon === 'lucide:monitor' && n.parent && ui[n.parent].className.includes('text-slate-600')) return '/static/icons/monitor--slate-600.png'
    return `/static/icons/${n.iconFile}.png`
  }
  return {
    ui, classes, tap, iconSrc,
    appendHtml: appendFragment,
    adapters: { document, location, navigator, setTimeout: scheduled },
    setHandlers(value) { handlers = value },
    dispose() { timers.forEach(id => globalThis.clearTimeout(id)); timers.clear() }
  }
}
