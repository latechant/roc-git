/**
 * avatar.js - 昵称生成文字头像
 *
 * 取昵称首字符（中文取第一个汉字，英文取首字母大写），
 * 颜色由昵称 hash 从固定色板选取，保证同一昵称每次颜色一致。
 */

// 与 Tailwind 主题协调的底色板（500 档），文字统一白色
const PALETTE = [
  '#3b82f6', // blue
  '#6366f1', // indigo
  '#8b5cf6', // violet
  '#ec4899', // pink
  '#ef4444', // red
  '#f59e0b', // amber
  '#10b981', // emerald
  '#14b8a6'  // teal
]

/** 昵称首字符：中文取第一个字，英文取首字母大写，其余情况取首字符 */
export function avatarChar(name) {
  const s = String(name || '').trim()
  if (!s) return ''
  return s.charAt(0).toUpperCase()
}

/** 稳定颜色：昵称 hash 选色板 */
export function avatarColor(name) {
  const s = String(name || '')
  let h = 0
  for (let i = 0; i < s.length; i++) {
    h = (h * 31 + s.charCodeAt(i)) >>> 0
  }
  return PALETTE[h % PALETTE.length]
}

/** 文字头像数据：{ char, color } */
export function textAvatar(name) {
  return { char: avatarChar(name), color: avatarColor(name) }
}
