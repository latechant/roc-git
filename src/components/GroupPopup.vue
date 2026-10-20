<template>
  <view v-if="show" class="group-popup-mask" @tap="onMaskTap">
    <view class="group-popup-card" @tap.stop>
      <view class="group-popup-header">
        <text class="group-popup-title">我的小组</text>
        <view class="group-popup-close" @tap="emit('close')">
          <app-icon name="x" :size="18" color="#94a3b8" />
        </view>
      </view>

      <!-- ① 我的小组列表 -->
      <view class="group-popup-section">
        <text class="group-popup-section-title">我的小组</text>
        <view v-if="loading" class="group-popup-empty"><text class="group-popup-empty-text">加载中…</text></view>
        <view v-else-if="!groups.length" class="group-popup-empty"><text class="group-popup-empty-text">暂无小组</text></view>
        <view v-else class="group-popup-list">
          <view v-for="g in groups" :key="g.groupId || g.name" class="group-popup-item">
            <!-- 小组名称行 -->
            <view class="group-popup-item-header">
              <view v-if="editingGroupId === g.groupId" class="group-popup-edit-row">
                <input
                  class="group-popup-edit-input"
                  v-model="editingName"
                  :maxlength="-1"
                  placeholder="输入新名称"
                  @blur="onSaveRename(g)"
                  @confirm="onSaveRename(g)"
                />
                <text class="group-popup-edit-cancel" @tap.stop="cancelEdit">取消</text>
              </view>
              <view v-else class="group-popup-name-row" @tap="onToggleExpand(g)">
                <text class="group-popup-item-name">{{ g.name }}</text>
                <text class="group-popup-item-arrow" :class="{ 'group-popup-item-arrow--up': expandedGroupId === g.groupId }">▾</text>
              </view>
              <text v-if="isLeader(g)" class="group-popup-item-tag group-popup-item-tag--leader" @tap.stop="onStartRename(g)">重命名</text>
            </view>

            <!-- 邀请码行 -->
            <view class="group-popup-invite-row">
              <text v-if="inviteCodeMap[g.groupId]" class="group-popup-invite-code">{{ inviteCodeMap[g.groupId] }}</text>
              <text v-else class="group-popup-invite-get" @tap="onFetchInvite(g)">获取邀请码</text>
              <text v-if="inviteCodeMap[g.groupId]" class="group-popup-invite-act" @tap="onCopyInvite(g)">复制</text>
              <text v-if="inviteCodeMap[g.groupId]" class="group-popup-invite-act" @tap="onRegenerateInvite(g)">重新生成</text>
            </view>

            <!-- 展开的成员列表 -->
            <view v-if="expandedGroupId === g.groupId" class="group-popup-members">
              <view class="group-popup-members-title">
                <text class="group-popup-members-title-text">成员（{{ getMemberCount(g.groupId) }}）</text>
                <text v-if="!isLeader(g)" class="group-popup-leave" @tap="onLeaveGroup(g)">退出小组</text>
              </view>

              <view v-if="memberLoadingMap[g.groupId]" class="group-popup-empty group-popup-empty--sm">
                <text class="group-popup-empty-text">加载中…</text>
              </view>
              <view v-else class="group-popup-member-list">
                <view
                  v-for="m in getMembers(g.groupId)"
                  :key="m.userId || m.nickname"
                  class="group-popup-member-item"
                >
                  <MemberAvatar :name="m.nickname" size="sm" />
                  <view class="group-popup-member-info">
                    <text class="group-popup-member-name">{{ m.nickname }}</text>
                    <text v-if="m.role === 'leader'" class="group-popup-member-role">组长</text>
                    <text v-else class="group-popup-member-role group-popup-member-role--member">组员</text>
                  </view>
                  <text
                    v-if="isLeader(g) && m.role !== 'leader'"
                    class="group-popup-member-action"
                    @tap="onTransferLeader(g, m)"
                  >设为组长</text>
                </view>
              </view>

              <text v-if="isLeader(g)" class="group-popup-leave-tip">组长需先转让组长才能退出</text>
            </view>
          </view>
        </view>
      </view>

      <!-- ② 创建小组 -->
      <view class="group-popup-section">
        <text class="group-popup-section-title">创建小组</text>
        <view class="group-popup-row">
          <input class="group-popup-input" v-model="createName" placeholder="输入小组名称" :maxlength="-1" />
          <button class="group-popup-btn" @tap="onCreate">创建</button>
        </view>
        <view v-if="inviteCode" class="group-popup-invite">
          <text class="group-popup-invite-label">邀请码：</text>
          <text class="group-popup-invite-code">{{ inviteCode }}</text>
        </view>
      </view>

      <!-- ③ 加入小组 -->
      <view class="group-popup-section">
        <text class="group-popup-section-title">加入小组</text>
        <view class="group-popup-row">
          <input class="group-popup-input" v-model="joinCode" placeholder="输入邀请码" :maxlength="-1" />
          <button class="group-popup-btn" @tap="onJoin">加入</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useUserStore } from '../stores/index.js'
