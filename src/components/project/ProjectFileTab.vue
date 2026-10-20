<!--
  项目详情 - 文件 Tab 组件（新架构版本）
  功能：文件列表 / 上传 / 筛选 / 批量操作 / 版本历史 / 文件菜单
  Props: projectId
-->
<template>
  <view class="file-tab">
    <!-- ========== 顶部操作栏 ========== -->
    <view class="file-toolbar">
      <view class="file-toolbar__search" @tap="onSearchTap">
        <app-icon name="search" :size="14" color="#94a3b8" />
        <text class="file-toolbar__search-ph">搜索文件名</text>
      </view>
      <view class="file-toolbar__actions">
        <view
          class="file-toolbar__btn"
          :class="{ 'file-toolbar__btn--on': batchMode }"
          @tap="toggleBatch"
        >
          <app-icon name="square" :size="16" :color="batchMode ? '#2563eb' : '#64748b'" />
        </view>
        <view class="file-toolbar__btn file-toolbar__btn--filter" @tap="showFilter = true">
          <app-icon name="settings" :size="16" color="#64748b" />
        </view>
        <view class="file-toolbar__btn file-toolbar__btn--primary" @tap="showUpload = true">
          <text class="file-toolbar__btn-icon">+</text>
        </view>
      </view>
    </view>

    <!-- ========== 文件列表 ========== -->
    <scroll-view class="file-list" scroll-y :show-scrollbar="false" @scrolltolower="onLoadMore">
      <view v-if="loading && files.length === 0" class="loading-state">
        <text>加载中...</text>
      </view>

      <view v-else-if="files.length === 0" class="empty-state">
        <app-icon name="folder" :size="42" color="#cbd5e1" />
        <text class="empty-state__text">暂无文件</text>
        <view class="empty-state__btn" @tap="showUpload = true">
          <text>上传文件</text>
        </view>
      </view>

      <view v-else class="file-list__inner">
        <FileItem
          v-for="f in files"
          :key="f.fileId"
          :file="f"
          :batch-mode="batchMode"
          :selected="selectedIds.includes(f.fileId)"
          @select="toggleSelect(f.fileId)"
          @menu="openFileMenu(f)"
          @open="onDownload(f)"
          @approve="onApprove(f)"
          @reject="onReject(f)"
          @resubmit="onResubmit(f)"
          @view-version="onViewVersion(f)"
        />
      </view>

      <view class="bottom-spacing"></view>
    </scroll-view>

    <!-- ========== 批量操作栏 ========== -->
    <view v-if="batchMode" class="batch-bar">
      <text class="batch-bar__count">已选 {{ selectedIds.length }} 个</text>
      <view class="batch-bar__actions">
        <view class="batch-bar__btn" @tap="onBatchMove">
          <text>移动</text>
        </view>
        <view class="batch-bar__btn batch-bar__btn--danger" @tap="showBatchConfirm = true">
          <text>删除</text>
        </view>
      </view>
    </view>

    <!-- ========== 上传弹窗 ========== -->
    <view v-if="showUpload" class="modal-mask" @tap="showUpload = false">
      <view class="modal-sheet" @tap.stop>
        <view class="modal-sheet__header">
          <text class="modal-sheet__title">上传文件</text>
          <view class="modal-sheet__close" @tap="showUpload = false"><app-icon name="x" :size="18" color="#94a3b8" /></view>
        </view>
        <view class="upload-body">
          <view class="upload-area" @tap="chooseFile">
            <app-icon name="upload-cloud" :size="36" color="#94a3b8" />
            <text class="upload-area__text">点击选择文件</text>
            <text class="upload-area__hint">文档从微信聊天选，图片从相册选</text>
          </view>
          <view class="upload-meta">
            <text class="upload-meta__label">上传到阶段</text>
            <picker :range="stageNames" @change="onStageChange" :value="uploadStageIdx">
              <view class="upload-meta__value">
                {{ stageNames[uploadStageIdx] || '请选择' }}
                <text class="upload-meta__arrow">›</text>
              </view>
            </picker>
          </view>
          <view class="upload-btn" :class="{ 'upload-btn--disabled': !uploading }" @tap="doUpload">
            <text>{{ uploading ? '上传中...' : '确认上传' }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- ========== 筛选弹窗 ========== -->
    <view v-if="showFilter" class="modal-mask" @tap="applyFilter">
      <view class="modal-sheet" @tap.stop>
        <view class="modal-sheet__header">
          <text class="modal-sheet__title">筛选</text>
          <view class="modal-sheet__close" @tap="showFilter = false"><app-icon name="x" :size="18" color="#94a3b8" /></view>
        </view>
        <scroll-view class="filter-body" scroll-y>
          <view class="filter-group">
            <text class="filter-group__title">文件格式</text>
            <view class="chip-row">
              <view
                v-for="fmt in formatOptions"
                :key="fmt"
                class="chip"
                :class="{ 'chip--active': filterFormats.includes(fmt) }"
                @tap="toggleChip('format', fmt)"
              >
                {{ fmt }}
              </view>
            </view>
          </view>
          <view class="filter-group">
            <text class="filter-group__title">所属阶段</text>
            <view class="chip-row">
              <view
                v-for="s in stages"
                :key="s.stageId"
                class="chip"
                :class="{ 'chip--active': filterStageIds.includes(s.stageId) }"
                @tap="toggleChip('stage', s.stageId)"
              >
                {{ s.name }}
              </view>
            </view>
          </view>
          <view class="filter-group">
            <text class="filter-group__title">上传者</text>
            <view class="chip-row">
              <view
                v-for="m in members"
                :key="m.userId"
                class="chip"
                :class="{ 'chip--active': filterUploaderIds.includes(m.userId) }"
                @tap="toggleChip('uploader', m.userId)"
              >
                {{ m.nickname }}
              </view>
            </view>
          </view>
          <view class="filter-group">
            <text class="filter-group__title">状态</text>
            <view class="chip-row">
              <view
                v-for="st in statusOptions"
                :key="st.value"
                class="chip"
                :class="{ 'chip--active': filterStatuses.includes(st.value) }"
                @tap="toggleChip('status', st.value)"
              >
                {{ st.label }}
              </view>
            </view>
          </view>
        </scroll-view>
        <view class="filter-footer">
          <view class="filter-footer__btn filter-footer__btn--ghost" @tap="clearFilter">
            <text>重置</text>
          </view>
          <view class="filter-footer__btn filter-footer__btn--primary" @tap="applyFilter">
            <text>应用筛选</text>
          </view>
        </view>
      </view>
    </view>

    <!-- ========== 文件菜单 ========== -->
    <view v-if="showMenu" class="modal-mask" @tap="showMenu = false">
      <view class="modal-sheet modal-sheet--sm" @tap.stop>
        <view class="menu-list">
          <view class="menu-list__item" @tap="onDownload">
            <app-icon name="download" :size="16" color="#2563eb" />
            <text>下载/查看</text>
          </view>
          <view class="menu-list__item" @tap="onFavorite">
            <app-icon name="star" :size="16" color="#f59e0b" />
            <text>收藏</text>
          </view>
          <view class="menu-list__item" @tap="onMove">
            <app-icon name="folder-open" :size="16" color="#0f172a" />
            <text>移动到</text>
          </view>
          <view class="menu-list__item menu-list__item--danger" @tap="onDelete">
            <app-icon name="trash-2" :size="16" color="#dc2626" />
            <text>删除</text>
          </view>
        </view>
        <view class="menu-list__cancel" @tap="showMenu = false">
          <text>取消</text>
        </view>
      </view>
    </view>

    <!-- ========== 版本历史弹窗 ========== -->
    <view v-if="showVersion" class="modal-mask" @tap="showVersion = false">
      <view class="modal-sheet" @tap.stop>
        <view class="modal-sheet__header">
          <text class="modal-sheet__title">版本历史</text>
          <view class="modal-sheet__close" @tap="showVersion = false"><app-icon name="x" :size="18" color="#94a3b8" /></view>
        </view>
        <scroll-view class="version-body" scroll-y>
          <view v-if="versions.length === 0" class="empty-state empty-state--sm">
            <text class="empty-state__text">暂无版本记录</text>
          </view>
          <view v-else class="version-list">
            <view v-for="(v, idx) in versions" :key="v.versionId || idx" class="version-item">
              <view class="version-item__info">
                <text class="version-item__name">v{{ v.version }}</text>
                <text class="version-item__time">{{ v.createdAt || '' }}</text>
              </view>
              <view v-if="idx > 0" class="version-item__action" @tap="onRollback(v)">
                <text>回滚</text>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- ========== 批量删除确认 ========== -->
    <view v-if="showBatchConfirm" class="modal-mask" @tap="showBatchConfirm = false">
      <view class="modal-card" @tap.stop>
        <text class="modal-title">确认删除？</text>
        <text class="modal-desc">将删除选中的 {{ selectedIds.length }} 个文件，此操作不可恢复。</text>
        <view class="modal-actions">
          <view class="modal-btn modal-btn--cancel" @tap="showBatchConfirm = false">
            <text>取消</text>
          </view>
          <view class="modal-btn modal-btn--danger" @tap="doBatchDelete">
            <text>确认删除</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import api from '../../utils/api.js'
import FileItem from './FileItem.vue'

const props = defineProps({
  projectId: { type: Number, required: true },
})

const emit = defineEmits(['refresh'])

// ---------- 数据 ----------
const files = ref([])
const stages = ref([])
const members = ref([])
const loading = ref(false)

// 批量模式
const batchMode = ref(false)
const selectedIds = ref([])

// 弹窗
const showUpload = ref(false)
const showFilter = ref(false)
const showMenu = ref(false)
const showVersion = ref(false)
const showBatchConfirm = ref(false)

// 上传相关
const uploading = ref(false)
const uploadStageIdx = ref(0)
const tempFile = ref(null)

// 筛选
const filterFormats = ref([])
const filterStageIds = ref([])
const filterUploaderIds = ref([])
const filterStatuses = ref([])

const formatOptions = ['文档', '表格', '图片', 'PDF', '压缩包', '其他']
const statusOptions = [
  { value: 'pending', label: '待审核' },
  { value: 'approved', label: '已通过' },
  { value: 'rejected', label: '已驳回' },
]

// 版本
const versions = ref([])
const currentFile = ref(null)

// ---------- 计算 ----------
const stageNames = computed(() => stages.value.map(s => s.name))

// ---------- 方法 ----------

/** 加载文件列表 */
async function loadFiles() {
  loading.value = true
  try {
    const params = { projectId: props.projectId }
    // 筛选条件
    if (filterStatuses.value.length) {
      params.status = filterStatuses.value.join(',')
    }
    if (filterStageIds.value.length) {
      params.stageId = filterStageIds.value[0]
    }
    const res = await api.fileList(params)
    const list = (res && (res.list || res.records)) || (Array.isArray(res) ? res : [])
    files.value = list
  } catch (e) {
    console.warn('[file-tab] 加载文件失败:', e)
  } finally {
    loading.value = false
  }
}

/** 加载阶段列表 */
async function loadStages() {
  try {
    const res = await api.projectStages(props.projectId)
    stages.value = Array.isArray(res) ? res : ((res && (res.list || res.records)) || [])
  } catch (e) {
    console.warn('[file-tab] 加载阶段失败:', e)
  }
}

/** 加载小组成员 */
async function loadMembers() {
  // 需要通过 projectDetail 拿到 groupId
  try {
    const detail = await api.projectDetail(props.projectId)
    if (detail && detail.groupId) {
      const res = await api.groupMembers(detail.groupId)
      members.value = (res && res.members) || []
    }
  } catch (e) {
    console.warn('[file-tab] 加载成员失败:', e)
  }
}

/** 加载全部 */
async function loadAll() {
  await Promise.all([
    loadFiles(),
    loadStages(),
    loadMembers(),
  ])
}

/** 刷新（对外暴露） */
function refresh() {
  loadFiles()
}
defineExpose({ refresh })

// ---------- 交互 ----------

/** 搜索（暂用 toast） */
function onSearchTap() {
  uni.showToast({ title: '搜索功能开发中', icon: 'none' })
}

/** 切换批量模式 */
function toggleBatch() {
  batchMode.value = !batchMode.value
  if (!batchMode.value) {
    selectedIds.value = []
  }
}

/** 切换选中 */
function toggleSelect(fileId) {
  const idx = selectedIds.value.indexOf(fileId)
  if (idx > -1) {
    selectedIds.value.splice(idx, 1)
  } else {
    selectedIds.value.push(fileId)
  }
}

/** 打开文件菜单 */
function openFileMenu(file) {
  currentFile.value = file
  showMenu.value = true
}

/** 下载/查看文件 */
async function onDownload(file) {
  showMenu.value = false
  const f = file || currentFile.value
  if (!f) return
  try {
    uni.showLoading({ title: '准备中...' })
    // 1) 向后端换取下载链接（downloadUrl 为 /files/{uuid}/{name} 相对路径）
    const data = await api.downloadFile(f.fileId)
    const origin = api.BASE_URL.replace(/\/api\/?$/, '')
    const url = origin + (data.downloadUrl || '')
    const name = f.name || data.filename || 'file'
    const ext = name.includes('.') ? name.split('.').pop().toLowerCase() : ''
    const isImage = ['png', 'jpg', 'jpeg', 'gif', 'webp', 'svg', 'bmp'].includes(ext)

    // #ifdef H5
    uni.hideLoading()
    window.open(url, '_blank')
    // #endif

    // #ifndef H5
    uni.downloadFile({
      url,
      success: (res) => {
        uni.hideLoading()
        if (res.statusCode === 200) {
          if (isImage) {
            uni.previewImage({ urls: [res.tempFilePath], current: res.tempFilePath })
          } else {
            uni.openDocument({
              filePath: res.tempFilePath,
              showMenu: true,
              fail: () => uni.showToast({ title: '暂不支持预览此格式', icon: 'none' })
            })
          }
        } else {
          uni.showToast({ title: '下载失败(' + res.statusCode + ')', icon: 'none' })
        }
      },
      fail: () => {
        uni.hideLoading()
        uni.showToast({ title: '下载失败，请检查网络', icon: 'none' })
      }
    })
    // #endif
  } catch (e) {
    uni.hideLoading()
    uni.showToast({ title: (e && e.message) || '获取下载链接失败', icon: 'none' })
  }
}

/** 收藏 */
function onFavorite() {
  showMenu.value = false
  if (!currentFile.value) return
  api.favoriteFile(currentFile.value.fileId).then(() => {
    uni.showToast({ title: '已收藏', icon: 'none' })
  }).catch(e => {
    uni.showToast({ title: (e && e.message) || '操作失败', icon: 'none' })
  })
}

/** 移动文件 */
function onMove() {
  showMenu.value = false
  if (!currentFile.value || !stages.value.length) return
  uni.showActionSheet({
    itemList: stages.value.map(s => s.name),
    success: (res) => {
      const stage = stages.value[res.tapIndex]
      api.moveFile(currentFile.value.fileId, stage.stageId).then(() => {
        uni.showToast({ title: '已移动', icon: 'none' })
        loadFiles()
      }).catch(e => {
        uni.showToast({ title: (e && e.message) || '移动失败', icon: 'none' })
      })
    }
  })
}

/** 删除文件 */
function onDelete() {
  showMenu.value = false
  if (!currentFile.value) return
  uni.showModal({
    title: '确认删除？',
    content: '删除后不可恢复',
    success: (res) => {
      if (res.confirm) {
        api.batchDeleteFile([currentFile.value.fileId]).then(() => {
          uni.showToast({ title: '已删除', icon: 'none' })
          loadFiles()
        }).catch(e => {
          uni.showToast({ title: (e && e.message) || '删除失败', icon: 'none' })
        })
      }
    }
  })
}

/** 审核通过 */
function onApprove(file) {
  api.approveFile(file.fileId).then(() => {
    uni.showToast({ title: '已通过', icon: 'none' })
    loadFiles()
  }).catch(e => {
    uni.showToast({ title: (e && e.message) || '操作失败', icon: 'none' })
  })
}

/** 驳回 */
function onReject(file) {
  uni.showModal({
    title: '驳回原因',
    editable: true,
    placeholderText: '请输入驳回原因',
    success: (res) => {
      if (res.confirm) {
        api.rejectFile(file.fileId, res.content || '未填写原因').then(() => {
          uni.showToast({ title: '已驳回', icon: 'none' })
          loadFiles()
        }).catch(e => {
          uni.showToast({ title: (e && e.message) || '操作失败', icon: 'none' })
        })
      }
    }
  })
}

/** 重新提交 */
function onResubmit(file) {
  uni.showToast({ title: '重新提交功能开发中', icon: 'none' })
}

/** 查看版本历史 */
async function onViewVersion(file) {
  currentFile.value = file
  showVersion.value = true
  versions.value = []
  try {
    const res = await api.versionList(file.fileId)
    versions.value = Array.isArray(res) ? res : ((res && (res.list || res.records)) || [])
  } catch (e) {
    uni.showToast({ title: '获取版本失败', icon: 'none' })
  }
}

/** 回滚版本 */
function onRollback(version) {
  if (!currentFile.value) return
  uni.showModal({
    title: '确认回滚？',
    content: `将回滚到 v${version.version} 版本`,
    success: (res) => {
      if (res.confirm) {
        api.versionRollback(currentFile.value.fileId, version.version).then(() => {
          uni.showToast({ title: '已回滚', icon: 'none' })
          showVersion.value = false
          loadFiles()
        }).catch(e => {
          uni.showToast({ title: (e && e.message) || '回滚失败', icon: 'none' })
        })
      }
    }
  })
}

/** 选择文件 */
function chooseFile() {
  // #ifdef H5
  const input = document.createElement('input')
  input.type = 'file'
  input.onchange = (e) => {
    if (e.target.files && e.target.files[0]) {
      tempFile.value = e.target.files[0]
      uni.showToast({ title: '已选择: ' + e.target.files[0].name, icon: 'none' })
    }
  }
  input.click()
  // #endif

  // #ifdef MP-WEIXIN
  // 小程序无系统文件管理器 API，按来源分两个入口：
  // 1) 微信聊天里的文档/压缩包 —— wx.chooseMessageFile type:'file'
  //    （type 合法值仅 file/image/video，无 'all'；'file' 表示除图片视频外的所有文件）
  // 2) 手机相册里的图片 —— wx.chooseMedia
  uni.showActionSheet({
    itemList: ['从微信聊天选择（文档/压缩包）', '从手机相册选择（图片）'],
    success: (res) => {
      if (res.tapIndex === 0) {
        uni.chooseMessageFile({
          count: 1,
          type: 'file',
          success: (r) => {
            const f = r.tempFiles[0]
            tempFile.value = { name: f.name, size: f.size, path: f.path }
            uni.showToast({ title: '已选择: ' + f.name, icon: 'none' })
          }
        })
      } else if (res.tapIndex === 1) {
        uni.chooseMedia({
          count: 1,
          mediaType: ['image'],
          sourceType: ['album'],
          success: (r) => {
            const f = r.tempFiles[0]
            const name = (f.tempFilePath || '').split('/').pop() || 'image.jpg'
            tempFile.value = { name, size: f.size, path: f.tempFilePath }
            uni.showToast({ title: '已选择: ' + name, icon: 'none' })
          }
        })
      }
    }
  })
  // #endif
}

/** 阶段选择 */
function onStageChange(e) {
  uploadStageIdx.value = e.detail.value
}

/** 上传前置校验：大小 50MB + 类型白名单（与后端规则保持一致） */
const UPLOAD_MAX_SIZE = 50 * 1024 * 1024
const UPLOAD_ALLOWED_EXT = ['md', 'txt', 'pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'png', 'jpg', 'jpeg', 'zip']
function checkFileBeforeUpload(file) {
  if ((file.size || 0) > UPLOAD_MAX_SIZE) {
    uni.showToast({ title: '文件超过 50MB 限制', icon: 'none' })
    return false
  }
  const name = file.name || ''
  const ext = name.includes('.') ? name.split('.').pop().toLowerCase() : ''
  if (!UPLOAD_ALLOWED_EXT.includes(ext)) {
    uni.showToast({ title: '不支持 .' + (ext || '?') + ' 类型文件', icon: 'none' })
    return false
  }
  return true
}

/** 上传文件 */
async function doUpload() {
  if (uploading.value) return
  if (!tempFile.value) {
    uni.showToast({ title: '请先选择文件', icon: 'none' })
    return
  }
  if (!checkFileBeforeUpload(tempFile.value)) return
  if (!stages.value.length) {
    uni.showToast({ title: '请先创建阶段', icon: 'none' })
    return
  }
  uploading.value = true
  try {
    const stage = stages.value[uploadStageIdx.value]
    // H5 传 File 对象；小程序传本地文件路径字符串
    let fileToUpload
    // #ifdef H5
    fileToUpload = tempFile.value
    // #endif
    // #ifndef H5
    fileToUpload = tempFile.value.path
    // #endif
    await api.uploadFile(fileToUpload, {
      projectId: props.projectId,
      stageId: stage ? stage.stageId : null,
    })
    uni.showToast({ title: '上传成功', icon: 'success' })
    showUpload.value = false
    tempFile.value = null
    loadFiles()
  } catch (e) {
    uni.showToast({ title: (e && e.message) || '上传失败', icon: 'none' })
  } finally {
    uploading.value = false
  }
}

/** 筛选芯片切换 */
function toggleChip(type, value) {
  let arr
  if (type === 'format') arr = filterFormats
  else if (type === 'stage') arr = filterStageIds
  else if (type === 'uploader') arr = filterUploaderIds
  else if (type === 'status') arr = filterStatuses
  else return

  const idx = arr.value.indexOf(value)
  if (idx > -1) {
    arr.value.splice(idx, 1)
  } else {
    arr.value.push(value)
  }
}

/** 应用筛选 */
function applyFilter() {
  showFilter.value = false
  loadFiles()
}

/** 清空筛选 */
function clearFilter() {
  filterFormats.value = []
  filterStageIds.value = []
  filterUploaderIds.value = []
  filterStatuses.value = []
}

/** 批量移动 */
function onBatchMove() {
  if (selectedIds.value.length === 0) {
    uni.showToast({ title: '请先选择文件', icon: 'none' })
    return
  }
  if (!stages.value.length) {
    uni.showToast({ title: '暂无阶段', icon: 'none' })
    return
  }
  uni.showActionSheet({
    itemList: stages.value.map(s => s.name),
    success: (res) => {
      const stage = stages.value[res.tapIndex]
      Promise.all(selectedIds.value.map(id =>
        api.moveFile(id, stage.stageId).catch(() => null)
      )).then(() => {
        uni.showToast({ title: '已移动', icon: 'none' })
        batchMode.value = false
        selectedIds.value = []
        loadFiles()
      })
    }
  })
}

/** 批量删除确认 */
function doBatchDelete() {
  if (selectedIds.value.length === 0) return
  showBatchConfirm.value = false
  api.batchDeleteFile(selectedIds.value).then(() => {
    uni.showToast({ title: '已删除', icon: 'none' })
    batchMode.value = false
    selectedIds.value = []
    loadFiles()
  }).catch(e => {
    uni.showToast({ title: (e && e.message) || '删除失败', icon: 'none' })
  })
}

/** 上拉加载更多 */
function onLoadMore() {
  // TODO: 分页
}

// ---------- 生命周期 ----------
onMounted(() => {
  if (props.projectId) {
    loadAll()
  }
})

watch(() => props.projectId, (newId) => {
  if (newId) loadAll()
})
</script>

<style scoped>
.file-tab {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

/* ---------- 顶部操作栏 ---------- */
.file-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
}
.file-toolbar__search {
  flex: 1;
  height: 36px;
  background: #f1f5f9;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
}
.file-toolbar__search-icon {
  font-size: 14px;
  opacity: 0.5;
}
.file-toolbar__search-ph {
  font-size: 13px;
  color: #94a3b8;
}
.file-toolbar__actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.file-toolbar__btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
}
.file-toolbar__btn--on {
  background: #dbeafe;
}
.file-toolbar__btn--on .file-toolbar__btn-icon {
  color: #2563eb;
}
.file-toolbar__btn--primary {
  background: #2563eb;
}
.file-toolbar__btn--primary .file-toolbar__btn-icon {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
}
.file-toolbar__btn-icon {
  font-size: 16px;
  color: #64748b;
}

/* ---------- 文件列表 ---------- */
.file-list {
  flex: 1;
  overflow: hidden;
  padding: 12px 16px;
}
.file-list__inner {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ---------- 加载/空状态 ---------- */
.loading-state {
  padding: 60px 0;
  text-align: center;
  font-size: 13px;
  color: #94a3b8;
}
.empty-state {
  padding: 60px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.empty-state--sm {
  padding: 30px 0;
}
.empty-state__icon {
  font-size: 42px;
  opacity: 0.4;
}
.empty-state__text {
  font-size: 13px;
  color: #94a3b8;
}
.empty-state__btn {
  margin-top: 8px;
  padding: 8px 20px;
  background: #2563eb;
  color: #fff;
  border-radius: 10px;
  font-size: 13px;
}

.bottom-spacing {
  height: 80px;
}

/* ---------- 批量操作栏 ---------- */
.batch-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid #f1f5f9;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.04);
  z-index: 100;
}
.batch-bar__count {
  font-size: 13px;
  color: #64748b;
}
.batch-bar__actions {
  display: flex;
  gap: 10px;
}
.batch-bar__btn {
  padding: 8px 16px;
  border-radius: 8px;
  background: #f1f5f9;
  font-size: 13px;
  font-weight: 500;
  color: #334155;
}
.batch-bar__btn--danger {
  background: #fee2e2;
  color: #dc2626;
}

/* ---------- 通用弹窗遮罩 ---------- */
.modal-mask {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 999;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}
.modal-sheet {
  width: 100%;
  max-height: 80vh;
  background: #fff;
  border-radius: 20px 20px 0 0;
  display: flex;
  flex-direction: column;
}
.modal-sheet--sm {
  max-height: 60vh;
}
.modal-sheet__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}
.modal-sheet__title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}
.modal-sheet__close {
  font-size: 16px;
  color: #94a3b8;
  padding: 4px;
}

