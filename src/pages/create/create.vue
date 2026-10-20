<!--
  创建项目页（四步向导）
  Step 1: 基础信息（项目名+描述+小组）
  Step 2: 阶段（动态行 + 模板）
  Step 3: 分工（成员选择 + 任务列表 + 任务模板）
  Step 4: 规则（审核 + 截止提醒）
-->
<template>
  <view class="create-page">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="nav-bar__back" @tap="onBack">
        <text>‹</text>
      </view>
      <text class="nav-bar__title">创建项目</text>
      <view class="nav-bar__placeholder"></view>
    </view>

    <!-- 步骤条 -->
    <view class="step-bar">
      <view
        v-for="(s, idx) in steps"
        :key="idx"
        class="step-bar__item"
        :class="{ 'step-bar__item--active': step === idx + 1, 'step-bar__item--done': step > idx + 1 }"
      >
        <view class="step-bar__dot">
          <app-icon v-if="step > idx + 1" name="check" :size="12" color="#fff" />
          <text v-else>{{ idx + 1 }}</text>
        </view>
        <text class="step-bar__label">{{ s.label }}</text>
      </view>
      <view class="step-bar__line" :style="{ width: lineWidth + '%', left: '12.5%' }"></view>
    </view>

    <scroll-view class="content" scroll-y :show-scrollbar="false">
      <!-- ========== Step 1: 基础信息 ========== -->
      <view v-if="step === 1" class="step-content">
        <view class="section">
          <text class="section__title">项目名称 <text class="required">*</text></text>
          <input
            class="form-input"
            v-model="form.name"
            placeholder="请输入项目名称"
            placeholder-class="form-input__ph"
            maxlength="30"
          />
        </view>

        <view class="section">
          <text class="section__title">所属小组</text>
          <picker mode="selector" :range="groupOptions" range-key="name" @change="onGroupPick">
            <view class="form-picker">
              <text>{{ selectedGroup ? selectedGroup.name : '请选择小组' }}</text>
              <text class="form-picker__arrow">›</text>
            </view>
          </picker>
          <text v-if="!groups.length" class="form-tip">
            暂无小组，<text class="form-tip__link" @tap="onGoGroups">去创建/加入</text>
          </text>
        </view>

        <view class="section">
          <text class="section__title">项目描述</text>
          <textarea
            class="form-textarea"
            v-model="form.description"
            placeholder="简单介绍一下这个项目（可选）"
            placeholder-class="form-input__ph"
            maxlength="200"
          />
        </view>
      </view>

      <!-- ========== Step 2: 阶段 ========== -->
      <view v-if="step === 2" class="step-content">
        <view class="section">
          <view class="section__header">
            <text class="section__title">项目阶段</text>
            <text class="section__count">{{ form.stages.length }} 个</text>
          </view>

          <!-- 阶段模板 -->
          <view class="template-row">
            <text class="template-row__label">快速模板：</text>
            <view
              v-for="tpl in stageTemplates"
              :key="tpl.key"
              class="template-chip"
              @tap="applyStageTemplate(tpl.key)"
            >
              <text>{{ tpl.label }}</text>
            </view>
          </view>

          <!-- 阶段行列表 -->
          <view class="stage-list">
            <view v-for="(s, idx) in form.stages" :key="idx" class="stage-row">
              <text class="stage-row__no">{{ formatNo(idx + 1) }}</text>
              <input
                class="stage-row__input"
                v-model="s.name"
                placeholder="阶段名称"
                placeholder-class="form-input__ph"
              />
              <picker mode="date" :value="s.endDate" @change="e => s.endDate = e.detail.value">
                <view class="stage-row__date">
                  {{ s.endDate || '截止日期' }}
                </view>
              </picker>
              <view v-if="form.stages.length > 1" class="stage-row__del" @tap="removeStage(idx)">
                <text>×</text>
              </view>
            </view>
          </view>

          <view class="add-row-btn" @tap="addStage">
            <text class="add-row-btn__icon">+</text>
            <text>添加阶段</text>
          </view>
        </view>
      </view>

      <!-- ========== Step 3: 分工 ========== -->
      <view v-if="step === 3" class="step-content">
        <!-- 成员选择 -->
        <view class="section">
          <text class="section__title">项目成员</text>
          <view class="member-pool">
            <view
              v-for="m in memberPool"
              :key="m.userId"
              class="member-chip"
              :class="{ 'member-chip--on': m.selected }"
              @tap="toggleMember(m)"
            >
              <view class="member-chip__avatar" :style="{ background: avatarColor(m.nickname) }">
                <text>{{ avatarText(m.nickname) }}</text>
              </view>
              <text class="member-chip__name">{{ m.nickname }}</text>
              <app-icon v-if="m.selected" class="member-chip__check" name="check" :size="10" color="#fff" />
            </view>
          </view>
          <text v-if="!memberPool.length" class="form-tip">暂无成员，先加入小组后再创建</text>
        </view>

        <!-- 任务列表 -->
        <view class="section">
          <view class="section__header">
            <text class="section__title">任务分工</text>
            <text class="section__count">{{ form.tasks.length }} 项</text>
          </view>

          <!-- 任务模板 -->
          <view class="template-row">
            <text class="template-row__label">快速添加：</text>
            <view
              v-for="tpl in taskTemplates"
              :key="tpl.key"
              class="template-chip"
              @tap="applyTaskTemplate(tpl.key)"
            >
              <text>{{ tpl.label }}</text>
            </view>
          </view>

          <view class="task-list">
            <view v-for="(t, idx) in form.tasks" :key="idx" class="task-row">
              <!-- 所属阶段 -->
              <picker
                mode="selector"
                :range="stageNames"
                :value="t.stageIdx"
                @change="e => t.stageIdx = Number(e.detail.value)"
              >
                <view class="task-row__stage">
                  {{ stageNames[t.stageIdx] || '阶段' }}
                </view>
              </picker>

              <input
                class="task-row__input"
                v-model="t.name"
                placeholder="任务描述"
                placeholder-class="form-input__ph"
              />

              <picker mode="date" :value="t.deadline" @change="e => t.deadline = e.detail.value">
                <view class="task-row__date">
                  {{ t.deadline || '截止' }}
                </view>
              </picker>

              <view v-if="form.tasks.length > 1" class="task-row__del" @tap="removeTask(idx)">
                <text>×</text>
              </view>
            </view>
          </view>

          <view class="add-row-btn" @tap="addTask">
            <text class="add-row-btn__icon">+</text>
            <text>添加任务</text>
          </view>
        </view>
      </view>

      <!-- ========== Step 4: 规则 ========== -->
      <view v-if="step === 4" class="step-content">
        <view class="section">
          <text class="section__title">项目规则</text>

          <view class="rule-item">
            <view class="rule-item__info">
              <text class="rule-item__title">挂单审核</text>
              <text class="rule-item__desc">上传的文件需要组长审核通过才能归档</text>
            </view>
            <switch
              :checked="form.auditRule.requireAudit"
              color="#2563eb"
              @change="form.auditRule.requireAudit = $event.detail.value"
            />
          </view>

          <view class="rule-item">
            <view class="rule-item__info">
              <text class="rule-item__title">截止提醒</text>
              <text class="rule-item__desc">任务截止前自动提醒负责人</text>
            </view>
            <switch
              :checked="reminderOn"
              color="#2563eb"
              @change="onReminderChange"
            />
          </view>
        </view>

        <!-- 项目概览 -->
        <view class="section">
          <text class="section__title">项目概览</text>
          <view class="overview-card">
            <view class="overview-row">
              <text class="overview-row__label">项目名称</text>
              <text class="overview-row__value">{{ form.name || '未命名' }}</text>
            </view>
            <view class="overview-row">
              <text class="overview-row__label">所属小组</text>
              <text class="overview-row__value">{{ selectedGroup ? selectedGroup.name : '未选择' }}</text>
            </view>
            <view class="overview-row">
              <text class="overview-row__label">阶段数量</text>
              <text class="overview-row__value">{{ form.stages.length }} 个</text>
            </view>
            <view class="overview-row">
              <text class="overview-row__label">成员数量</text>
              <text class="overview-row__value">{{ selectedMembers.length }} 人</text>
            </view>
            <view class="overview-row">
              <text class="overview-row__label">任务数量</text>
              <text class="overview-row__value">{{ form.tasks.length }} 项</text>
            </view>
          </view>
        </view>
      </view>

      <view class="bottom-spacing"></view>
    </scroll-view>

    <!-- 底部按钮栏 -->
    <view class="bottom-bar">
      <view v-if="step > 1" class="bottom-bar__btn bottom-bar__btn--ghost" @tap="prevStep">
        <text>上一步</text>
      </view>
      <view v-if="step < 4" class="bottom-bar__btn bottom-bar__btn--primary" @tap="nextStep">
        <text>下一步</text>
      </view>
      <view
        v-else
        class="bottom-bar__btn bottom-bar__btn--primary"
        :class="{ 'bottom-bar__btn--disabled': submitting }"
        @tap="submitCreate"
      >
        <text>{{ submitting ? '创建中...' : '创建项目' }}</text>
      </view>
    </view>
  </view>

  <!-- 小组管理弹窗 -->
  <GroupPopup :show="showGroupPopup" @close="showGroupPopup = false" @joined="onGroupJoined" />
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '../../utils/api.js'
import GroupPopup from '../../components/GroupPopup.vue'

