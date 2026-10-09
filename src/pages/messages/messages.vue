<template>
<view class="prototype-screen">
<view v-if="!ui.n0.removed" :class="classes(ui.n0.className)" :style="ui.n0.style" class="original-div">
<template v-if="ui.n0.textContent !== undefined"><text>{{ ui.n0.textContent }}</text></template><template v-else>
<view v-if="!ui.n1.removed" :class="classes(ui.n1.className)" :style="ui.n1.style" class="original-header">
<template v-if="ui.n1.textContent !== undefined"><text>{{ ui.n1.textContent }}</text></template><template v-else>
<view v-if="!ui.n2.removed" :class="classes(ui.n2.className)" :style="ui.n2.style" class="original-div">
<template v-if="ui.n2.textContent !== undefined"><text>{{ ui.n2.textContent }}</text></template><template v-else>
<view v-if="!ui.n3.removed" :class="classes(ui.n3.className)" :style="ui.n3.style" class="original-div">
<template v-if="ui.n3.textContent !== undefined"><text>{{ ui.n3.textContent }}</text></template><template v-else>
<view v-if="!ui.n4.removed" :class="classes(ui.n4.className)" :style="ui.n4.style" class="original-h1">
<template v-if="ui.n4.textContent !== undefined"><text>{{ ui.n4.textContent }}</text></template><template v-else>
<text>消息</text>
</template>
</view>
<view v-if="!ui.n5.removed" :class="classes(ui.n5.className)" :style="ui.n5.style" class="original-p">
<template v-if="ui.n5.textContent !== undefined"><text>{{ ui.n5.textContent }}</text></template><template v-else>
<text></text>
</template>
</view>
</template>
</view>
<button v-if="!ui.n6.removed" :class="classes(ui.n6.className)" :style="ui.n6.style" class="original-button" @tap.stop="tap('n6', $event)">
<template v-if="ui.n6.textContent !== undefined"><text>{{ ui.n6.textContent }}</text></template><template v-else>
<text>全部已读</text>
</template>
</button>
</template>
</view>
<scroll-view v-if="!ui.n7.removed" :class="classes(ui.n7.className)" :style="ui.n7.style" class="original-horizontal-scroll" scroll-x :show-scrollbar="false">
<view class="horizontal-content" style="gap:8px"><template v-if="ui.n7.textContent !== undefined"><text>{{ ui.n7.textContent }}</text></template><template v-else>
<button v-if="!ui.n8.removed" :class="classes(ui.n8.className)" :style="ui.n8.style" class="original-button" @tap.stop="tap('n8', $event)">
<template v-if="ui.n8.textContent !== undefined"><text>{{ ui.n8.textContent }}</text></template><template v-else>
<text>全部</text>
</template>
</button>
<button v-if="!ui.n9.removed" :class="classes(ui.n9.className)" :style="ui.n9.style" class="original-button" @tap.stop="tap('n9', $event)">
<template v-if="ui.n9.textContent !== undefined"><text>{{ ui.n9.textContent }}</text></template><template v-else>
<text>待审核</text>
</template>
</button>
<button v-if="!ui.n10.removed" :class="classes(ui.n10.className)" :style="ui.n10.style" class="original-button" @tap.stop="tap('n10', $event)">
<template v-if="ui.n10.textContent !== undefined"><text>{{ ui.n10.textContent }}</text></template><template v-else>
<text>截止</text>
</template>
</button>
<button v-if="!ui.n11.removed" :class="classes(ui.n11.className)" :style="ui.n11.style" class="original-button" @tap.stop="tap('n11', $event)">
<template v-if="ui.n11.textContent !== undefined"><text>{{ ui.n11.textContent }}</text></template><template v-else>
<text>成果</text>
</template>
</button>
<button v-if="!ui.n12.removed" :class="classes(ui.n12.className)" :style="ui.n12.style" class="original-button" @tap.stop="tap('n12', $event)">
<template v-if="ui.n12.textContent !== undefined"><text>{{ ui.n12.textContent }}</text></template><template v-else>
<text>研讨</text>
</template>
</button>
</template></view>
</scroll-view>
</template>
</view>
<scroll-view v-if="!ui.n13.removed" :class="classes(ui.n13.className)" :style="ui.n13.style" class="original-scroll" scroll-y :show-scrollbar="false">
<template v-if="ui.n13.textContent !== undefined"><text>{{ ui.n13.textContent }}</text></template><template v-else>
<view v-if="!ui.n14.removed" :class="classes(ui.n14.className)" :style="ui.n14.style" class="original-div">
<template v-if="ui.n14.textContent !== undefined"><text>{{ ui.n14.textContent }}</text></template><template v-else>
<view v-if="!ui.n15.removed" :class="classes(ui.n15.className)" :style="ui.n15.style" class="original-div">
<template v-if="ui.n15.textContent !== undefined"><text>{{ ui.n15.textContent }}</text></template><template v-else>
<view v-if="!ui.n16.removed" :class="classes(ui.n16.className)" :style="ui.n16.style" class="original-p">
<template v-if="ui.n16.textContent !== undefined"><text>{{ ui.n16.textContent }}</text></template><template v-else>
<text></text>
</template>
</view>
<view v-if="!ui.n17.removed" :class="classes(ui.n17.className)" :style="ui.n17.style" class="original-p">
<template v-if="ui.n17.textContent !== undefined"><text>{{ ui.n17.textContent }}</text></template><template v-else>
<text></text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n77.removed" :class="classes(ui.n77.className)" :style="ui.n77.style" class="original-div">
<template v-if="ui.n77.textContent !== undefined"><text>{{ ui.n77.textContent }}</text></template><template v-else>