import api from '../utils/api.js'
import MemberAvatar from './MemberAvatar.vue'

const props = defineProps({
  show: { type: Boolean, default: false }
})
const emit = defineEmits(['close', 'joined'])

const userStore = useUserStore()

const groups = ref([])
const loading = ref(false)
const createName = ref('')
const joinCode = ref('')
const inviteCode = ref('')
const inviteCodeMap = ref({})

// 展开状态
const expandedGroupId = ref(null)
const memberMap = ref({})          // { groupId: [members] }
const memberLoadingMap = ref({})   // { groupId: true/false }

// 重命名状态
const editingGroupId = ref(null)
const editingName = ref('')

function storageKey(groupId) { return 'rocgit_group_invite_' + groupId }

function loadInviteCodes() {
  const map = {}
  ;(groups.value || []).forEach((g) => {
    const cached = uni.getStorageSync(storageKey(g.groupId))
    if (cached) map[g.groupId] = cached
  })
  inviteCodeMap.value = map
}

/** 判断当前用户是否是该小组组长 */
function isLeader(g) {
  const members = memberMap.value[g.groupId]
  if (!members) return false
  const myId = userStore.userId
  return members.some(m => m.userId === myId && m.role === 'leader')
}

/** 获取小组成员（用于模板） */
function getMembers(groupId) {
  const list = memberMap.value[groupId] || []
  // 组长置顶
  const leader = list.find(m => m.role === 'leader')
  const others = list.filter(m => m.role !== 'leader')
  return leader ? [leader, ...others] : others
}

function getMemberCount(groupId) {
  return (memberMap.value[groupId] || []).length
}

/** 展开/收起成员列表 */
async function onToggleExpand(g) {
  if (expandedGroupId.value === g.groupId) {
    expandedGroupId.value = null
    return
  }
  expandedGroupId.value = g.groupId
  // 未加载过则加载
  if (!memberMap.value[g.groupId] && !memberLoadingMap.value[g.groupId]) {
    await loadMembers(g.groupId)
  }
}

/** 加载小组成员 */
async function loadMembers(groupId) {
  memberLoadingMap.value = { ...memberLoadingMap.value, [groupId]: true }
  try {
    const res = await api.groupMembers(groupId).catch(() => null)
    const list = (res && res.members) || []
    memberMap.value = { ...memberMap.value, [groupId]: list }
  } finally {
    memberLoadingMap.value = { ...memberLoadingMap.value, [groupId]: false }
  }
}

/** 开始重命名 */
function onStartRename(g) {
  editingGroupId.value = g.groupId
  editingName.value = g.name
}

/** 取消重命名 */
function cancelEdit() {
  editingGroupId.value = null
  editingName.value = ''
}

/** 保存重命名 */
async function onSaveRename(g) {
  const name = (editingName.value || '').trim()
  if (!name) {
    cancelEdit()
    return
  }
  if (name === g.name) {
    cancelEdit()
    return
  }
  try {
    await api.updateGroup(g.groupId, { name })
    const idx = groups.value.findIndex(x => x.groupId === g.groupId)
    if (idx >= 0) {
      groups.value[idx] = { ...groups.value[idx], name }
    }
    showToast('已重命名')
  } catch (e) {
    showToast('重命名失败')
  }
  cancelEdit()
}

