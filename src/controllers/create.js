import data from '../data/create.json'
import { createPageController } from '../utils/page-controller.js'

// Original prototype actions operate on reactive Vue state through adapters.
// No browser DOM, inline JavaScript or eval is required in the mini program.
export default function () {
  const controller = createPageController(data)
  const { document, location, navigator, setTimeout } = controller.adapters
  
  var mc=3,names=['sky','rose','emerald'],labels=['陈','刘','张'];
  function addMember(){
    mc++;
    var n=labels[(mc-1)%3],c=names[(mc-1)%3];
    document.getElementById('members').insertAdjacentHTML('beforeend','<div class="flex items-center gap-1.5 px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl"><span class="w-5 h-5 rounded-full bg-'+c+'-400 text-'+c+'-900 font-bold flex items-center justify-center text-[10px]">'+n+'</span><span class="text-[12px] text-slate-700">成员'+mc+'</span></div>');
    showToast('已添加');
  }
  var sc=3;
  function addStage(){
    sc++;
    document.getElementById('stages').insertAdjacentHTML('beforeend','<div class="flex items-center gap-2 bg-white border border-slate-200 rounded-xl px-3 py-2.5"><span class="text-[12px] font-medium text-slate-700 w-6">0'+sc+'</span><input class="flex-1 text-[13px] text-slate-900 outline-none bg-transparent" placeholder="阶段名称"><input type="date" class="text-[11px] text-slate-400 bg-transparent outline-none"></div>');
    showToast('已添加阶段');
  }
  var tc=2;
  function addTask(){
    tc++;
    var n=labels[tc%3],c=names[tc%3];
    document.getElementById('tasks').insertAdjacentHTML('beforeend','<div class="flex items-center gap-2 bg-white border border-slate-200 rounded-xl p-3"><span class="w-6 h-6 rounded-full bg-'+c+'-400 text-'+c+'-900 font-bold flex items-center justify-center text-[10px]">'+n+'</span><input class="flex-1 text-[13px] text-slate-900 outline-none bg-transparent" placeholder="任务内容"><input type="date" class="text-[11px] text-slate-400 bg-transparent outline-none"></div>');
  }
  function createProject(){showToast('项目已创建');setTimeout(function(){location.href='project.html'},800)}
  function showToast(msg){var t=document.getElementById('toast');t.textContent=msg;t.classList.remove('hidden');setTimeout(function(){t.classList.add('hidden')},1500)}

  controller.setHandlers({
n40: function(event) { addMember() },
n55: function(event) { addStage() },
n72: function(event) { addTask() },
n105: function(event) { createProject() }
  })
  return controller
}
