import assert from 'node:assert/strict'
import { readOriginal } from './original-source.mjs'
import fs from 'node:fs/promises'
import { parse } from 'parse5'
import { createPageController } from '../src/utils/page-controller.js'

const names = ['home', 'project', 'create', 'meeting', 'messages', 'profile']
const find = (n, tag) => n.tagName === tag ? n : (n.childNodes || []).map(c => find(c, tag)).find(Boolean)
const classes = JSON.parse(await fs.readFile('src/data/classes.json', 'utf8'))
for (const name of names) {
  const html = await readOriginal(name)
  const data = JSON.parse(await fs.readFile(`src/data/${name}.json`, 'utf8'))
  const vue = await fs.readFile(`src/pages/${name}/${name}.vue`, 'utf8')
  const original = []
  const walk = n => {
    if (n.nodeName === '#comment' || n.tagName === 'script') return
    if (n.tagName) original.push({ tag: n.tagName, attrs: Object.fromEntries(n.attrs.map(a => [a.name, a.value])) })
    n.childNodes?.forEach(walk)
  }
  find(parse(html), 'body').childNodes.forEach(walk)
  assert.equal(data.records.length, original.length, `${name}: every original element retained`)
  original.forEach((n, i) => assert.deepEqual({ tag: data.records[i].tag, attrs: data.records[i].attrs }, n, `${name}: original attributes retained`))
  const lookup = Object.fromEntries(data.records.map(n => [n.id, n]))
  const retainedText = []
  const collect = refs => refs.forEach(r => { if ('text' in r) retainedText.push(r.text); else collect(lookup[r.id].children) })
  collect(data.roots)
  const originalText = []
  const textWalk = n => { if (n.nodeName === '#comment' || n.tagName === 'script') return; if (n.nodeName === '#text' && n.value.trim()) originalText.push(n.value.replace(/\s+/g, ' ')); n.childNodes?.forEach(textWalk) }
  find(parse(html), 'body').childNodes.forEach(textWalk)
  assert.deepEqual(retainedText, originalText, `${name}: all text unchanged, including hidden dialogs`)
  assert(!/<(?:div|a|header|nav|select|option|iconify-icon)\b/.test(vue), `${name}: native uni-app tags`)
  assert(!/\bonclick=|<web-view|v-html|\beval\(/.test(vue), `${name}: no HTML wrapper or inline execution`)
  for (const n of data.records.filter(n => n.iconFile)) await fs.access(`src/static/icons/${n.iconFile}.png`)
}

const data = JSON.parse(await fs.readFile('src/data/project.json', 'utf8'))
const state = createPageController(data)
const doc = state.adapters.document
const filters = doc.getElementById('filterModal')
filters.classList.remove('hidden')
assert(!filters.classList.contains('hidden'))
filters.classList.toggle('hidden', true)
assert(filters.classList.contains('hidden'))
doc.getElementById('confirmInput').value = '清理'
assert.equal(doc.getElementById('confirmInput').value, '清理')
const row = doc.querySelectorAll('.file-row')[0]
assert(row.querySelector('.batch-check'))
row.remove()
assert.equal(doc.querySelectorAll('.file-row').length, data.records.filter(n => n.className.split(' ').includes('file-row')).length - 1)
assert(state.classes('bg-blue-600 text-white').includes(classes['bg-blue-600']))
state.dispose()

const createData = JSON.parse(await fs.readFile('src/data/create.json', 'utf8'))
const create = createPageController(createData)
create.adapters.document.getElementById('members').insertAdjacentHTML('beforeend', '<div class="flex gap-2"><span>新增成员</span><input value="任务"></div>')
const members = Object.values(create.ui).find(n => n.attrs.id === 'members')
assert.equal(members.appended.length, 1)
const added = create.ui[members.appended[0]]
assert.equal(create.ui[added.children[0].id].children[0].text, '新增成员')
assert.equal(create.ui[added.children[1].id].value, '任务')
create.dispose()
console.log('PASS: 6 pages retain all original text, attributes, local icons, and reactive interaction state.')
