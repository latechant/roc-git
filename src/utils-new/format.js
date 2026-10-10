/**
 * 格式化工具函数
 */

/**
 * 格式化文件大小
 * @param {Number} bytes 字节数
 * @returns {String} 如 "1.2 MB"
 */
export function formatFileSize(bytes) {
  if (!bytes && bytes !== 0) return '-'
  if (bytes === 0) return '0 B'
  const k = 1024
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(i === 0 ? 0 : 1) + ' ' + units[i]
}

/**
 * 格式化日期
 * @param {String|Number|Date} date
 * @param {String} format 'YYYY-MM-DD' | 'YYYY-MM-DD HH:mm' | 'MM-DD' | 'HH:mm'
 * @returns {String}
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  const d = new Date(date)
  if (isNaN(d.getTime())) return String(date)

  const pad = (n) => String(n).padStart(2, '0')
  const year = d.getFullYear()
  const month = pad(d.getMonth() + 1)
  const day = pad(d.getDate())
  const hour = pad(d.getHours())
  const minute = pad(d.getMinutes())

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
}

/**
 * 相对时间（刚刚 / X 分钟前 / X 小时前 / X 天前 / 更早显示日期）
 * @param {String|Number|Date} date
 * @returns {String}
 */
export function timeAgo(date) {
  if (!date) return ''
  const d = new Date(date)
  if (isNaN(d.getTime())) return String(date)

  const now = Date.now()
  const diff = now - d.getTime()
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < minute) return '刚刚'
  if (diff < hour) return Math.floor(diff / minute) + ' 分钟前'
  if (diff < day) return Math.floor(diff / hour) + ' 小时前'
  if (diff < 7 * day) return Math.floor(diff / day) + ' 天前'
  return formatDate(date, 'YYYY-MM-DD')
}

/**
 * 时间桶分组（今天 / 昨天 / 更早）
 * @param {Array} list 消息列表
 * @param {String} timeField 时间字段名，默认 'createTime'
 * @returns {Array<{ label: String, items: Array }>}
 */
export function groupByTimeBucket(list, timeField = 'createTime') {
  if (!Array.isArray(list) || !list.length) return []

  const now = new Date()
  const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime()
  const yesterdayStart = todayStart - 24 * 60 * 60 * 1000

  const buckets = {
    today: { label: '今天', items: [] },
    yesterday: { label: '昨天', items: [] },
    earlier: { label: '更早', items: [] },
  }

  list.forEach(item => {
    const t = new Date(item[timeField]).getTime()
    if (t >= todayStart) buckets.today.items.push(item)
    else if (t >= yesterdayStart) buckets.yesterday.items.push(item)
    else buckets.earlier.items.push(item)
  })

  return Object.values(buckets).filter(b => b.items.length > 0)
}

/**
 * 文件扩展名
 * @param {String} filename
 * @returns {String} 小写扩展名，不含点
 */
export function fileExt(filename) {
  if (!filename) return ''
  const idx = filename.lastIndexOf('.')
  return idx >= 0 ? filename.slice(idx + 1).toLowerCase() : ''
}

/**
 * 文件图标 emoji（根据扩展名）
 * @param {String} filename
 * @returns {String} emoji
 */
export function fileIconEmoji(filename) {
  const ext = fileExt(filename)
  const map = {
    // 文档
    'pdf': '📕',
    'doc': '📘', 'docx': '📘',
    'ppt': '📙', 'pptx': '📙',
    'xls': '📗', 'xlsx': '📗',
    'txt': '📄', 'md': '📝',
    // 图片
    'jpg': '🖼️', 'jpeg': '🖼️', 'png': '🖼️', 'gif': '🖼️', 'webp': '🖼️', 'svg': '🖼️',
    // 视频
    'mp4': '🎬', 'mov': '🎬', 'avi': '🎬', 'mkv': '🎬',
    // 音频
    'mp3': '🎵', 'wav': '🎵', 'm4a': '🎵',
    // 压缩
    'zip': '📦', 'rar': '📦', '7z': '📦', 'tar': '📦', 'gz': '📦',
    // 代码
    'js': '💻', 'ts': '💻', 'vue': '💻', 'html': '💻', 'css': '💻', 'json': '💻', 'py': '💻', 'java': '💻',
  }
  return map[ext] || '📄'
}

/**
 * 阶段状态 → 徽章类型映射
 * @param {String} status 'pending' | 'ongoing' | 'completed'
 * @returns {String} badge type
 */
export function stageStatusBadge(status) {
  const map = {
    pending: 'default',
    ongoing: 'primary',
    completed: 'success',
  }
  return map[status] || 'default'
}

/**
 * 阶段状态文本
 */
export function stageStatusText(status) {
  const map = {
    pending: '待开始',
    ongoing: '进行中',
    completed: '已完成',
  }
  return map[status] || status
}

/**
 * 文件审核状态 → 徽章类型
 */
export function fileStatusBadge(status) {
  const map = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    submitted: 'warning',
  }
  return map[status] || 'default'
}

/**
 * 文件审核状态文本
 */
export function fileStatusText(status) {
  const map = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已驳回',
    submitted: '待审核',
  }
  return map[status] || status
}
