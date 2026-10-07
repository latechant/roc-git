<template>
  <view v-if="show" class="group-popup-mask" @tap="onMaskTap">
    <view class="group-popup-card" @tap.stop>
      <view class="group-popup-header">
        <text class="group-popup-title">我的小组</text>
        <text class="group-popup-close" @tap="emit('close')">✕</text>
      </view>

      <!-- ① 我的小组列表 -->
      <view class="group-popup-section">
        <text class="group-popup-section-title">我的小组</text>
        <view v-if="loading" class="group-popup-empty"><text class="group-popup-empty-text">加载中…</text></view>
        <view v-else-if="!groups.length" class="group-popup-empty"><text class="group-popup-empty-text">暂无小组</text></view>
        <view v-else class="group-popup-list">
          <view v-for="g in groups" :key="g.groupId || g.name" class="group-popup-item">
            <text class="group-popup-item-name">{{ g.name }}</text>
            <view class="group-popup-invite-row">
              <text v-if="inviteCodeMap[g.groupId]" class="group-popup-invite-code">{{ inviteCodeMap[g.groupId] }}</text>
              <text v-else class="group-popup-invite-get" @tap="onFetchInvite(g)">获取邀请码</text>
              <text v-if="inviteCodeMap[g.groupId]" class="group-popup-invite-act" @tap="onCopyInvite(g)">复制</text>
              <text v-if="inviteCodeMap[g.groupId]" class="group-popup-invite-act" @tap="onRegenerateInvite(g)">重新生成</text>
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
import api from '../utils/api.js'

const props = defineProps({
  show: { type: Boolean, default: false }
})
const emit = defineEmits(['close', 'joined'])

const groups = ref([])
const loading = ref(false)
const createName = ref('')
const joinCode = ref('')
const inviteCode = ref('')
// 邀请码展示：优先读本地 storage（创建成功时写入），无则按需拉取 / 重新生成
const inviteCodeMap = ref({})

function storageKey(groupId) { return 'rocgit_group_invite_' + groupId }

function loadInviteCodes() {
  const map = {}
  ;(groups.value || []).forEach((g) => {
    const cached = uni.getStorageSync(storageKey(g.groupId))
    if (cached) map[g.groupId] = cached
  })
  inviteCodeMap.value = map
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
  // 尽量复用页面 toast，取不到则用 uni 内置
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
  loading.value = false
}

watch(() => props.show, (v) => { if (v) load() })

async function onCreate() {
  const name = (createName.value || '').trim()
  if (!name) { showToast('请输入小组名称'); return }
  const res = await api.createGroup({ name }).catch((e) => null)
  if (!res) { showToast('创建失败'); return }
  inviteCode.value = res.inviteCode || res.code || ''
  // 创建成功即落盘邀请码，列表项邀请码行直接展示
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
  width: 300px;
  max-height: 80vh;
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
  font-size: 16px;
  color: #94a3b8;
  padding: 4px;
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
.group-popup-empty-text {
  font-size: 13px;
  color: #94a3b8;
}
.group-popup-list {
  display: flex;
  flex-direction: column;
}
.group-popup-item {
  padding: 8px 0;
  border-bottom: 1px solid #f1f5f9;
}
.group-popup-item-name {
  font-size: 14px;
  color: #334155;
}
.group-popup-invite-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 4px;
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
.group-popup-invite-code {
  font-size: 14px;
  font-weight: 600;
  color: #2563eb;
}
</style>