/** 退出小组 */
function onLeaveGroup(g) {
  uni.showModal({
    title: '退出小组',
    content: `确定要退出「${g.name}」吗？退出后将无法再查看小组项目。`,
    confirmColor: '#ef4444',
    success: async (res) => {
      if (!res.confirm) return
      try {
        await api.leaveGroup(g.groupId)
        showToast('已退出')
        await load()
        emit('joined')
      } catch (e) {
        showToast('退出失败')
      }
    }
  })
}

/** 转让组长 */
function onTransferLeader(g, member) {
  uni.showModal({
    title: '转让组长',
    content: `确定要将组长转让给「${member.nickname}」吗？`,
    confirmColor: '#2563eb',
    success: async (res) => {
      if (!res.confirm) return
      try {
        await api.transferLeader(g.groupId, member.userId)
        showToast('已转让')
        // 刷新成员列表
        await loadMembers(g.groupId)
        await load()
        emit('joined')
      } catch (e) {
        showToast('转让失败')
      }
    }
  })
}

function onFetchInvite(g) {
  api.regenerateInviteCode(g.groupId).then((res) => {
    const code = res && (res.inviteCode || res.code)
    if (!code) { showToast('获取失败'); return }
    uni.setStorageSync(storageKey(g.groupId), code)
    inviteCodeMap.value = Object.assign({}, inviteCodeMap.value, { [g.groupId]: code })
    showToast('已获取邀请码')
  }).catch(() => showToast('获取失败'))
}

function onRegenerateInvite(g) {
  api.regenerateInviteCode(g.groupId).then((res) => {
    const code = res && (res.inviteCode || res.code)
    if (!code) { showToast('重新生成失败'); return }
    uni.setStorageSync(storageKey(g.groupId), code)
    inviteCodeMap.value = Object.assign({}, inviteCodeMap.value, { [g.groupId]: code })
    showToast('已重新生成')
  }).catch(() => showToast('重新生成失败'))
}

function onCopyInvite(g) {
  const code = inviteCodeMap.value[g.groupId]
  if (!code) return
  if (navigator && navigator.clipboard) {
    navigator.clipboard.writeText(code).then(() => showToast('已复制')).catch(() => showToast('复制失败'))
  } else {
    uni.setClipboardData({ data: code, success: () => showToast('已复制') })
  }
}

function showToast(msg) {
  const t = document && document.getElementById && document.getElementById('toast')
  if (t) {
    t.textContent = msg
    t.classList.remove('hidden')
    setTimeout(() => t.classList.add('hidden'), 1800)
  } else {
    uni.showToast({ title: msg, icon: 'none' })
  }
}

async function load() {
  loading.value = true
  const data = await api.myGroups().catch(() => null)
  groups.value = Array.isArray(data) ? data : []
  loadInviteCodes()
  // 清空成员缓存（可能有变化）
  memberMap.value = {}
  memberLoadingMap.value = {}
  expandedGroupId.value = null
  loading.value = false
}

watch(() => props.show, (v) => { if (v) load() })

async function onCreate() {
  const name = (createName.value || '').trim()
  if (!name) { showToast('请输入小组名称'); return }
  const res = await api.createGroup({ name }).catch((e) => null)
  if (!res) { showToast('创建失败'); return }
  inviteCode.value = res.inviteCode || res.code || ''
  if (res.groupId && inviteCode.value) {
    uni.setStorageSync(storageKey(res.groupId), inviteCode.value)
    inviteCodeMap.value = Object.assign({}, inviteCodeMap.value, { [res.groupId]: inviteCode.value })
  }
  createName.value = ''
  showToast('创建成功')
  await load()
  emit('joined')
}