/* ---------- 上传弹窗 ---------- */
.upload-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.upload-area {
  border: 2px dashed #cbd5e1;
  border-radius: 14px;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  background: #f8fafc;
}
.upload-area__icon {
  font-size: 36px;
}
.upload-area__text {
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}
.upload-area__hint {
  font-size: 12px;
  color: #94a3b8;
}
.upload-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: #f8fafc;
  border-radius: 10px;
}
.upload-meta__label {
  font-size: 13px;
  color: #64748b;
}
.upload-meta__value {
  font-size: 13px;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 4px;
}
.upload-meta__arrow {
  font-size: 16px;
  color: #94a3b8;
}
.upload-btn {
  padding: 14px;
  background: #2563eb;
  color: #fff;
  border-radius: 12px;
  text-align: center;
  font-size: 15px;
  font-weight: 600;
}
.upload-btn--disabled {
  opacity: 0.6;
}

/* ---------- 筛选弹窗 ---------- */
.filter-body {
  flex: 1;
  padding: 16px 20px;
}
.filter-group {
  margin-bottom: 20px;
}
.filter-group__title {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 10px;
}
.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.chip {
  padding: 6px 14px;
  border-radius: 999px;
  background: #f1f5f9;
  font-size: 12px;
  color: #64748b;
}
.chip--active {
  background: #dbeafe;
  color: #2563eb;
  font-weight: 500;
}
.filter-footer {
  display: flex;
  gap: 10px;
  padding: 12px 20px 20px;
  border-top: 1px solid #f1f5f9;
}
.filter-footer__btn {
  flex: 1;
  padding: 12px;
  border-radius: 10px;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
}
.filter-footer__btn--ghost {
  background: #f1f5f9;
  color: #64748b;
}
.filter-footer__btn--primary {
  background: #2563eb;
  color: #fff;
}

