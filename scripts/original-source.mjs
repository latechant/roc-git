import fs from 'node:fs/promises'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

// Original HTML lives in the desktop backup, outside the uni-app project.
// Set ORIGINAL_HTML_DIR if the backup is moved or used on another machine.
const projectRoot = fileURLToPath(new URL('../', import.meta.url))
export const originalDirectory = process.env.ORIGINAL_HTML_DIR || path.resolve(projectRoot, '../快速UI-原始HTML备份-20260916-180046')
export function readOriginal(name) {
  return fs.readFile(path.join(originalDirectory, `${name}.html`), 'utf8')
}
