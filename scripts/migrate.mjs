// One-time migration tooling. Original HTML is read from the desktop backup.
import { readOriginal } from './original-source.mjs'
import fs from 'node:fs/promises'
import path from 'node:path'
import { parse, parseFragment } from 'parse5'
import postcss from 'postcss'
import tailwind from 'tailwindcss'
import selectorParser from 'postcss-selector-parser'
import colors from 'tailwindcss/colors.js'
import sharp from 'sharp'

const names = ['home', 'project', 'create', 'meeting', 'messages', 'profile']
const sources = await Promise.all(names.map(readOriginal))
const mkdir = p => fs.mkdir(p, { recursive: true })
await Promise.all(['src/pages', 'src/styles', 'src/controllers', 'src/data', 'src/static/icons'].map(mkdir))
const write = (p, data) => fs.writeFile(p, data, 'utf8')
const attrs = n => Object.fromEntries((n.attrs || []).map(a => [a.name, a.value]))
const find = (n, tag) => n.tagName === tag ? n : (n.childNodes || []).map(c => find(c, tag)).find(Boolean)
const escape = s => s.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;')
const attrEscape = s => escape(s).replaceAll('"', '&quot;')
const iconUses = new Map()

for (const [pageIndex, name] of names.entries()) {
  const source = sources[pageIndex]
  const body = find(parse(source), 'body')
  const records = []
  const handlers = {}
  let counter = 0
  const collect = (node, parent = null) => {
    if (node.nodeName === '#comment' || node.tagName === 'script') return null
    if (node.nodeName === '#text') return node.value.trim() ? { text: node.value.replace(/\s+/g, ' ') } : null
    const a = attrs(node), id = `n${counter++}`
    const record = { id, tag: node.tagName, parent, attrs: a, className: a.class || '', style: Object.fromEntries((a.style || '').split(';').filter(Boolean).map(s => { const i = s.indexOf(':'); return [s.slice(0, i).trim(), s.slice(i + 1).trim()] })), removed: false, value: a.value || '', checked: 'checked' in a, children: [] }
    records.push(record)
    if (a.onclick) handlers[id] = a.onclick
    record.children = (node.childNodes || []).map(c => collect(c, id)).filter(Boolean)
    if (record.tag === 'textarea') record.value = record.children.map(c => c.text || '').join('')
    return { id }
  }
  const roots = body.childNodes.map(n => collect(n)).filter(Boolean)
  const lookup = Object.fromEntries(records.map(n => [n.id, n]))
  // Resolve original icon colour, including inherited text utility classes.
  const iconColour = n => {
    let cursor = n
    while (cursor) {
      const token = cursor.className.split(/\s+/).find(t => /^text-(white|black|[a-z]+-\d+)(\/\d+)?$/.test(t))
      if (token) return token.slice(5)
      cursor = lookup[cursor.parent]
    }
    return 'black'
  }
  for (const n of records.filter(n => n.tag === 'iconify-icon')) {
    const colour = iconColour(n), icon = n.attrs.icon.replace('lucide:', '')
    const key = `${icon}--${colour.replace('/', '-')}`
    n.iconFile = key
    iconUses.set(key, { icon, colour, fill: n.attrs.fill })
  }
  const render = ref => {
    if ('text' in ref) return `<text>${escape(ref.text)}</text>`
    const n = lookup[ref.id], a = n.attrs, state = `ui.${n.id}`
    const binding = `v-if="!${state}.removed" :class="classes(${state}.className)" :style="${state}.style"`
    let tap = a.onclick || a.href ? ` @tap.stop="tap('${n.id}', $event)"` : ''
    const checkbox = n.tag === 'label' && n.children.map(c => lookup[c.id]).find(c => c?.tag === 'input' && c.attrs.type === 'checkbox')
    if (checkbox) tap = ` @tap.stop="ui.${checkbox.id}.checked = !ui.${checkbox.id}.checked"`
    let tag = ['span', 'label', 'p', 'h1', 'h2', 'h3'].includes(n.tag) ? 'view' : ['input', 'textarea', 'button'].includes(n.tag) ? n.tag : 'view'
    let extra = ` class="original-${n.tag}"`
    if (n.tag === 'iconify-icon') return `<image ${binding}${tap} class="original-icon" :src="iconSrc('${n.id}')" style="width:${a.width || 16}px;height:${a.width || 16}px" mode="aspectFit" />`
    if (n.tag === 'br') return "<text class=\"original-break\">{{ '\\n' }}</text>"
    if (n.tag === 'input' && a.type === 'checkbox') return `<view ${binding} class="original-checkbox" @tap.stop="ui.${n.id}.checked = !ui.${n.id}.checked"><image v-if="${state}.checked" src="/static/icons/check--white.png" style="width:100%;height:100%;background:#2563eb;border-radius:3px" /><view v-else class="checkbox-empty" /></view>`
    if (n.tag === 'input' && a.type === 'date') return `<!-- #ifdef H5 -->\n<NativeDate ${binding} v-model:value="${state}.value" />\n<!-- #endif -->\n<!-- #ifndef H5 -->\n<picker ${binding} class="original-date" mode="date" :value="${state}.value" @change="${state}.value = $event.detail.value"><view>{{ ${state}.value ? ${state}.value.replaceAll('-', '/') : '年/月/日' }}</view></picker>\n<!-- #endif -->`
    if (n.tag === 'select') {
      n.options = n.children.map(r => lookup[r.id].children.map(c => c.text || '').join(''))
      n.value = n.options[0]
      return `<picker ${binding} class="original-select" :range="${state}.options" @change="${state}.value = ${state}.options[$event.detail.value]"><view>{{ ${state}.value }}</view></picker>`
    }
    if (['input', 'textarea'].includes(n.tag)) {
      extra += ` :value="${state}.value" @input="${state}.value = $event.detail.value" placeholder="${attrEscape(a.placeholder || '')}" :disabled="${Boolean(a.readonly)}" :maxlength="-1"`
      if (tag === 'textarea') extra += ' :auto-height="false" :fixed="true" :show-confirm-bar="false"'
      return `<${tag} ${binding}${extra}${tap}${tag === 'input' ? ' />' : `></${tag}>`}`
    }
    let children = n.children.map(render).join('\n')
    // textContent assignments (toast and counters) replace original children.
    children = `<template v-if="${state}.textContent !== undefined"><text>{{ ${state}.textContent }}</text></template><template v-else>\n${children}\n</template>`
    if (['members', 'stages', 'tasks', 'updates'].includes(a.id) && ['create', 'meeting'].includes(name)) children += `\n<NativeFragment v-for="childId in ${state}.appended" :key="childId" :node-id="childId" />`
    if (a.class?.includes('overflow-y-auto') && a.class.includes('flex-1')) {
      tag = 'scroll-view'; extra = ' class="original-scroll" scroll-y :show-scrollbar="false"'
    } else if (a.class?.includes('overflow-x-auto')) {
      tag = 'scroll-view'; extra = ' class="original-horizontal-scroll" scroll-x :show-scrollbar="false"'
      if (a.class.split(/\s+/).includes('flex')) {
        const gap = a.class.match(/\bgap-([\d.]+)/)?.[1] || 0
        children = `<view class="horizontal-content" style="gap:${Number(gap) * 4}px">${children}</view>`
      }
    }
    return `<${tag} ${binding}${extra}${tap}>\n${children}\n</${tag}>`
  }
  const template = roots.map(render).join('\n')
  await mkdir(`src/pages/${name}`)
  await write(`src/pages/${name}/${name}.vue`, `<template>\n<view class="prototype-screen">\n${template}\n</view>\n</template>\n\n<script setup>\nimport { provide } from 'vue'\nimport { onUnload } from '@dcloudio/uni-app'\nimport NativeFragment from '../../components/NativeFragment.vue'\n// #ifdef H5\nimport NativeDate from '../../components/NativeDate.vue'\n// #endif\nimport createController from '../../controllers/${name}.js'\nconst controller = createController()\nconst { ui, classes, tap, iconSrc } = controller\nprovide('pageController', controller)\nonUnload(controller.dispose)\n</script>\n`)
  await write(`src/data/${name}.json`, JSON.stringify({ records, roots }, null, 2))
  let script = [...source.matchAll(/<script>([\s\S]*?)<\/script>/g)].map(m => m[1]).join('\n')
  // Native modal is asynchronous; preserve the original confirmation text.
  script = script.replace("if(confirm('确定删除此阶段？')){el.closest('.bg-white').remove();showToast('已删除')}", "uni.showModal({content:'确定删除此阶段？',success(result){if(result.confirm){el.closest('.bg-white').remove();showToast('已删除')}}})")
  // This entry invokes a missing function in the supplied home.html.
  if (name === 'home') script += '\nfunction showFileSearch() {}\n'
  const handlerCode = Object.entries(handlers).map(([id, code]) => `${id}: function(event) { ${code} }`).join(',\n')
  await write(`src/controllers/${name}.js`, `import data from '../data/${name}.json'\nimport { createPageController } from '../utils/page-controller.js'\n\n// Original prototype actions operate on reactive Vue state through adapters.\n// No browser DOM, inline JavaScript or eval is required in the mini program.\nexport default function () {\n  const controller = createPageController(data)\n  const { document, location, navigator, setTimeout } = controller.adapters\n  ${script}\n  controller.setHandlers({\n${handlerCode}\n  })\n  return controller\n}\n`)
}

