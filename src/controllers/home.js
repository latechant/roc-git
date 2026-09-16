import data from '../data/home.json'
import { createPageController } from '../utils/page-controller.js'

// Original prototype actions operate on reactive Vue state through adapters.
// No browser DOM, inline JavaScript or eval is required in the mini program.
export default function () {
  const controller = createPageController(data)
  const { document, location, navigator, setTimeout } = controller.adapters
  
  function showToast(msg) {
    var t = document.getElementById('toast');
    t.textContent = msg;
    t.classList.remove('hidden');
    setTimeout(function(){ t.classList.add('hidden'); }, 1500);
  }

function showFileSearch() {}

  controller.setHandlers({
n9: function(event) { showToast('分享') },
n23: function(event) { showFileSearch() },
n147: function(event) { event.preventDefault();showToast('已通过') },
n154: function(event) { event.preventDefault();showToast('已通过') }
  })
  return controller
}