const steps = [
  { label: '基础信息' },
  { label: '阶段' },
  { label: '分工' },
  { label: '规则' },
]

const step = ref(1)
const submitting = ref(false)

// 表单数据
const form = reactive({
  name: '',
  description: '',
  stages: [
    { name: '需求分析', endDate: '' },
    { name: '方案设计', endDate: '' },
    { name: '编码实现', endDate: '' },
    { name: '测试验收', endDate: '' },
  ],
  tasks: [],
  auditRule: { requireAudit: true, auditorRole: 'leader' },
  reminderRule: { taskReminderDays: [3, 1] },
})

const reminderOn = computed(() => form.reminderRule.taskReminderDays.length > 0)

// 小组
const groups = ref([])
const groupIdx = ref(-1)
const groupOptions = computed(() => groups.value)
const selectedGroup = computed(() =>
  groupIdx.value >= 0 ? groups.value[groupIdx.value] : null
)
const showGroupPopup = ref(false)

// 成员池
const memberPool = ref([])
const selectedMembers = computed(() => memberPool.value.filter(m => m.selected))

// 阶段名列表（供任务选择）
const stageNames = computed(() => form.stages.map((s, i) => s.name || ('阶段' + (i + 1))))

// 步骤条进度线
const lineWidth = computed(() => ((step.value - 1) / 3) * 75)

