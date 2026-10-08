import { reactive } from 'vue'
import data from '../data/home.json'
import { createPageController } from '../utils/page-controller.js'
import { loadHome } from '../utils/bind-api.js'
import api from '../utils/api.js'

// Original prototype actions operate on reactive Vue state through adapters.
// No browser DOM, inline JavaScript or eval is required in the mini program.
export default function () {
  const controller = reactive(createPageController(data))
  const { document, location, navigator, setTimeout } = controller.adapters

  // 接入真实接口：后端数据覆盖原型文本
  loadHome(controller)
  
  function showToast(msg) {
    var t = document.getElementById('toast');
    t.textContent = msg;
    t.classList.remove('hidden');
    setTimeout(function(){ t.classList.add('hidden'); }, 1500);
  }

  // 打开首页"当前项目"卡片指向的项目：先写 storage 再跳（project.js 读取 storage 优先）
  function openProject(i) {
    var p = controller.rocProjects && controller.rocProjects[i]
    if (!p) { showToast('暂无项目'); return }
    uni.setStorageSync('rocgit_current_project_id', p.projectId)
    location.href = 'project.html'
  }

  controller.setHandlers({
n7: async function (event) {
  event.preventDefault()
  const groups = await api.myGroups().catch(() => null)
  if (groups && groups.length) {
    location.href = 'create.html'
  } else {
    showToast('请先加入/创建小组，再新建项目')
    controller.showGroupPopup = true
  }
},
n33: function (event) { event.preventDefault(); openProject(0) },
n73: function (event) { event.preventDefault(); openProject(1) },
n9: function(event) { showToast('分享') },
n147: function(event) { event.preventDefault();showToast('已通过') },
n154: function(event) { event.preventDefault();showToast('已通过') }
  })

  // 弹窗内创建/加入成功后：刷新小组列表，用户可再点"+"直接进创建页
  controller.onJoined = async function () {
    const groups = await api.myGroups().catch(() => null)
    controller.rocGroups = Array.isArray(groups) ? groups : []
  }
  return controller
}
