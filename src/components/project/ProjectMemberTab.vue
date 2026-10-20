<!--
  项目详情 - 成员 Tab 组件
  功能：展示项目所属小组的成员列表（头像 + 姓名 + 角色）
  Props: groupId - 小组ID
-->
<template>
  <view class="member-tab">
    <scroll-view class="member-list" scroll-y :show-scrollbar="false">
      <view v-if="loading" class="loading-state">
        <text>加载中...</text>
      </view>

      <view v-else-if="members.length === 0" class="empty-state">
        <app-icon name="users" :size="42" color="#cbd5e1" />
        <text class="empty-state__text">暂无成员</text>
      </view>

      <view v-else class="member-list__inner">
        <!-- 组长置顶 -->
        <view v-if="leader" class="member-card member-card--leader">
          <view class="member-avatar" :style="{ background: avatarColor(leader.nickname) }">
            <text class="member-avatar__text">{{ avatarText(leader.nickname) }}</text>
          </view>
          <view class="member-info">
            <view class="member-info__row">
              <text class="member-info__name">{{ leader.nickname || '未知用户' }}</text>
              <text class="member-badge member-badge--leader">组长</text>
            </view>
            <text v-if="leader.major || leader.school" class="member-info__sub">
              {{ leader.major || '' }}{{ leader.school ? ' · ' + leader.school : '' }}
            </text>
          </view>
        </view>

        <!-- 组员 -->
        <view v-for="m in normalMembers" :key="m.userId || m.id" class="member-card">
          <view class="member-avatar" :style="{ background: avatarColor(m.nickname) }">
            <text class="member-avatar__text">{{ avatarText(m.nickname) }}</text>
          </view>
          <view class="member-info">
            <view class="member-info__row">
              <text class="member-info__name">{{ m.nickname || '未知用户' }}</text>
              <text class="member-badge member-badge--member">组员</text>
            </view>
            <text v-if="m.major || m.school" class="member-info__sub">
              {{ m.major || '' }}{{ m.school ? ' · ' + m.school : '' }}
            </text>
          </view>
        </view>
      </view>

      <view class="bottom-spacing"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import api from '../../utils/api.js'

const props = defineProps({
  groupId: { type: Number, default: 0 },
})

const members = ref([])
const loading = ref(false)

const leader = computed(() => members.value.find(m => m.role === 'leader'))
const normalMembers = computed(() => members.value.filter(m => m.role !== 'leader'))

/** 头像文字（首字） */
function avatarText(name) {
  if (!name) return '?'
  return String(name).trim().charAt(0).toUpperCase()
}

/** 头像颜色（根据名称哈希） */
function avatarColor(name) {
  const colors = [
    '#f59e0b', '#10b981', '#3b82f6', '#8b5cf6',
    '#ec4899', '#f97316', '#06b6d4', '#84cc16',
  ]
  let hash = 0
  for (let i = 0; i < (name || '').length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  return colors[Math.abs(hash) % colors.length]
}

/** 加载成员 */
async function loadMembers() {
  if (!props.groupId) {
    members.value = []
    return
  }
  loading.value = true
  try {
    const res = await api.groupMembers(props.groupId)
    const list = (res && res.members) || (Array.isArray(res) ? res : [])
    members.value = list
  } catch (e) {
    console.warn('[member-tab] 加载成员失败:', e)
  } finally {
    loading.value = false
  }
}

function refresh() {
  loadMembers()
}
defineExpose({ refresh })

onMounted(() => {
  if (props.groupId) loadMembers()
})

watch(() => props.groupId, (gid) => {
  if (gid) loadMembers()
})
</script>

<style scoped>
.member-tab {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

.member-list {
  flex: 1;
  overflow: hidden;
  padding: 14px 16px;
}
.member-list__inner {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 加载/空状态 */
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
.empty-state__icon {
  font-size: 42px;
  opacity: 0.4;
}
.empty-state__text {
  font-size: 13px;
  color: #94a3b8;
}

.bottom-spacing {
  height: 30px;
}

/* 成员卡片 */
.member-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #f1f5f9;
}
.member-card--leader {
  border-color: #fde68a;
  background: linear-gradient(135deg, #fffbeb 0%, #fff 100%);
}

.member-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.member-avatar__text {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
}

.member-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.member-info__row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.member-info__name {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.member-info__sub {
  font-size: 12px;
  color: #94a3b8;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.member-badge {
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 600;
  flex-shrink: 0;
}
.member-badge--leader {
  background: #fde68a;
  color: #b45309;
}
.member-badge--member {
  background: #e0f2fe;
  color: #0369a1;
}
</style>