// ---------- 模板 ----------
const stageTemplates = [
  { key: 'course', label: '课程项目' },
  { key: 'research', label: '调研报告' },
  { key: 'agile', label: '敏捷迭代' },
]
const STAGE_TEMPLATE_DATA = {
  course: ['需求分析', '方案设计', '编码实现', '单元测试', '联调验收', '答辩提交'],
  research: ['选题与文献调研', '数据收集', '分析论证', '报告撰写', '修改提交'],
  agile: ['迭代规划', '需求澄清', '开发', '评审', '复盘'],
}

const taskTemplates = [
  { key: 'dev', label: '通用开发' },
  { key: 'report', label: '报告答辩' },
]
const TASK_TEMPLATE_DATA = {
  dev: ['需求分析', '系统设计', '编码实现', '单元测试', '集成联调', '文档整理'],
  report: ['资料收集', '初稿撰写', '小组评审', '修改定稿', '答辩准备'],
}

// ---------- 方法 ----------

function onBack() {
  if (step.value > 1) {
    step.value--
  } else {
    uni.navigateBack()
  }
}

function nextStep() {
  // 校验
  if (step.value === 1) {
    if (!form.name.trim()) {
      uni.showToast({ title: '请输入项目名称', icon: 'none' })
      return
    }
    if (!selectedGroup.value) {
      uni.showToast({ title: '请选择小组', icon: 'none' })
      return
    }
  }
  if (step.value === 2) {
    const validStages = form.stages.filter(s => s.name.trim())
    if (!validStages.length) {
      uni.showToast({ title: '至少添加一个阶段', icon: 'none' })
      return
    }
  }
  if (step.value < 4) {
    step.value++
  }
}

function prevStep() {
  if (step.value > 1) step.value--
}

function onGroupPick(e) {
  groupIdx.value = Number(e.detail.value)
  loadMembers(groups.value[groupIdx.value].groupId)
}

function onGoGroups() {
  showGroupPopup.value = true
}

