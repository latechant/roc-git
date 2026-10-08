import data from '../data/messages.json'
import { createPageController } from '../utils/page-controller.js'
import { loadMessages } from '../utils/bind-api.js'
import api from '../utils/api.js'

// Original prototype actions operate on reactive Vue state through adapters.
// No browser DOM, inline JavaScript or eval is required in the mini program.
export default function () {
  const controller = createPageController(data)
  const { document, location, navigator, setTimeout } = controller.adapters

  // 接入真实接口：后端未读数/消息列表覆盖原型数据
  loadMessages(controller)
  
  // 全部已读：调用后端 read-all 后重刷未读数与消息列表（红点由 read 字段控制）
  function markRead(){
    api.readAll().then(function(){ loadMessages(controller) }).catch(function(){})
  }

  // 动态消息行点击：标记单条已读 + 按类型跳转（audit/deadline/version → 项目详情页）
  controller.setDynActions({
    'msg-open': function (attrs) {
      const mid = attrs['data-id']
      if (mid) api.readMessage(mid).catch(function () {})
      const pid = attrs['data-pid'] || ''
      if (pid) {
        uni.setStorageSync('rocgit_current_project_id', Number(pid))
        location.href = 'project.html'
      }
    }
  })
  function filter(cat){
    document.querySelectorAll('.msg-tab').forEach(function(b){
      b.className='msg-tab shrink-0 px-3.5 py-1.5 rounded-full text-[12px] font-medium '+(b.dataset.cat===cat?'bg-blue-600 text-white':'bg-slate-100 text-slate-600');
    });
    document.querySelectorAll('.notice').forEach(function(n){
      n.classList.toggle('hidden',cat!=='all'&&n.dataset.cat!==cat);
    });
  }

  controller.setHandlers({
n6: function(event) { markRead() },
n8: function(event) { filter('all') },
n9: function(event) { filter('review') },
n10: function(event) { filter('deadline') },
n11: function(event) { filter('version') },
n12: function(event) { filter('meeting') }
  })
  return controller
}
