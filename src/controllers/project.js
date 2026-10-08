import data from '../data/project.json'
import { createPageController } from '../utils/page-controller.js'
import { loadProject } from '../utils/bind-api.js'
import api from '../utils/api.js'

// Original prototype actions operate on reactive Vue state through adapters.
// No browser DOM, inline JavaScript or eval is required in the mini program.
export default function () {
  const controller = createPageController(data)
  const { document, location, navigator, setTimeout } = controller.adapters

  // 接入真实接口：读取创建页写入的项目 id（无则兜底项目 1，不破坏其他入口）
  const pid = Number(uni.getStorageSync('rocgit_current_project_id')) || 1
  loadProject(controller, pid)
  
  function switchTab(name) {
    document.querySelectorAll('.tab-content').forEach(function(i){i.classList.add('hidden')});
    document.getElementById(name).classList.remove('hidden');
    document.querySelectorAll('.tab').forEach(function(b){
      b.className = 'tab flex-1 py-3 text-[13px] font-medium ' + (b.dataset.tab===name?'text-blue-600 border-b-2 border-blue-600':'text-slate-400 border-b-2 border-transparent');
    });
  }

  // 多维检索（入口卡片 n600 打开；applyFilter 落地为本地真实筛选）
  function showFilters(){document.getElementById('filterModal').classList.remove('hidden')}
  function hideFilters(){document.getElementById('filterModal').classList.add('hidden')}
  function toggleFormat(btn){
    var on=btn.className.indexOf('bg-blue-600')>=0;
    btn.className=on
      ? btn.className.replace('bg-blue-600 text-white','bg-slate-100 text-slate-600')
      : btn.className.replace('bg-slate-100 text-slate-600','bg-blue-600 text-white');
  }
  function activeChips(scopeSel){
    return document.querySelectorAll(scopeSel).map(function(b){return {el:b,on:b.className.indexOf('bg-blue-600')>=0}})
  }
  // 本地筛选文件行：关键词(输入框) + 选中格式/阶段/上传者/状态芯片 逐行匹配文本
  function applyFilter(){
    var kw=(document.getElementById('filterKeyword')||{value:''}).value||'';
    var active=[].concat(
      activeChips('#filterModal .format-chip'),
      activeChips('#filterModal .stage-chip'),
      activeChips('#filterModal .uploader-chip'),
      activeChips('#filterModal .status-chip')
    ).filter(function(c){return c.on}).map(function(c){return (c.el.textContent||'').trim()});
    var rows=document.querySelectorAll('.file-row'), matched=0;
    rows.forEach(function(r){
      var text=(r.textContent||'').toLowerCase();
      var kwOk=!kw || text.indexOf(kw.toLowerCase())>=0;
      var chipOk=active.every(function(t){return text.indexOf(t.toLowerCase())>=0});
      var show=kwOk&&chipOk;
      r.style.display=show?'':'none';
      if(show)matched++;
    });
    hideFilters();
    document.getElementById('activeFilters').classList.remove('hidden');
    document.getElementById('filterBadge').classList.remove('hidden');
    showToast(matched+' 条文件匹配');
  }
  function clearFilter(){
    document.getElementById('activeFilters').classList.add('hidden');
    document.getElementById('filterBadge').classList.add('hidden');
    document.querySelectorAll('.file-row').forEach(function(r){r.style.display=''});
  }

  // 批量清理模式（真实勾选集合 + batch-delete/batch-move 接口）
  var batchMode=false,batchSelIds=[];
  function toggleBatch(){
    batchMode=!batchMode;batchSelIds=[];
    document.getElementById('batchBar').classList.toggle('hidden',!batchMode);
    document.getElementById('batchCount').textContent=0;
    document.querySelectorAll('[data-batch-check]').forEach(function(b){b.classList.toggle('hidden',!batchMode)});
    document.getElementById('batchBtn').classList.toggle('bg-blue-100',batchMode);
    document.getElementById('batchBtn').classList.toggle('bg-slate-50',!batchMode);
  }
  function batchAction(type){
    if(batchSelIds.length===0){showToast('请先选择文件');return}
    if(type==='delete'){document.getElementById('batchConfirm').classList.remove('hidden')}
    else if(type==='hide'){
      // 批量移动（归档语义：移到目标阶段）
      api.projectStages(pid).then(function(stages){
        var list=stages||[];
        if(!list.length){showToast('暂无阶段可移动');return}
        uni.showActionSheet({
          itemList:list.map(function(s){return s.name||('阶段'+s.orderNum)}),
          success:function(res){
            api.batchMoveFile(batchSelIds,list[res.tapIndex].stageId).then(function(){
              showToast('已移动 '+batchSelIds.length+' 项');
              toggleBatch();loadProject(controller,pid)
            }).catch(function(err){showToast((err&&err.message)||'移动失败')})
          }
        })
      }).catch(function(){showToast('获取阶段失败')})
    }
  }
  function hideBatchConfirm(){document.getElementById('batchConfirm').classList.add('hidden')}
  function doBatchDelete(){
    if(document.getElementById('confirmInput').value.trim()!=='清理'){showToast('请输入「清理」确认');return}
    api.batchDeleteFile(batchSelIds).then(function(){
      hideBatchConfirm();
      toggleBatch();
      showToast('已删除 '+batchSelIds.length+' 项');
      loadProject(controller,pid)
    }).catch(function(err){showToast((err&&err.message)||'删除失败')})
  }

  // 文件操作菜单
  function openFileMenu(el){
    if(batchMode)return;
    document.getElementById('fileMenu').classList.remove('hidden');
  }
  function hideFileMenu(){document.getElementById('fileMenu').classList.add('hidden')}
  function toggleHidden(){
    document.getElementById('hiddenList').classList.toggle('hidden');
  }

  // 邀请链接（功能点1）：展开时拉取小组真实邀请码；无则自动生成；复制真实链接
  function showInvite(){
    document.getElementById('inviteSection').classList.remove('hidden');
    if (!controller.groupId) { showToast('未获取到小组信息'); return }
    api.myGroups().then(function (groups) {
      var g = (groups || []).find(function (x) { return x.groupId === controller.groupId })
      var code = g && g.inviteCode
      if (code) { setInviteLink(code); return }
      // 无邀请码 → 重新生成
      api.regenerateInviteCode(controller.groupId).then(function (res) {
        var nc = res && (res.inviteCode || res.code)
        if (nc) setInviteLink(nc)
        else showToast('获取邀请码失败')
      }).catch(function (err) { showToast((err && err.message) || '生成邀请码失败') })
    }).catch(function () { showToast('获取小组信息失败') })
  }
  function setInviteLink(code){
    var host = (typeof location !== 'undefined' && location.host) ? location.host : 'roc-git.com'
    document.getElementById('inviteLink').value = 'https://' + host + '/join?code=' + code
  }
  function hideInvite(){document.getElementById('inviteSection').classList.add('hidden')}
  function copyLink(){
    var input=document.getElementById('inviteLink');
    if(navigator.clipboard){navigator.clipboard.writeText(input.value)}
    showToast('邀请链接已复制');
  }

  // 阶段编辑（功能点2）
  function editStage(el){
    document.getElementById('editStageModal').classList.remove('hidden');
  }
  function hideEditStage(){document.getElementById('editStageModal').classList.add('hidden')}
  function deleteStage(el){
    uni.showModal({content:'确定删除此阶段？',success(result){if(result.confirm){el.closest('.bg-white').remove();showToast('已删除')}}})
  }
  function showAddStage(){document.getElementById('addStageModal').classList.remove('hidden')}
  function hideAddStage(){document.getElementById('addStageModal').classList.add('hidden')}
  function applyTemplate(){showToast('已应用系统默认模板')}
  function addTaskInStage(){showToast('请在「添加」中输入任务')}

  // 溯源搜索（功能点13）
  function showVersionDiff(){document.getElementById('versionDiffModal').classList.remove('hidden')}
  function hideVersionDiff(){document.getElementById('versionDiffModal').classList.add('hidden')}
  function showVersionHistory(name){showToast('版本历史：'+name)}

  function showUpload(){document.getElementById('uploadModal').classList.remove('hidden')}
  function hideUpload(){document.getElementById('uploadModal').classList.add('hidden')}
  function toggleMore(){document.getElementById('moreModal').classList.toggle('hidden')}
  function hideMore(){document.getElementById('moreModal').classList.add('hidden')}
  function showToast(msg){
    var t=document.getElementById('toast');t.textContent=msg;t.classList.remove('hidden');
    setTimeout(function(){t.classList.add('hidden')},1500);
  }

  controller.setHandlers({
n8: function(event) { toggleMore() },
n27: function(event) { switchTab('files') },
n28: function(event) { switchTab('stages') },
n29: function(event) { switchTab('members') },
n30: function(event) { switchTab('trace') },
n33: function(event) { showUpload() },
n36: function(event) { showFilters() },
n38: function(event) { showFilters() },
n600: function(event) { showFilters() },
n41: function(event) { toggleBatch() },
n45: function(event) { clearFilter(this) },
n48: function(event) { clearFilter(this) },
n51: function(event) { clearFilter(this) },
n57: function(event) { batchAction('hide') },
n58: function(event) { batchAction('delete') },
n59: function(event) { toggleBatch() },
n63: function(event) { toggleCheck(this) },
n67: function(event) { openFileMenu(this) },
n72: function(event) { event.stopPropagation();showToast('已选择下载') },
n74: function(event) { event.stopPropagation();showToast('已转发') },
n76: function(event) { event.stopPropagation();openFileMenu(this) },
n80: function(event) { toggleCheck(this) },
n84: function(event) { openFileMenu(this) },
n89: function(event) { event.stopPropagation();showToast('已选择下载') },
n91: function(event) { event.stopPropagation();showToast('已转发') },
n93: function(event) { event.stopPropagation();openFileMenu(this) },
n97: function(event) { toggleCheck(this) },
n101: function(event) { openFileMenu(this) },
n106: function(event) { event.stopPropagation();showToast('已驳回') },
n108: function(event) { event.stopPropagation();showToast('已通过') },
n110: function(event) { event.stopPropagation();openFileMenu(this) },
n114: function(event) { toggleCheck(this) },
n118: function(event) { openFileMenu(this) },
n122: function(event) { event.stopPropagation();showToast('已还原') },
n133: function(event) { showToast('已选择下载') },
n135: function(event) { showToast('已转发') },
n139: function(event) { toggleCheck(this) },
n143: function(event) { openFileMenu(this) },
n148: function(event) { event.stopPropagation();showToast('已驳回') },
n150: function(event) { event.stopPropagation();showToast('已通过') },
n152: function(event) { event.stopPropagation();openFileMenu(this) },
n155: function(event) { toggleHidden() },
n168: function(event) { event.stopPropagation();showToast('已恢复') },
n172: function(event) { showAddStage() },
n180: function(event) { editStage(this) },
n190: function(event) { editStage(this) },
n192: function(event) { deleteStage(this) },
n205: function(event) { addTaskInStage() },
n210: function(event) { this.parentElement.remove() },
n216: function(event) { this.parentElement.remove() },
n222: function(event) { this.parentElement.remove() },
n230: function(event) { editStage(this) },
n232: function(event) { deleteStage(this) },
n235: function(event) {
    // 应用模板：拉真实模板列表，选择后提示（后端详情页无"应用模板"接口，模板在创建项目时选择）
    api.projectTemplateList().then(function (list) {
      var names = (list || []).map(function (t) { return t.name || ('模板' + t.templateId) })
      if (!names.length) { showToast('暂无可用模板'); return }
      uni.showActionSheet({
        itemList: names,
        success: function () { showToast('模板将在创建新项目时应用（详情页暂不支持替换阶段）') },
        fail: function () { /* 取消 */ }
      })
    }).catch(function () { showToast('获取模板失败') })
  },
n240: function(event) { showInvite() },
n244: function(event) { hideInvite() },
n250: function(event) { copyLink() },
n253: function(event) { showToast('已发送微信邀请') },
n255: function(event) { showToast('已生成二维码') },
n257: function(event) { showToast('已复制') },
n283: function(event) { showToast('已取消') },
n309: function(event) { showVersionDiff() },
n311: function(event) { showVersionDiff() },
// n320: function(event) { location.href='meeting.html' },  // 研讨记录入口已下线（任务：前端隐藏，后端保留）
n329: function(event) { showVersionHistory('访谈记录') },
n337: function(event) { hideVersionDiff() },
n338: function(event) { event.stopPropagation() },
n341: function(event) { hideVersionDiff() },
n364: function(event) { showToast('请在版本列表中选择要回滚的版本');hideVersionDiff() },
n365: function(event) { hideVersionDiff() },
n366: function(event) { hideEditStage() },
n367: function(event) { event.stopPropagation() },
n370: function(event) { hideEditStage() },
n383: function(event) { showToast('已删除阶段');hideEditStage() },
n384: function(event) { showToast('已保存');hideEditStage() },
n385: function(event) { hideAddStage() },
n386: function(event) { event.stopPropagation() },
n389: function(event) { hideAddStage() },
n401: function(event) { showToast('阶段已添加');hideAddStage() },
n402: function(event) { hideFilters() },
n403: function(event) { event.stopPropagation() },
n406: function(event) { hideFilters() },
n417: function(event) { toggleFormat(this) },
n418: function(event) { toggleFormat(this) },
n419: function(event) { toggleFormat(this) },
n420: function(event) { toggleFormat(this) },
n421: function(event) { toggleFormat(this) },
n422: function(event) { toggleFormat(this) },
n427: function(event) { toggleFormat(this) },
n428: function(event) { toggleFormat(this) },
n429: function(event) { toggleFormat(this) },
n434: function(event) { toggleFormat(this) },
n436: function(event) { toggleFormat(this) },
n438: function(event) { toggleFormat(this) },
n444: function(event) { toggleFormat(this) },
n445: function(event) { toggleFormat(this) },
n446: function(event) { toggleFormat(this) },
n447: function(event) { toggleFormat(this) },
n456: function(event) { clearFilter() },
n457: function(event) { applyFilter() },
n458: function(event) { hideFileMenu() },
n459: function(event) { event.stopPropagation() },
n467: function(event) {
    // 闲置 → 收藏 toggle（后端语义映射：favorite）
    hideFileMenu()
    if (!currentFileId) { showToast('请先选择文件'); return }
    api.favoriteFile(currentFileId).then(function (res) {
      showToast(res && res.isFavorite ? '已收藏' : '已取消收藏')
      loadProject(controller, pid)
    }).catch(function (err) { showToast((err && err.message) || '操作失败') })
  },
n472: function(event) {
    // 归档 → 移动到阶段（后端语义映射：move 到目标阶段）
    hideFileMenu()
    if (!currentFileId) { showToast('请先选择文件'); return }
    api.projectStages(pid).then(function (stages) {
      var list = stages || []
      if (!list.length) { showToast('暂无阶段可归档'); return }
      uni.showActionSheet({
        itemList: list.map(function (s) { return s.name || ('阶段' + s.orderNum) }),
        success: function (res) {
          api.moveFile(currentFileId, list[res.tapIndex].stageId).then(function () {
            showToast('已移动')
            loadProject(controller, pid)
          }).catch(function (err) { showToast((err && err.message) || '移动失败') })
        }
      })
    }).catch(function () { showToast('获取阶段失败') })
  },
n477: function(event) {
    // 冗余 → 删除（后端语义映射：batch-delete 单文件）
    hideFileMenu()
    if (!currentFileId) { showToast('请先选择文件'); return }
    uni.showModal({
      content: '确定删除该文件（冗余清理）？',
      success: function (res) {
        if (!res.confirm) return
        api.batchDeleteFile([currentFileId]).then(function () {
          showToast('已删除')
          loadProject(controller, pid)
        }).catch(function (err) { showToast((err && err.message) || '删除失败') })
      }
    })
  },
n482: function(event) { hideFileMenu() },
n489: function(event) { hideBatchConfirm() },
n490: function(event) { doBatchDelete() },
n495: function(event) { hideUpload() },
n497: function(event) {
    // 触发 H5 原生文件选择：运行时动态创建原生 input（模板静态 input 会被 uni 编译，不弹系统选择器）
    // #ifdef H5
    openFilePicker()
    return
    // #endif
    showToast('当前环境暂不支持文件选择')
  },
n503: function(event) { showToast('已连接聊天');hideUpload() },
n509: function(event) { showToast('已还原');hideUpload() },
n515: function(event) { toggleMore() },
n516: function(event) { event.stopPropagation() },
n519: function(event) { toggleMore() },
n521: function(event) {
  // 重命名：输入新名 → renameFile → 刷新
  hideMore()
  if (!currentFileId) { showToast('请先选择文件'); return }
  uni.showModal({
    title: '重命名文件', editable: true, placeholderText: '输入新文件名',
    success: function (res) {
      if (!res.confirm) return
      const name = (res.content || '').trim()
      if (!name) { showToast('文件名不能为空'); return }
      api.renameFile(currentFileId, name).then(function () {
        showToast('已重命名')
        loadProject(controller, pid)
      }).catch(function (err) { showToast((err && err.message) || '重命名失败') })
    }
  })
},
n524: function(event) { hideMore();showToast('设置暂未开放') },
n527: function(event) {
  // 归档：确认 → archiveProject → 刷新（首页当前项目自动消失、归档模块出现）
  hideMore()
  uni.showModal({
    title: '归档项目', content: '归档后项目将从当前列表移至归档模块，可随时查看。确定归档？',
    success: function (res) {
      if (!res.confirm) return
      api.archiveProject(pid).then(function () {
        showToast('已归档')
        loadProject(controller, pid)
      }).catch(function (err) { showToast((err && err.message) || '归档失败') })
    }
  })
},
    // 阶段弹窗：确认添加（addStageModal 表单 n394 名称 / n397 日期 / n400 描述）
    n401: function(event) {
      const name = (controller.ui.n394.value || '').trim()
      if (!name) { showToast('请输入阶段名称'); return }
      api.createStage(pid, { name: name, startDate: controller.ui.n397.value || null, endDate: controller.ui.n397.value || null }).then(function () {
        showToast('阶段已添加')
        hideAddStage()
        controller.ui.n394.value = ''
        controller.ui.n397.value = ''
        loadProject(controller, pid)
      }).catch(function (err) { showToast((err && err.message) || '添加失败') })
    },
    // 阶段弹窗：删除（editStageModal 底部按钮）
    n383: function(event) {
      if (!editStageId) { hideEditStage(); return }
      uni.showModal({
        content: '确定删除此阶段？',
        success: function (res) {
          if (!res.confirm) return
          api.deleteStage(pid, editStageId).then(function () {
            showToast('已删除')
            editStageId = null
            hideEditStage()
            loadProject(controller, pid)
          }).catch(function (err) { showToast((err && err.message) || '删除失败') })
        }
      })
    },
    // 阶段弹窗：保存（editStageModal 表单 n375 名称 / n378 日期）
    n384: function(event) {
      if (!editStageId) { hideEditStage(); return }
      const name = (controller.ui.n375.value || '').trim()
      if (!name) { showToast('请输入阶段名称'); return }
      api.updateStage(pid, editStageId, { name: name, startDate: controller.ui.n378.value || null, endDate: controller.ui.n378.value || null }).then(function () {
        showToast('已保存')
        editStageId = null
        hideEditStage()
        loadProject(controller, pid)
      }).catch(function (err) { showToast((err && err.message) || '保存失败') })
    }
  })

  // 文件选择回调：取原生 input.files[0] → 上传 → 成功关弹窗并重跑 loadProject 刷新文件列表
  var resubmitFileId = null
  // 当前编辑中的阶段 id（editStageModal 保存/删除用）
  var editStageId = null
  // 当前查看版本的文件 id（versionDiffModal）
  var versionFileId = null
  // 动态审核动作委托（buildFileRowHtml 的 data-action 按钮，page-controller.tap 分发）
  controller.setDynActions({
    'approve-file': function (attrs) {
      api.approveFile(Number(attrs['data-file-id'])).then(function () {
        showToast('已通过')
        loadProject(controller, pid)
      }).catch(function (err) { showToast((err && err.message) || '操作失败') })
    },
    'reject-file': function (attrs) {
      uni.showModal({
        title: '驳回原因', editable: true, placeholderText: '请输入驳回原因',
        success: function (res) {
          if (!res.confirm) return
          api.rejectFile(Number(attrs['data-file-id']), (res.content || '').trim() || '未填写原因').then(function () {
            showToast('已驳回')
            loadProject(controller, pid)
          }).catch(function (err) { showToast((err && err.message) || '操作失败') })
        }
      })
    },
    'resubmit-file': function (attrs) {
      resubmitFileId = Number(attrs['data-file-id'])
      // #ifdef H5
      openFilePicker()
      return
      // #endif
      showToast('当前环境暂不支持文件选择')
    },
    // 阶段行：编辑（预填弹窗表单并记录 stageId）
    'edit-stage': function (attrs) {
      editStageId = Number(attrs['data-stage-id'])
      api.projectStages(pid).then(function (stages) {
        const s = (stages || []).find(x => x.stageId === editStageId)
        controller.ui.n375.value = (s && s.name) || ''
        controller.ui.n378.value = (s && s.endDate) ? String(s.endDate).slice(0, 10) : ''
        document.getElementById('editStageModal').classList.remove('hidden')
      }).catch(function () { showToast('获取阶段信息失败') })
    },
    // 阶段行：删除（确认后真实删除并刷新）
    'delete-stage': function (attrs) {
      const sid = Number(attrs['data-stage-id'])
      uni.showModal({
        content: '确定删除此阶段？',
        success: function (res) {
          if (!res.confirm) return
          api.deleteStage(pid, sid).then(function () {
            showToast('已删除')
            loadProject(controller, pid)
          }).catch(function (err) { showToast((err && err.message) || '删除失败') })
        }
      })
    },
    // 文件行：打开更多菜单（记录当前文件 id）
    'file-menu': function (attrs) {
      currentFileId = Number(attrs['data-file-id'])
      document.getElementById('fileMenu').classList.remove('hidden')
    },
    // 文件行：历史版本（拉真实版本列表渲染进弹窗）
    'file-version': function (attrs) {
      versionFileId = Number(attrs['data-file-id'])
      api.versionList(versionFileId).then(function (list) {
        var versions = list || []
        var html = versions.length
          ? versions.map(function (v) {
              var isCur = v.version === v.currentVersion || v.isCurrent
              return '<div class="flex items-center justify-between py-2.5 border-b border-slate-100 last:border-0">' +
                '<div class="flex-1 min-w-0">' +
                '<p class="text-[13px] font-medium text-slate-900">v' + (v.version || '') + (isCur ? ' <text class="text-emerald-500 text-[10px]">当前</text>' : '') + '</p>' +
                '<p class="text-[11px] text-slate-400">' + (v.remark || v.name || '') + ' · ' + (v.createdAt ? String(v.createdAt).slice(0, 16).replace('T', ' ') : '') + '</p>' +
                '</div>' +
                (isCur ? '' : '<span class="px-2.5 py-1 rounded-lg bg-blue-50 text-blue-600 text-[11px] font-medium shrink-0 ml-2" data-action="version-rollback" data-version="' + v.version + '">回滚</span>') +
                '</div>'
            }).join('')
          : '<p class="text-[12px] text-slate-400 py-4 text-center">暂无历史版本</p>'
        controller.appendHtml('n343', html)
        document.getElementById('versionDiffModal').classList.remove('hidden')
      }).catch(function (err) { showToast((err && err.message) || '获取版本失败') })
    },
    // 版本行：回滚（确认后 rollback → 刷新）
    'version-rollback': function (attrs) {
      const target = Number(attrs['data-version'])
      uni.showModal({
        content: '确定回滚到 v' + target + '？当前版本将更新为该版本内容。',
        success: function (res) {
          if (!res.confirm) return
          api.versionRollback(versionFileId, target).then(function () {
            showToast('已回滚至 v' + target)
            hideVersionDiff()
            loadProject(controller, pid)
          }).catch(function (err) { showToast((err && err.message) || '回滚失败') })
        }
      })
    },
    // 文件行：批量勾选切换（this = 勾选框节点）
    'batch-toggle': function (attrs) {
      if (!batchMode) return
      const id = Number(attrs['data-file-id'])
      const idx = batchSelIds.indexOf(id)
      if (idx >= 0) {
        batchSelIds.splice(idx, 1)
        this.className = String(this.className).replace('bg-blue-600 border-blue-600 text-white', 'border-slate-300 text-slate-400')
      } else {
        batchSelIds.push(id)
        this.className = String(this.className).replace('border-slate-300 text-slate-400', 'bg-blue-600 border-blue-600 text-white')
      }
      document.getElementById('batchCount').textContent = batchSelIds.length
    },
    // 任务行：完成（completeTask）
    'complete-task': function (attrs) {
      api.completeTask(Number(attrs['data-task-id']), {}).then(function () {
        showToast('任务已完成')
        loadProject(controller, pid)
      }).catch(function (err) { showToast((err && err.message) || '操作失败') })
    },
    // 任务行：删除（确认后真实删除并刷新）
    'delete-task': function (attrs) {
      const tid = Number(attrs['data-task-id'])
      uni.showModal({
        content: '确定删除此任务？',
        success: function (res) {
          if (!res.confirm) return
          api.deleteTask(tid).then(function () {
            showToast('已删除')
            loadProject(controller, pid)
          }).catch(function (err) { showToast((err && err.message) || '删除失败') })
        }
      })
    }
  })
  // H5 原生文件选择器：运行时动态创建 input（静态模板 input 会被 uni 编译失效）
  // #ifdef H5
  function openFilePicker() {
    var nativeDoc = (typeof window !== 'undefined') ? window.document : null
    if (!nativeDoc) { showToast('当前环境暂不支持文件选择'); return }
    var input = nativeDoc.createElement('input')
    input.type = 'file'
    input.style.display = 'none'
    nativeDoc.body.appendChild(input)
    input.onchange = function (e) { controller.onFileChange(e) }
    input.onclick = function () { /* 空：阻止冒泡即可 */ }
    input.click()
    // 用后移除（change 后延迟清理，避免文件句柄先释放）
    setTimeout(function () {
      if (input.parentNode) input.parentNode.removeChild(input)
    }, 1000)
  }
  // #endif
  controller.onFileChange = function (e) {
    var f = null
    // #ifdef H5
    var el = e && (e.target || (e.detail && e.detail.target))
    f = el && el.files && el.files[0]
    // #endif
    if (!f) { showToast('未选择文件'); return }
    showToast('上传中…')
    if (resubmitFileId) {
      api.resubmitFile(resubmitFileId, f).then(function () {
        showToast('已重新提交')
        resubmitFileId = null
        hideUpload()
        loadProject(controller, pid)
      }).catch(function (err) {
        showToast((err && err.message) || '重传失败')
        resubmitFileId = null
      })
      return
    }
    api.uploadFile(f, { projectId: pid }).then(function () {
      showToast('上传成功')
      hideUpload()
      loadProject(controller, pid)
    }).catch(function (err) {
      showToast((err && err.message) || '上传失败')
    })
  }

  // 完成项目：确认 → completeProject → 刷新（首页该项目移入归档模块，进度 100%）
  controller.doCompleteProject = function () {
    hideMore()
    uni.showModal({
      title: '完成项目', content: '结项后项目状态将变为已完成（进度 100%），可在归档模块查看。确定完成？',
      success: function (res) {
        if (!res.confirm) return
        api.completeProject(pid).then(function () {
          showToast('已结项')
          loadProject(controller, pid)
        }).catch(function (err) { showToast((err && err.message) || '结项失败') })
      }
    })
  }
  return controller
}