/** 小组创建/加入成功后刷新列表并选中新小组 */
async function onGroupJoined() {
  showGroupPopup.value = false
  const list = await api.myGroups().catch(() => null)
  groups.value = Array.isArray(list) ? list : []
  if (groups.value.length) {
    // 选中最新加入/创建的小组
    groupIdx.value = 0
    loadMembers(groups.value[0].groupId)
  }
}

// 阶段操作
function addStage() {
  form.stages.push({ name: '', endDate: '' })
}
function removeStage(idx) {
  if (form.stages.length <= 1) return
  form.stages.splice(idx, 1)
}
function applyStageTemplate(key) {
  const names = STAGE_TEMPLATE_DATA[key]
  if (!names) return
  form.stages = names.map(n => ({ name: n, endDate: '' }))
  const label = stageTemplates.find(t => t.key === key)?.label || key
  uni.showToast({ title: `已套用「${label}」模板`, icon: 'none' })
}

// 成员操作
function toggleMember(m) {
  m.selected = !m.selected
}
function avatarColor(name) {
  const colors = ['#f59e0b', '#10b981', '#3b82f6', '#8b5cf6', '#ec4899', '#f97316']
  let hash = 0
  for (let i = 0; i < (name || '').length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  return colors[Math.abs(hash) % colors.length]
}
function avatarText(name) {
  if (!name) return '?'
  return String(name).trim().charAt(0).toUpperCase()
}
function formatNo(n) {
  return n < 10 ? '0' + n : '' + n
}

// 任务操作
function addTask() {
  form.tasks.push({ name: '', stageIdx: 0, deadline: '' })
}
function removeTask(idx) {
  if (form.tasks.length <= 1) return
  form.tasks.splice(idx, 1)
}
function applyTaskTemplate(key) {
  const names = TASK_TEMPLATE_DATA[key]
  if (!names) return
  const startIdx = form.tasks.length
  names.forEach((n, i) => {
    form.tasks.push({
      name: n,
      stageIdx: Math.min(i, form.stages.length - 1) < 0 ? 0 : Math.min(i, form.stages.length - 1),
      deadline: '',
    })
  })
  const label = taskTemplates.find(t => t.key === key)?.label || key
  uni.showToast({ title: `已添加「${label}」模板`, icon: 'none' })
}

// 提醒开关
function onReminderChange(e) {
  if (e.detail.value) {
    form.reminderRule.taskReminderDays = [3, 1]
  } else {
    form.reminderRule.taskReminderDays = []
  }
}

// ---------- 数据加载 ----------

async function loadGroups() {
  try {
    const res = await api.myGroups()
    const list = Array.isArray(res) ? res : []
    groups.value = list
    if (list.length) {
      groupIdx.value = 0
      loadMembers(list[0].groupId)
    }
  } catch (e) {
    console.warn('[create] 加载小组失败:', e)
  }
}

async function loadMembers(groupId) {
  if (!groupId) return
  try {
    const res = await api.groupMembers(groupId, { pageNum: 1, pageSize: 50 })
    const list = (res && res.members) || (Array.isArray(res) ? res : [])
    // 默认全不选中
    memberPool.value = list.map(m => ({
      ...m,
      selected: false,
    }))
  } catch (e) {
    console.warn('[create] 加载成员失败:', e)
  }
}

// ---------- 提交 ----------

async function submitCreate() {
  if (submitting.value) return
  const group = selectedGroup.value
  if (!group) {
    uni.showToast({ title: '请选择小组', icon: 'none' })
    return
  }
  if (!form.name.trim()) {
    uni.showToast({ title: '请输入项目名称', icon: 'none' })
    return
  }
  const validStages = form.stages.filter(s => s.name.trim())
  if (!validStages.length) {
    uni.showToast({ title: '至少添加一个阶段', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    // 组装阶段数据（计算 startDate = 上一个 endDate + 1）
    const stages = []
    let prevEnd = ''
    validStages.forEach((s, i) => {
      const endDate = s.endDate ? String(s.endDate).slice(0, 10) : null
      let startDate = null
      if (prevEnd) {
        const d = new Date(prevEnd + 'T00:00:00')
        d.setDate(d.getDate() + 1)
        const m = String(d.getMonth() + 1).padStart(2, '0')
        const day = String(d.getDate()).padStart(2, '0')
        startDate = d.getFullYear() + '-' + m + '-' + day
      }
      stages.push({
        name: s.name.trim(),
        orderNum: i + 1,
        startDate,
        endDate,
      })
      if (endDate) prevEnd = endDate
    })

    // 组装成员
    const members = selectedMembers.value.map(m => ({
      userId: m.userId,
      role: m.role || 'member',
    }))

    // 创建项目
    const data = await api.createProject({
      groupId: group.groupId,
      name: form.name.trim(),
      description: form.description || '',
      stages,
      members,
      auditRule: form.auditRule,
      reminderRule: form.reminderRule,
    })

    const projectId = data && data.projectId
    if (!projectId) throw new Error('创建失败')

    // 创建任务
    const validTasks = form.tasks.filter(t => t.name.trim())
    if (validTasks.length) {
      const createdStages = (data.stages || [])
        .slice()
        .sort((a, b) => (a.orderNum || 0) - (b.orderNum || 0))

      if (createdStages.length) {
        // 获取当前用户ID作为兜底
        const profile = await api.getProfile().catch(() => null)
        const currentUserId = profile && profile.userId

        for (let i = 0; i < validTasks.length; i++) {
          const t = validTasks[i]
          const stageIdx = Math.min(t.stageIdx || 0, createdStages.length - 1)
          const st = createdStages[stageIdx]
          if (!st) continue

          // 负责人：当前选了谁？这里简化：默认当前用户
          const assigneeId = currentUserId

          let deadline = ''
          if (t.deadline && String(t.deadline).length >= 10) {
            deadline = String(t.deadline).slice(0, 10) + ' 23:59:59'
          } else {
            const d = new Date(Date.now() + 7 * 24 * 3600 * 1000)
            const m = String(d.getMonth() + 1).padStart(2, '0')
            const day = String(d.getDate()).padStart(2, '0')
            deadline = d.getFullYear() + '-' + m + '-' + day + ' 23:59:59'
          }

          await api.createTask({
            projectId,
            stageId: st.stageId,
            name: t.name.trim(),
            assigneeId,
            deadline,
          })
        }
      }
    }

    uni.showToast({ title: '创建成功', icon: 'success' })
    uni.setStorageSync('rocgit_current_project_id', projectId)

    setTimeout(() => {
      uni.redirectTo({ url: '/pages/project/project?id=' + projectId })
    }, 800)
  } catch (e) {
    uni.showToast({ title: (e && e.message) || '创建失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadGroups()
})
</script>

<style scoped>
.create-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f8fafc;
}

/* 导航栏 */
.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 44px 16px 12px;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
  flex-shrink: 0;
}
.nav-bar__back {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #475569;
}
.nav-bar__title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}
.nav-bar__placeholder {
  width: 32px;
}

/* 步骤条 */
.step-bar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 20px 24px;
  background: #fff;
  position: relative;
  flex-shrink: 0;
}
.step-bar__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  z-index: 1;
  flex: 1;
}
.step-bar__dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  color: #94a3b8;
}
.step-bar__item--active .step-bar__dot {
  background: #2563eb;
  color: #fff;
}
.step-bar__item--done .step-bar__dot {
  background: #10b981;
  color: #fff;
  font-size: 12px;
}
.step-bar__label {
  font-size: 11px;
  color: #94a3b8;
}
.step-bar__item--active .step-bar__label {
  color: #2563eb;
  font-weight: 500;
}
.step-bar__item--done .step-bar__label {
  color: #10b981;
}
.step-bar__line {
  position: absolute;
  top: 34px;
  height: 2px;
  background: #2563eb;
  z-index: 0;
  transition: width 0.3s;
}