const pageTitles = ['ROC-git', '项目协作空间', '创建项目', '研讨记录', '协作消息', '个人空间']
await write('src/pages.json', JSON.stringify({ pages: names.map((n, i) => ({ path: `pages/${n}/${n}`, style: { navigationStyle: 'custom', navigationBarTitleText: pageTitles[i], disableScroll: true } })), globalStyle: { navigationStyle: 'custom', backgroundColor: '#F8FAFC', backgroundTextStyle: 'light' } }, null, 2))

// Tailwind runs only during migration. Compiled CSS and safe class names are local.
const classSet = new Set()
for (const source of sources) {
  for (const m of source.matchAll(/class=["']([^"']+)["']/g)) for (const token of m[1].split(/\s+/)) classSet.add(token)
  for (const m of source.matchAll(/["']([^"']+)["']/g)) for (const token of m[1].split(/\s+/)) if (/^[a-z-]+(?:-|\[)/.test(token)) classSet.add(token)
}
for (const c of ['sky','rose','emerald']) for (const shade of [400,900]) for (const type of ['bg','text']) classSet.add(`${type}-${c}-${shade}`)
const result = await postcss([tailwind({ content: sources.map(raw => ({ raw, extension: 'html' })), safelist: [...classSet], corePlugins: { preflight: false } })]).process('@tailwind base;\n@tailwind utilities;', { from: undefined })
const mapping = {}
let index = 0
const safe = name => mapping[name] ||= `u${index++}`
result.root.walkRules(rule => {
  rule.selector = selectorParser(selectors => {
    selectors.walkClasses(c => { c.value = safe(c.value) })
  }).processSync(rule.selector)
  // Mini-program selectors cannot use HTML attribute selectors or universal children.
  rule.selector = rule.selector.replace(/>\s*:not\(\[hidden\]\)\s*~\s*:not\(\[hidden\]\)/g, '> :nth-child(n+2)').replace(/>\s*\*/g, '> view')
  if (rule.selector === '*, ::before, ::after') rule.selector = 'page, view, text, button, input, textarea, picker, scroll-view, image, view::before, view::after'
})
result.root.walkDecls(d => { d.value = d.value.replace(/(-?[\d.]+)rem\b/g, (_, v) => `${Number(v) * 16}px`) })
await write('src/styles/utilities.css', result.root.toString())
await write('src/data/classes.json', JSON.stringify(mapping, null, 2))
await write('src/data/classes.js', `export default ${JSON.stringify(mapping, null, 2)}\n`)

const iconData = JSON.parse(await fs.readFile('node_modules/@iconify-json/lucide/icons.json', 'utf8'))
iconUses.set('check--white', { icon: 'check', colour: 'white' })
iconUses.set('monitor--slate-600', { icon: 'monitor', colour: 'slate-600' })
for (const [key, use] of iconUses) {
  let icon = iconData.icons[use.icon]
  if (!icon && iconData.aliases?.[use.icon]) icon = iconData.icons[iconData.aliases[use.icon].parent]
  if (!icon) throw new Error(`Missing icon: ${use.icon}`)
  const [token, alpha] = use.colour.split('/')
  const [family, shade] = token.split('-')
  const colour = shade ? colors[family][shade] : token
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="72" height="72" viewBox="0 0 ${icon.width || 24} ${icon.height || 24}" color="${colour}" opacity="${alpha ? Number(alpha) / 100 : 1}">${icon.body}</svg>`
  await sharp(Buffer.from(svg)).png().toFile(path.join('src/static/icons', `${key}.png`))
}
console.log(`Migrated ${names.length} pages; ${Object.keys(mapping).length} local utilities; ${iconUses.size} local icon variants.`)
