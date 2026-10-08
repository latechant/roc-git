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

  // 批量清理模式
  var batchMode=false,batchSel=0;
  function toggleBatch(){
    batchMode=!batchMode;batchSel=0;
    document.getElementById('batchBar').classList.toggle('hidden',!batchMode);
    document.getElementById('batchCount').textContent=0;
    document.querySelectorAll('.batch-check').forEach(function(b){b.classList.toggle('hidden',!batchMode)});
    document.getElementById('batchBtn').classList.toggle('bg-blue-100',batchMode);
    document.getElementById('batchBtn').classList.toggle('bg-slate-50',!batchMode);
  }
  function toggleCheck(el){
    var c=el.querySelector('iconify-icon');
    var checked=c.classList.toggle('hidden')?false:true;
    if(checked){el.classList.add('bg-blue-600','border-blue-600');el.classList.remove('border-slate-300');batchSel++}
    else{el.classList.remove('bg-blue-600','border-blue-600');el.classList.add('border-slate-300');batchSel--}
    document.getElementById('batchCount').textContent=batchSel;
  }
  function batchAction(type){
    if(batchSel===0){showToast('请先选择文件');return}
    if(type==='delete'){document.getElementById('batchConfirm').classList.remove('hidden')}
    else if(type==='hide'){
      document.querySelectorAll('.file-row').forEach(function(r){
        var c=r.querySelector('.batch-check');
        if(c&&c.classList.contains('bg-blue-600')){r.style.display='none'}
      });
      toggleBatch();
      showToast('已归档隐藏 '+batchSel+' 项');
      document.getElementById('hiddenSection').classList.remove('hidden');
    }
  }
  function hideBatchConfirm(){document.getElementById('batchConfirm').classList.add('hidden')}
  function doBatchDelete(){
    if(document.getElementById('confirmInput').value.trim()!=='清理'){showToast('请输入「清理」确认');return}
    document.querySelectorAll('.file-row').forEach(function(r){
      var c=r.querySelector('.batch-check');
      if(c&&c.classList.contains('bg-blue-600')){r.remove()}
    });
    hideBatchConfirm();
    toggleBatch();
    showToast('已清理冗余');
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

  // 邀请链接（功能点1）
  function showInvite(){document.getElementById('inviteSection').classList.remove('hidden')}
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
n235: function(event) { applyTemplate() },
n240: function(event) { showInvite() },
n244: function(event) { hideInvite() },
n250: function(event) { copyLink() },
n253: function(event) { showToast('已发送微信邀请') },
n255: function(event) { showToast('已生成二维码') },
n257: function(event) { showToast('已复制') },
n283: function(event) { showToast('已取消') },
n309: function(event) { showVersionDiff() },
n311: function(event) { showVersionDiff() },
n320: function(event) { location.href='meeting.html' },
n329: function(event) { showVersionHistory('访谈记录') },
n337: function(event) { hideVersionDiff() },
n338: function(event) { event.stopPropagation() },
n341: function(event) { hideVersionDiff() },
n364: function(event) { showToast('已回滚至 v3');hideVersionDiff() },
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
n467: function(event) { hideFileMenu();showToast('已标记闲置') },
n472: function(event) { hideFileMenu();showToast('已归档隐藏') },
n477: function(event) { hideFileMenu();showToast('已标为冗余') },
n482: function(event) { hideFileMenu() },
n489: function(event) { hideBatchConfirm() },
n490: function(event) { doBatchDelete() },
n495: function(event) { hideUpload() },
n497: function(event) {
    // 触发 H5 原生文件选择（模板静态隐藏 input#fileUploadInput；window.document 为原生 DOM，非适配器代理）
    // #ifdef H5
    var nativeDoc = (typeof window !== 'undefined') ? window.document : null
    var el = nativeDoc && nativeDoc.getElementById('fileUploadInput')
    if (el) { el.click(); return }
    // #endif
    showToast('当前环境暂不支持文件选择')
  },
n503: function(event) { showToast('已连接聊天');hideUpload() },
n509: function(event) { showToast('已还原');hideUpload() },
n515: function(event) { toggleMore() },
n516: function(event) { event.stopPropagation() },
n519: function(event) { toggleMore() },
n521: function(event) { hideMore();showToast('已重命名') },
n524: function(event) { hideMore();showToast('已设置') },
n527: function(event) { hideMore();showToast('已归档') }
  })

  // 文件选择回调：取原生 input.files[0] → 上传 → 成功关弹窗并重跑 loadProject 刷新文件列表
  controller.onFileChange = function (e) {
    var f = null
    // #ifdef H5
    var el = e && (e.target || (e.detail && e.detail.target))
    f = el && el.files && el.files[0]
    // #endif
    if (!f) { showToast('未选择文件'); return }
    showToast('上传中…')
    api.uploadFile(f, { projectId: pid }).then(function () {
      showToast('上传成功')
      hideUpload()
      loadProject(controller, pid)
    }).catch(function (err) {
      showToast((err && err.message) || '上传失败')
    })
  }
  return controller
}