/* 内容区 */
.content {
  flex: 1;
  overflow: hidden;
}
.step-content {
  padding: 16px;
}

.section {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 12px;
}
.section__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.section__title {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  display: block;
  margin-bottom: 12px;
}
.section__header .section__title {
  margin-bottom: 0;
}
.section__count {
  font-size: 12px;
  color: #94a3b8;
}
.required {
  color: #dc2626;
}

/* 表单控件 */
.form-input {
  height: 44px;
  padding: 0 14px;
  background: #f8fafc;
  border-radius: 10px;
  font-size: 14px;
  color: #0f172a;
}
.form-input__ph {
  color: #94a3b8;
}
.form-textarea {
  min-height: 80px;
  padding: 10px 14px;
  background: #f8fafc;
  border-radius: 10px;
  font-size: 14px;
  color: #0f172a;
  line-height: 1.5;
  width: 100%;
  box-sizing: border-box;
}
.form-picker {
  height: 44px;
  padding: 0 14px;
  background: #f8fafc;
  border-radius: 10px;
  font-size: 14px;
  color: #0f172a;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.form-picker__arrow {
  font-size: 16px;
  color: #94a3b8;
}
.form-tip {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: #94a3b8;
}
.form-tip__link {
  color: #2563eb;
}

/* 模板行 */
.template-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}
.template-row__label {
  font-size: 12px;
  color: #64748b;
}
.template-chip {
  padding: 4px 12px;
  background: #eff6ff;
  border-radius: 999px;
  font-size: 11px;
  color: #2563eb;
  font-weight: 500;
}

