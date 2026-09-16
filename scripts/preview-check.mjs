import fs from 'node:fs/promises'
import { readOriginal } from './original-source.mjs'
import { chromium } from 'playwright'
import postcss from 'postcss'
import tailwind from 'tailwindcss'

await fs.mkdir('artifacts', { recursive: true })
const names = ['home', 'project', 'create', 'meeting', 'messages', 'profile']
const sources = await Promise.all(names.map(readOriginal))
const css = (await postcss([tailwind({ content: sources.map(raw => ({ raw, extension: 'html' })) })]).process('@tailwind base;@tailwind utilities;', { from: undefined })).css
const icons = JSON.parse(await fs.readFile('node_modules/@iconify-json/lucide/icons.json', 'utf8'))
const iconScript = `customElements.define('iconify-icon',class extends HTMLElement{connectedCallback(){let icons=${JSON.stringify(icons)};let name=this.getAttribute('icon').replace('lucide:','');let icon=icons.icons[name]||icons.icons[icons.aliases?.[name]?.parent];this.style.display='inline-block';this.style.verticalAlign='middle';this.style.width=(this.getAttribute('width')||16)+'px';this.style.height=(this.getAttribute('width')||16)+'px';this.style.flexShrink='0';this.innerHTML='<svg xmlns="http://www.w3.org/2000/svg" width="100%" height="100%" viewBox="0 0 24 24">'+icon.body+'</svg>';}});`
const browser = await chromium.launch({ channel: 'msedge', headless: true })
const page = await browser.newPage({ viewport: { width: 393, height: 852 }, deviceScaleFactor: 1 })
const errors = []
page.on('pageerror', error => errors.push(error.message))
for (const [index, name] of names.entries()) {
  await page.route(`**/${name}.html`, route => route.fulfill({ contentType: 'text/html; charset=utf-8', body: sources[index] }))
}
await page.route('**/tailwindcss.js', route => route.fulfill({ contentType: 'application/javascript', body: `document.head.insertAdjacentHTML('beforeend', '<style>'+${JSON.stringify(css)}+'</style>')` }))
await page.route('**/iconify-icon.min.js', route => route.fulfill({ contentType: 'application/javascript', body: iconScript }))
const report = {}
for (const name of names) {
  const extract = () => [...document.querySelectorAll('h1,h2,h3,p,uni-view.original-h1,uni-view.original-h2,uni-view.original-h3,uni-view.original-p')].filter(n => n.getClientRects().length).map(n => { const r = n.getBoundingClientRect(); const c = getComputedStyle(n); return { text: n.innerText, x: Math.round(r.x), y: Math.round(r.y), w: Math.round(r.width), h: Math.round(r.height), font: c.fontSize, line: c.lineHeight } })
  await page.goto(`http://127.0.0.1:5173/${name}.html`)
  await page.waitForTimeout(250)
  const original = await page.evaluate(extract)
  const formExtract = () => [...document.querySelectorAll('input,select,textarea,label,uni-input.original-input,uni-textarea.original-textarea,uni-picker.original-select,uni-picker.original-date,uni-view.original-label')].filter(n => n.getClientRects().length).map(n => { const r = n.getBoundingClientRect(); const c = getComputedStyle(n); return { tag:n.tagName, text: n.innerText || n.value, x:r.x,y:r.y,w:r.width,h:r.height,display:c.display,line:c.lineHeight,padding:c.padding,margin:c.margin } })
  const originalForms = await page.evaluate(formExtract)
  await page.screenshot({ path: `artifacts/${name}-original.png` })
  await page.goto(`http://127.0.0.1:5173/#/pages/${name}/${name}`)
  await page.waitForSelector('.original-h1', { timeout: 30000 })
  await page.waitForTimeout(250)
  const migrated = await page.evaluate(extract)
  const migratedForms = await page.evaluate(formExtract)
  await page.screenshot({ path: `artifacts/${name}-uniapp.png` })
  report[name] = { original, migrated, originalForms, migratedForms }
}
await fs.writeFile('artifacts/layout-report.json', JSON.stringify({ report, errors }, null, 2))
console.log(JSON.stringify({ errors, counts: Object.fromEntries(names.map(n => [n, [report[n].original.length, report[n].migrated.length]])) }, null, 2))
await browser.close()
