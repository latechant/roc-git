<!--
  文件项组件
  Props: file, batchMode, selected
  Events: select, menu, approve, reject, resubmit, viewVersion
-->
<template>
  <view class="file-item" :class="{ 'file-item--selected': selected }" @tap="onTap">
    <!-- 批量选择框 -->
    <view
      v-if="batchMode"
      class="file-item__check"
      :class="{ 'file-item__check--on': selected }"
      @tap.stop="$emit('select')"
    >
      <app-icon v-if="selected" name="check" :size="12" color="#fff" />
    </view>

    <!-- 文件图标 -->
    <view class="file-item__icon">
      <app-icon :name="fileIconName(file.name)" :size="20" color="#94a3b8" />
    </view>

    <!-- 文件信息 -->
    <view class="file-item__info">
      <text class="file-item__name">{{ file.name || '未命名文件' }}</text>
      <text class="file-item__meta">{{ metaText }}</text>
    </view>

    <!-- 状态徽章 -->
    <view class="file-item__badge" :class="'file-item__badge--' + statusType">
      {{ statusText }}
    </view>

    <!-- 更多操作 -->
    <view class="file-item__more" @tap.stop="$emit('menu', file)">
      <text>⋯</text>
    </view>

    <!-- 操作按钮区 -->
    <view v-if="showActions" class="file-item__actions">
      <view class="file-item__btn file-item__btn--ghost" @tap.stop="$emit('viewVersion', file)">
        历史版本
      </view>
      <view
        v-if="file.status === 'pending'"
        class="file-item__btn file-item__btn--success"
        @tap.stop="$emit('approve', file)"
      >
        通过
      </view>
      <view
        v-if="file.status === 'pending'"
        class="file-item__btn file-item__btn--danger"
        @tap.stop="$emit('reject', file)"
      >
        驳回
      </view>
      <view
        v-if="file.status === 'rejected'"
        class="file-item__btn file-item__btn--primary"
        @tap.stop="$emit('resubmit', file)"
      >
        重新提交
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { formatFileSize } from '../../utils-new/format.js'
import { fileIconName } from '../../utils/iconPaths'

const props = defineProps({
  file: { type: Object, required: true },
  batchMode: { type: Boolean, default: false },
  selected: { type: Boolean, default: false },
})

const emit = defineEmits(['select', 'menu', 'approve', 'reject', 'resubmit', 'viewVersion', 'open'])

function onTap() {
  // 批量模式下点击整行切换选中；非批量模式触发打开/下载
  if (props.batchMode) {
    emit('select')
  } else {
    emit('open')
  }
}

const statusText = computed(() => {
  const map = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已驳回',
    draft: '草稿',
  }
  return map[props.file.status] || props.file.status || ''
})

const statusType = computed(() => {
  const map = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    draft: 'default',
  }
  return map[props.file.status] || 'default'
})

const metaText = computed(() => {
  const f = props.file
  const parts = []
  if (f.uploader && f.uploader.nickname) parts.push(f.uploader.nickname)
  if (f.uploaderName) parts.push(f.uploaderName)
  if (f.version) parts.push('v' + f.version)
  if (f.size != null) parts.push(formatFileSize(f.size))
  if (f.stageName) parts.push(f.stageName)
  return parts.filter(Boolean).join(' · ')
})

const showActions = computed(() => {
  // 有状态或有版本历史时显示操作区
  return ['pending', 'rejected', 'approved'].includes(props.file.status)
})
</script>

<style scoped>
.file-item {
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}
.file-item--selected {
  border-color: #2563eb;
  background: #eff6ff;
}
.file-item__check {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: 2px solid #cbd5e1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #fff;
  flex-shrink: 0;
  margin-right: 10px;
}
.file-item__check--on {
  background: #2563eb;
  border-color: #2563eb;
}
.file-item__icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: #f0f9ff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  margin-right: 12px;
}
.file-item__info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}
.file-item__name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.file-item__meta {
  font-size: 11px;
  color: #94a3b8;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.file-item__badge {
  padding: 3px 8px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 600;
  flex-shrink: 0;
  margin-left: 8px;
}
.file-item__badge--warning { background: #fef3c7; color: #d97706; }
.file-item__badge--success { background: #d1fae5; color: #059669; }
.file-item__badge--danger { background: #fee2e2; color: #dc2626; }
.file-item__badge--default { background: #f1f5f9; color: #64748b; }
.file-item__more {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #94a3b8;
  flex-shrink: 0;
  margin-left: 4px;
}
.file-item__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f8fafc;
}
.file-item__btn {
  padding: 5px 10px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 500;
  line-height: 1.4;
}
.file-item__btn--ghost {
  background: #f1f5f9;
  color: #64748b;
}
.file-item__btn--success {
  background: #d1fae5;
  color: #059669;
}
.file-item__btn--danger {
  background: #fee2e2;
  color: #dc2626;
}
.file-item__btn--primary {
  background: #dbeafe;
  color: #2563eb;
}
</style>