/* 阶段行 */
.stage-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}
.stage-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: #f8fafc;
  border-radius: 10px;
}
.stage-row__no {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  width: 24px;
  flex-shrink: 0;
}
.stage-row__input {
  flex: 1;
  font-size: 13px;
  color: #0f172a;
  background: transparent;
}
.stage-row__date {
  font-size: 11px;
  color: #64748b;
  flex-shrink: 0;
  padding: 4px 8px;
  background: #fff;
  border-radius: 6px;
  white-space: nowrap;
}
.stage-row__del {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #94a3b8;
  flex-shrink: 0;
}

/* 任务行 */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}
.task-row {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 12px;
  background: #f8fafc;
  border-radius: 10px;
}
.task-row__stage {
  font-size: 11px;
  color: #2563eb;
  background: #dbeafe;
  padding: 3px 8px;
  border-radius: 6px;
  flex-shrink: 0;
  white-space: nowrap;
  max-width: 70px;
  overflow: hidden;
  text-overflow: ellipsis;
}
.task-row__input {
  flex: 1;
  font-size: 13px;
  color: #0f172a;
  background: transparent;
  min-width: 0;
}
.task-row__date {
  font-size: 11px;
  color: #64748b;
  flex-shrink: 0;
  padding: 3px 6px;
  background: #fff;
  border-radius: 6px;
  white-space: nowrap;
}
.task-row__del {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #94a3b8;
  flex-shrink: 0;
}

/* 添加行按钮 */
.add-row-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 10px;
  background: #f1f5f9;
  border-radius: 10px;
  font-size: 13px;
  color: #64748b;
  border: 1px dashed #cbd5e1;
}
.add-row-btn__icon {
  font-size: 16px;
  font-weight: 500;
}

/* 成员池 */
.member-pool {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 8px;
}
.member-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px 6px 6px;
  background: #f1f5f9;
  border-radius: 999px;
  border: 1.5px solid transparent;
  position: relative;
  transition: all 0.2s;
}
.member-chip--on {
  background: #eff6ff;
  border-color: #2563eb;
}
.member-chip__avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
}
.member-chip__name {
  font-size: 12px;
  color: #475569;
  max-width: 70px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.member-chip--on .member-chip__name {
  color: #2563eb;
  font-weight: 500;
}
.member-chip__check {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 16px;
  height: 16px;
  background: #2563eb;
  border-radius: 50%;
  color: #fff;
  font-size: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
}

/* 规则项 */
.rule-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid #f1f5f9;
}
.rule-item:last-child {
  border-bottom: none;
}
.rule-item__info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.rule-item__title {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}
.rule-item__desc {
  font-size: 12px;
  color: #94a3b8;
}

/* 概览卡片 */
.overview-card {
  background: #f8fafc;
  border-radius: 12px;
  padding: 4px 0;
}
.overview-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
}
.overview-row__label {
  font-size: 13px;
  color: #64748b;
}
.overview-row__value {
  font-size: 13px;
  font-weight: 500;
  color: #0f172a;
}

.bottom-spacing {
  height: 100px;
}

/* 底部按钮栏 */
.bottom-bar {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1px solid #f1f5f9;
  flex-shrink: 0;
}
.bottom-bar__btn {
  flex: 1;
  padding: 13px;
  border-radius: 12px;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
}
.bottom-bar__btn--ghost {
  background: #f1f5f9;
  color: #64748b;
}
.bottom-bar__btn--primary {
  background: #2563eb;
  color: #fff;
}
.bottom-bar__btn--disabled {
  opacity: 0.6;
}
</style>