</template>
</view>
<NativeFragment v-for="childId in ui.n13.appended" :key="childId" :node-id="childId" />
</template>
</scroll-view>
<view v-if="!ui.n78.removed" :class="classes(ui.n78.className)" :style="ui.n78.style" class="original-nav">
<template v-if="ui.n78.textContent !== undefined"><text>{{ ui.n78.textContent }}</text></template><template v-else>
<view v-if="!ui.n79.removed" :class="classes(ui.n79.className)" :style="ui.n79.style" class="original-a" @tap.stop="tap('n79', $event)">
<template v-if="ui.n79.textContent !== undefined"><text>{{ ui.n79.textContent }}</text></template><template v-else>
<image v-if="!ui.n80.removed" :class="classes(ui.n80.className)" :style="ui.n80.style" class="original-icon" :src="iconSrc('n80')" style="width:22px;height:22px" mode="aspectFit" />
<span v-if="!ui.n81.removed" :class="classes(ui.n81.className)" :style="ui.n81.style" class="original-span">
<template v-if="ui.n81.textContent !== undefined"><text>{{ ui.n81.textContent }}</text></template><template v-else>
<text>首页</text>
</template>
</span>
</template>
</view>
<view v-if="!ui.n82.removed" :class="classes(ui.n82.className)" :style="ui.n82.style" class="original-a" @tap.stop="tap('n82', $event)">
<template v-if="ui.n82.textContent !== undefined"><text>{{ ui.n82.textContent }}</text></template><template v-else>
<image v-if="!ui.n83.removed" :class="classes(ui.n83.className)" :style="ui.n83.style" class="original-icon" :src="iconSrc('n83')" style="width:22px;height:22px" mode="aspectFit" />
<span v-if="!ui.n84.removed" :class="classes(ui.n84.className)" :style="ui.n84.style" class="original-span">
<template v-if="ui.n84.textContent !== undefined"><text>{{ ui.n84.textContent }}</text></template><template v-else>
<text>消息</text>
</template>
</span>
</template>
</view>
<view v-if="!ui.n85.removed" :class="classes(ui.n85.className)" :style="ui.n85.style" class="original-a" @tap.stop="tap('n85', $event)">
<template v-if="ui.n85.textContent !== undefined"><text>{{ ui.n85.textContent }}</text></template><template v-else>
<image v-if="!ui.n86.removed" :class="classes(ui.n86.className)" :style="ui.n86.style" class="original-icon" :src="iconSrc('n86')" style="width:22px;height:22px" mode="aspectFit" />
<view v-if="!ui.n87.removed" :class="classes(ui.n87.className)" :style="ui.n87.style" class="original-span">
<template v-if="ui.n87.textContent !== undefined"><text>{{ ui.n87.textContent }}</text></template><template v-else>
<text>我的</text>
</template>
</view>
</template>
</view>
</template>
</view>
</template>
</view>
</view>
</template>

<script setup>
import { provide } from 'vue'
import { onUnload } from '@dcloudio/uni-app'
import NativeFragment from '../../components/NativeFragment.vue'
// #ifdef H5
import NativeDate from '../../components/NativeDate.vue'
// #endif
import createController from '../../controllers/messages.js'
const controller = createController()
const { ui, classes, tap, iconSrc } = controller
provide('pageController', controller)
onUnload(controller.dispose)
</script>
