import data from '../data/home.json'
import { createPageController } from '../utils/page-controller.js'
import { loadHome } from '../utils/bind-api.js'
import api from '../utils/api.js'

// Original prototype actions operate on reactive Vue state through adapters.
// No browser DOM, inline JavaScript or eval is required in the mini program.
export default function () {
  const controller = createPageController(data)
  const { document, location, navigator, setTimeout } = controller.adapters

  // 接入真实接口：后端数据覆盖原型文本
  loadHome(controller)
  
  function showToast(msg) {
    var t = document.getElementById('toast');
    t.textContent = msg;
    t.classList.remove('hidden');
    setTimeout(function(){ t.classList.add('hidden'); }, 1500);
  }

  controller.setHandlers({
n7: async function (event) {
  event.preventDefault()
  const groups = await api.myGroups().catch(() => null)
  if (groups && groups.length) {
    location.href = 'create.html'
  } else {
    showToast('请先加入/创建小组，再新建项目')
  }
},
n9: function(event) { showToast('分享') },
n147: function(event) { event.preventDefault();showToast('已通过') },
n154: function(event) { event.preventDefault();showToast('已通过') }
  })
  return controller
}
