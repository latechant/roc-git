/**
 * 通用工具函数
 */

/**
 * 复制到剪贴板（兼容 H5 + 小程序）
 * @param {String} text
 * @returns {Promise<Boolean>}
 */
export function copyToClipboard(text) {
  return new Promise((resolve) => {
    // #ifdef H5
    const fallback = () => {
      try {
        const ta = document.createElement('textarea')
        ta.value = text
        ta.style.position = 'fixed'
        ta.style.opacity = '0'
        document.body.appendChild(ta)
        ta.select()
        document.execCommand('copy')
        document.body.removeChild(ta)
        resolve(true)
      } catch {
        resolve(false)
      }
    }
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(text)
        .then(() => resolve(true))
        .catch(fallback)
    } else {
      fallback()
    }
    // #endif

    // #ifndef H5
    uni.setClipboardData({
      data: text,
      success: () => resolve(true),
      fail: () => resolve(false),
    })
    // #endif
  })
}

/**
 * 显示 Toast
 * @param {String} msg
 * @param {Number} duration
 */
export function showToast(msg, duration = 2000) {
  // #ifdef H5
  if (typeof uni !== 'undefined' && uni.showToast) {
    uni.showToast({ title: msg, icon: 'none', duration })
    return
  }
  // H5 降级：用页面 toast 元素
  const el = document.getElementById('toast')
  if (el) {
    el.textContent = msg
    el.classList.remove('hidden')
    clearTimeout(el._timer)
    el._timer = setTimeout(() => el.classList.add('hidden'), duration)
  }
  // #endif
  // #ifndef H5
  uni.showToast({ title: msg, icon: 'none', duration })
  // #endif
}

/**
 * 防抖
 */
export function debounce(fn, delay = 300) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}

/**
 * 节流
 */
export function throttle(fn, delay = 300) {
  let last = 0
  return function (...args) {
    const now = Date.now()
    if (now - last >= delay) {
      last = now
      fn.apply(this, args)
    }
  }
}

/**
 * 生成唯一 ID（简单版）
 */
export function uid(prefix = 'id') {
  return prefix + '_' + Date.now().toString(36) + '_' + Math.random().toString(36).slice(2, 8)
}

/**
 * 深拷贝（简单场景）
 */
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  if (Array.isArray(obj)) return obj.map(deepClone)
  const res = {}
  for (const k in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, k)) {
      res[k] = deepClone(obj[k])
    }
  }
  return res
}