async function onJoin() {
  const code = (joinCode.value || '').trim()
  if (!code) { showToast('请输入邀请码'); return }
  const ok = await api.validateInviteCode({ inviteCode: code }).catch(() => null)
  if (!ok) { showToast('邀请码无效'); return }
  const res = await api.joinGroup({ inviteCode: code }).catch((e) => null)
  if (!res) { showToast('加入失败'); return }
  joinCode.value = ''
  showToast('加入成功')
  await load()
  emit('joined')
}

function onMaskTap() { emit('close') }
</script>

<style scoped>
.group-popup-mask {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}
.group-popup-card {
  width: 320px;
  max-height: 85vh;
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-sizing: border-box;
  overflow-y: auto;
}
.group-popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.group-popup-title {
  font-size: 17px;
  font-weight: 600;
  color: #0f172a;
}
.group-popup-close {
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.group-popup-section {
  margin-bottom: 16px;
}
.group-popup-section-title {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
  margin-bottom: 8px;
}
.group-popup-empty {
  padding: 12px 0;
}
.group-popup-empty--sm {
  padding: 8px 0;
}
.group-popup-empty-text {
  font-size: 13px;
  color: #94a3b8;
}
.group-popup-list {
  display: flex;
  flex-direction: column;
}
.group-popup-item {
  padding: 10px 0;
  border-bottom: 1px solid #f1f5f9;
}
.group-popup-item:last-child {
  border-bottom: none;
}
.group-popup-item-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}
.group-popup-name-row {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}
.group-popup-item-name {
  font-size: 14px;
  color: #334155;
  font-weight: 500;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.group-popup-item-arrow {
  font-size: 11px;
  color: #94a3b8;
  transition: transform 0.2s;
  flex-shrink: 0;
}
.group-popup-item-arrow--up {
  transform: rotate(180deg);
}
.group-popup-item-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 6px;
  flex-shrink: 0;
}
.group-popup-item-tag--leader {
  color: #2563eb;
  background: #dbeafe;
}

/* 重命名编辑 */
.group-popup-edit-row {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
}
.group-popup-edit-input {
  flex: 1;
  height: 28px;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  padding: 0 8px;
  font-size: 13px;
  background: #eff6ff;
  box-sizing: border-box;
}
.group-popup-edit-cancel {
  font-size: 12px;
  color: #64748b;
  flex-shrink: 0;
}

.group-popup-invite-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 6px;
}
.group-popup-invite-code {
  font-size: 12px;
  font-weight: 600;
  color: #2563eb;
}
.group-popup-invite-get {
  font-size: 12px;
  color: #2563eb;
}
.group-popup-invite-act {
  font-size: 12px;
  color: #64748b;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 2px 8px;
}

/* 成员列表 */
.group-popup-members {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #e2e8f0;
}
.group-popup-members-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.group-popup-members-title-text {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}
.group-popup-leave {
  font-size: 12px;
  color: #ef4444;
}
.group-popup-leave-tip {
  font-size: 11px;
  color: #94a3b8;
  margin-top: 6px;
  display: block;
  text-align: center;
}
.group-popup-member-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 180px;
  overflow-y: auto;
}
.group-popup-member-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.group-popup-member-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}
.group-popup-member-name {
  font-size: 13px;
  color: #334155;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.group-popup-member-role {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
  background: #fef3c7;
  color: #b45309;
  flex-shrink: 0;
}
.group-popup-member-role--member {
  background: #f1f5f9;
  color: #64748b;
}
.group-popup-member-action {
  font-size: 11px;
  color: #2563eb;
  flex-shrink: 0;
}

.group-popup-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.group-popup-input {
  flex: 1;
  height: 36px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 0 10px;
  font-size: 14px;
  box-sizing: border-box;
  background: #fff;
}
.group-popup-btn {
  height: 36px;
  line-height: 36px;
  padding: 0 16px;
  border-radius: 8px;
  background: #2563eb;
  color: #fff;
  font-size: 14px;
}
.group-popup-invite {
  margin-top: 8px;
}
.group-popup-invite-label {
  font-size: 13px;
  color: #64748b;
}
</style>
