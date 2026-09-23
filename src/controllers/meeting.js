import data from '../data/meeting.json'
import { createPageController } from '../utils/page-controller.js'
import { loadMeeting } from '../utils/bind-api.js'

// Original prototype actions operate on reactive Vue state through adapters.
// No browser DOM, inline JavaScript or eval is required in the mini program.
export default function () {
  const controller = createPageController(data)
  const { document, location, navigator, setTimeout } = controller.adapters

  // 接入真实接口：后端研讨记录覆盖原型数据（无路由参数，默认项目 1）
  loadMeeting(controller, 1)
  
  function switchMTab(name){
    document.querySelectorAll('.m-tab').forEach(function(b){
      b.className='m-tab flex-1 py-3 text-[13px] font-medium '+(b.dataset.tab===name?'text-blue-600 border-b-2 border-blue-600':'text-slate-400 border-b-2 border-transparent');
    });
    document.getElementById('m-list').classList.toggle('hidden',name!=='list');
    document.getElementById('m-conclusions').classList.toggle('hidden',name!=='conclusions');
  }
  function showNewMeeting(){document.getElementById('newMeetingModal').classList.remove('hidden')}
  function hideNewMeeting(){document.getElementById('newMeetingModal').classList.add('hidden')}
  function setType(t){
    document.getElementById('typeOnline').className=(t==='online'?'py-3 rounded-xl bg-blue-600 text-white':'py-3 rounded-xl bg-slate-100 text-slate-600')+' text-[12px] font-medium flex items-center justify-center gap-2';
  }
  function addUpdate(){
    var d=document.getElementById('updates');
    d.insertAdjacentHTML('beforeend','<div class="flex items-center gap-2"><input class="flex-1 bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-[12px] outline-none" placeholder="落地任务（如：更新任务截止日期）"><input type="date" class="bg-slate-50 border border-slate-200 rounded-xl px-2 py-2 text-[11px] outline-none w-28"></div>');
  }
  function saveMeeting(){
    hideNewMeeting();
    showToast('研讨记录已保存');
  }
  function showToast(msg){var t=document.getElementById('toast');t.textContent=msg;t.classList.remove('hidden');setTimeout(function(){t.classList.add('hidden')},1500)}

  controller.setHandlers({
n8: function(event) { showNewMeeting() },
n11: function(event) { switchMTab('list') },
n12: function(event) { switchMTab('conclusions') },
n109: function(event) { showToast('跳转更新任务') },
n116: function(event) { showToast('跳转更新任务') },
n123: function(event) { hideNewMeeting() },
n124: function(event) { event.stopPropagation() },
n127: function(event) { hideNewMeeting() },
n132: function(event) { setType('online') },
n134: function(event) { setType('offline') },
n154: function(event) { this.parentElement.remove() },
n156: function(event) { this.parentElement.remove() },
n158: function(event) { this.parentElement.remove() },
n166: function(event) { addUpdate() },
n169: function(event) { saveMeeting() }
  })
  return controller
}
