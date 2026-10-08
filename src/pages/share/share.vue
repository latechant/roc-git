<template>
  <view class="share-root">
    <view class="share-head">
      <text class="share-head-title">项目分享</text>
      <text class="share-head-sub">ROC-git · 只读公开页</text>
    </view>

    <view v-if="controller.loading" class="share-state">
      <text class="share-state-text">加载中…</text>
    </view>

    <view v-else-if="controller.error" class="share-state">
      <text class="share-state-text">{{ controller.error }}</text>
    </view>

    <scroll-view v-else class="share-scroll" scroll-y :show-scrollbar="false">
      <!-- 项目概览 -->
      <view class="share-card">
        <text class="share-card-title">项目概览</text>
        <text class="share-name">{{ controller.overview.name || '未命名项目' }}</text>
        <text v-if="controller.overview.description" class="share-desc">{{ controller.overview.description }}</text>
        <view class="share-meta-row">
          <text class="share-meta">状态：{{ controller.overview.status === 'archived' ? '已归档' : (controller.overview.status === 'completed' ? '已完成' : '进行中') }}</text>
          <text class="share-meta">进度：{{ controller.overview.progress || 0 }}%</text>
        </view>
      </view>

      <!-- 时间线 -->
      <view class="share-card">
        <text class="share-card-title">协作时间线</text>
        <view v-if="!controller.timeline.length" class="share-empty"><text class="share-empty-text">暂无记录</text></view>
        <view v-for="(t, i) in controller.timeline" :key="i" class="share-tl-item">
          <view class="share-tl-dot"></view>
          <view class="share-tl-body">
            <text class="share-tl-text">{{ t.action || t.title || '' }}</text>
            <text class="share-tl-time">{{ String(t.createdAt || '').replace('T', ' ').slice(0, 16) }}</text>
          </view>
        </view>
      </view>

      <!-- 文件列表 -->
      <view class="share-card">
        <text class="share-card-title">项目文件（{{ controller.files.length }}）</text>
        <view v-if="!controller.files.length" class="share-empty"><text class="share-empty-text">暂无文件</text></view>
        <view v-for="f in controller.files" :key="f.fileId" class="share-file-row">
          <view class="share-file-main">
            <text class="share-file-name">{{ f.name }}</text>
            <text class="share-file-meta">{{ f.size || '' }}{{ f.size ? ' · ' : '' }}{{ f.status === 'approved' ? '已通过' : (f.status === 'pending' ? '待审核' : '已驳回') }}</text>
          </view>
          <text class="share-file-date">{{ String(f.createdAt || '').replace('T', ' ').slice(0, 10) }}</text>
        </view>
      </view>

      <view class="share-foot"><text class="share-foot-text">由 ROC-git 生成 · 数据只读</text></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import api from '../../utils/api.js'

const controller = reactive({
  loading: true,
  error: '',
  overview: {},
  timeline: [],
  files: []
})

onLoad(function (options) {
  const token = (options && options.token) || ''
  if (!token) {
    controller.loading = false
    controller.error = '缺少分享 token'
    return
  }
  Promise.all([
    api.shareOverview(token).catch(function () { return null }),
    api.shareTimeline(token).catch(function () { return null }),
    api.shareFiles(token).catch(function () { return null })
  ]).then(function (res) {
    controller.loading = false
    controller.overview = (res[0] && (res[0].data || {})) || {}
    const tl = res[1] && res[1].data
    controller.timeline = Array.isArray(tl) ? tl : ((tl && (tl.list || tl.records)) || [])
    const fl = res[2] && res[2].data
    controller.files = Array.isArray(fl) ? fl : ((fl && (fl.list || fl.records)) || [])
    if (!controller.overview.name && !controller.timeline.length && !controller.files.length) {
      controller.error = '分享链接无效或已失效'
    }
  })
})
</script>

<style>
.share-root { min-height: 100vh; background: #f8fafc; }
.share-head { padding: 40rpx 32rpx 24rpx; background: linear-gradient(135deg, #1e293b, #334155); }
.share-head-title { display: block; font-size: 34rpx; font-weight: 600; color: #fff; }
.share-head-sub { display: block; font-size: 22rpx; color: #94a3b8; margin-top: 6rpx; }
.share-state { padding: 120rpx 32rpx; text-align: center; }
.share-state-text { font-size: 26rpx; color: #94a3b8; }
.share-scroll { height: calc(100vh - 160rpx); }
.share-card { background: #fff; border-radius: 20rpx; margin: 24rpx; padding: 28rpx; box-shadow: 0 1px 4px rgba(15, 23, 42, 0.06); }
.share-card-title { display: block; font-size: 24rpx; font-weight: 600; color: #64748b; margin-bottom: 16rpx; }
.share-name { display: block; font-size: 34rpx; font-weight: 600; color: #0f172a; }
.share-desc { display: block; font-size: 25rpx; color: #64748b; margin-top: 10rpx; line-height: 1.6; }
.share-meta-row { display: flex; gap: 24rpx; margin-top: 14rpx; }
.share-meta { font-size: 22rpx; color: #2563eb; background: #eff6ff; border-radius: 10rpx; padding: 4rpx 14rpx; }
.share-empty { padding: 20rpx 0; }
.share-empty-text { font-size: 23rpx; color: #94a3b8; }
.share-tl-item { display: flex; gap: 16rpx; padding: 12rpx 0; border-bottom: 1px solid #f1f5f9; }
.share-tl-dot { width: 14rpx; height: 14rpx; border-radius: 50%; background: #38bdf8; margin-top: 12rpx; flex-shrink: 0; }
.share-tl-body { flex: 1; }
.share-tl-text { display: block; font-size: 25rpx; color: #334155; line-height: 1.5; }
.share-tl-time { display: block; font-size: 20rpx; color: #94a3b8; margin-top: 4rpx; }
.share-file-row { display: flex; align-items: center; justify-content: space-between; gap: 12rpx; padding: 14rpx 0; border-bottom: 1px solid #f1f5f9; }
.share-file-main { flex: 1; min-width: 0; }
.share-file-name { display: block; font-size: 25rpx; color: #334155; font-weight: 500; }
.share-file-meta { display: block; font-size: 21rpx; color: #94a3b8; margin-top: 4rpx; }
.share-file-date { font-size: 21rpx; color: #94a3b8; flex-shrink: 0; }
.share-foot { text-align: center; padding: 24rpx 0 48rpx; }
.share-foot-text { font-size: 21rpx; color: #cbd5e1; }
</style>
