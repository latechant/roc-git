import data from '../data/profile.json'
import { reactive } from 'vue'
import { createPageController } from '../utils/page-controller.js'
import { loadProfile } from '../utils/bind-api.js'

// Original prototype actions operate on reactive Vue state through adapters.
// No browser DOM, inline JavaScript or eval is required in the mini program.
export default function () {
  const controller = reactive(createPageController(data))
  const { document, location, navigator, setTimeout } = controller.adapters

  // 弹窗状态：进行中项目列表弹窗（复用 ProjectListPopup）
  controller.showProjectList = false
  controller.rocProjects = []

  // 接入真实接口：后端个人资料/统计覆盖原型数据
  loadProfile(controller)
  
  function showToast(msg){var t=document.getElementById('toast');t.textContent=msg;t.classList.remove('hidden');setTimeout(function(){t.classList.add('hidden')},1500)}
  function toggleSwitch(btn){
    btn.classList.toggle('bg-blue-600');btn.classList.toggle('bg-slate-300');
    var dot=btn.querySelector('span');dot.classList.toggle('right-0.5');dot.classList.toggle('left-0.5');
  }
  function showArchive(){document.getElementById('archiveModal').classList.remove('hidden')}
  function hideArchive(){document.getElementById('archiveModal').classList.add('hidden')}
  function doArchive(){hideArchive();showToast('已归档 2 个项目，文件已打包')}

  controller.setHandlers({
n14: function(event) { showToast('编辑资料') },
n43: function(event) { event.preventDefault(); controller.showProjectList = true },
n46: function(event) { showToast('已加载') },
n51: function(event) { showArchive() },
n58: function(event) { showToast('存储正常') },
n66: function(event) { location.href='teacher.html' },
n73: function(event) { location.href='teacher.html' },
n87: function(event) { toggleSwitch(this) },
n93: function(event) { toggleSwitch(this) },
n96: function(event) { showToast('已退出') },
n108: function(event) { hideArchive() },
n109: function(event) { event.stopPropagation() },
n112: function(event) { hideArchive() },
n161: function(event) { doArchive() }
  })

  // 弹窗选中项目：关弹窗 → 写 storage → 跳对应项目详情（与 home.js 同款）
  controller.onSelectProject = function (p) {
    controller.showProjectList = false
    if (!p) return
    uni.setStorageSync('rocgit_current_project_id', p.projectId)
    location.href = 'project.html'
  }
  return controller
}