/* ---------- 文件菜单 ---------- */
.menu-list {
  padding: 8px 0;
}
.menu-list__item {
  padding: 14px 20px;
  font-size: 15px;
  color: #0f172a;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid #f8fafc;
}
.menu-list__item--danger {
  color: #dc2626;
}
.menu-list__cancel {
  padding: 14px 20px;
  font-size: 15px;
  color: #64748b;
  text-align: center;
  background: #f8fafc;
}

/* ---------- 版本历史 ---------- */
.version-body {
  flex: 1;
  padding: 12px 20px;
}
.version-list {
  display: flex;
  flex-direction: column;
}
.version-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid #f1f5f9;
}
.version-item:last-child {
  border-bottom: none;
}
.version-item__info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.version-item__name {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}
.version-item__time {
  font-size: 12px;
  color: #94a3b8;
}
.version-item__action {
  padding: 6px 12px;
  background: #f1f5f9;
  border-radius: 8px;
  font-size: 12px;
  color: #64748b;
}

/* ---------- 确认弹窗 ---------- */
.modal-card {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 280px;
  background: #fff;
  border-radius: 16px;
  padding: 24px 20px 16px;
  z-index: 1000;
}
.modal-title {
  display: block;
  font-size: 17px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 10px;
  text-align: center;
}
.modal-desc {
  display: block;
  font-size: 13px;
  color: #64748b;
  text-align: center;
  margin-bottom: 20px;
  line-height: 1.5;
}
.modal-actions {
  display: flex;
  gap: 10px;
}
.modal-btn {
  flex: 1;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
}
.modal-btn--cancel {
  background: #f1f5f9;
  color: #64748b;
}
.modal-btn--danger {
  background: #ef4444;
  color: #fff;
}
</style>
