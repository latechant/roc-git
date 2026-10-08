<template>
<view class="prototype-screen">
<view v-if="!ui.n0.removed" :class="classes(ui.n0.className)" :style="ui.n0.style" class="original-div">
<template v-if="ui.n0.textContent !== undefined"><text>{{ ui.n0.textContent }}</text></template><template v-else>
<view v-if="!ui.n1.removed" :class="classes(ui.n1.className)" :style="ui.n1.style" class="original-header">
<template v-if="ui.n1.textContent !== undefined"><text>{{ ui.n1.textContent }}</text></template><template v-else>
<view v-if="!ui.n2.removed" :class="classes(ui.n2.className)" :style="ui.n2.style" class="original-div">
<template v-if="ui.n2.textContent !== undefined"><text>{{ ui.n2.textContent }}</text></template><template v-else>
<view v-if="!ui.n3.removed" :class="classes(ui.n3.className)" :style="ui.n3.style" class="original-a" @tap.stop="tap('n3', $event)">
<template v-if="ui.n3.textContent !== undefined"><text>{{ ui.n3.textContent }}</text></template><template v-else>
<image v-if="!ui.n4.removed" :class="classes(ui.n4.className)" :style="ui.n4.style" class="original-icon" :src="iconSrc('n4')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n5.removed" :class="classes(ui.n5.className)" :style="ui.n5.style" class="original-div">
<template v-if="ui.n5.textContent !== undefined"><text>{{ ui.n5.textContent }}</text></template><template v-else>
<view v-if="!ui.n6.removed" :class="classes(ui.n6.className)" :style="ui.n6.style" class="original-p">
<template v-if="ui.n6.textContent !== undefined"><text>{{ ui.n6.textContent }}</text></template><template v-else>
<text>新建协作空间</text>
</template>
</view>
<view v-if="!ui.n7.removed" :class="classes(ui.n7.className)" :style="ui.n7.style" class="original-h1">
<template v-if="ui.n7.textContent !== undefined"><text>{{ ui.n7.textContent }}</text></template><template v-else>
<text>创建项目</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n8.removed" :class="classes(ui.n8.className)" :style="ui.n8.style" class="original-div">
<template v-if="ui.n8.textContent !== undefined"><text>{{ ui.n8.textContent }}</text></template><template v-else>
<span v-if="!ui.n9.removed" :class="classes(ui.n9.className)" :style="ui.n9.style" class="original-span">
<template v-if="ui.n9.textContent !== undefined"><text>{{ ui.n9.textContent }}</text></template><template v-else>
<view v-if="!ui.n10.removed" :class="classes(ui.n10.className)" :style="ui.n10.style" class="original-span" @tap.stop="goStep(1)">
<template v-if="ui.n10.textContent !== undefined"><text>{{ ui.n10.textContent }}</text></template><template v-else>
<text>1</text>
</template>
</view>
<text>基础</text>
</template>
</span>
<view v-if="!ui.n11.removed" :class="classes(ui.n11.className)" :style="ui.n11.style" class="original-div">
<template v-if="ui.n11.textContent !== undefined"><text>{{ ui.n11.textContent }}</text></template><template v-else>

</template>
</view>
<span v-if="!ui.n12.removed" :class="classes(ui.n12.className)" :style="ui.n12.style" class="original-span">
<template v-if="ui.n12.textContent !== undefined"><text>{{ ui.n12.textContent }}</text></template><template v-else>
<span v-if="!ui.n13.removed" :class="classes(ui.n13.className)" :style="ui.n13.style" class="original-span" @tap.stop="goStep(2)">
<template v-if="ui.n13.textContent !== undefined"><text>{{ ui.n13.textContent }}</text></template><template v-else>
<text>2</text>
</template>
</span>
<text>阶段</text>
</template>
</span>
<view v-if="!ui.n14.removed" :class="classes(ui.n14.className)" :style="ui.n14.style" class="original-div">
<template v-if="ui.n14.textContent !== undefined"><text>{{ ui.n14.textContent }}</text></template><template v-else>

</template>
</view>
<view v-if="!ui.n15.removed" :class="classes(ui.n15.className)" :style="ui.n15.style" class="original-span">
<template v-if="ui.n15.textContent !== undefined"><text>{{ ui.n15.textContent }}</text></template><template v-else>
<view v-if="!ui.n16.removed" :class="classes(ui.n16.className)" :style="ui.n16.style" class="original-span" @tap.stop="goStep(3)">
<template v-if="ui.n16.textContent !== undefined"><text>{{ ui.n16.textContent }}</text></template><template v-else>
<text>3</text>
</template>
</view>
<text>分工</text>
</template>
</view>
<view v-if="!ui.n17.removed" :class="classes(ui.n17.className)" :style="ui.n17.style" class="original-div">
<template v-if="ui.n17.textContent !== undefined"><text>{{ ui.n17.textContent }}</text></template><template v-else>

</template>
</view>
<view v-if="!ui.n18.removed" :class="classes(ui.n18.className)" :style="ui.n18.style" class="original-span">
<template v-if="ui.n18.textContent !== undefined"><text>{{ ui.n18.textContent }}</text></template><template v-else>
<view v-if="!ui.n19.removed" :class="classes(ui.n19.className)" :style="ui.n19.style" class="original-span" @tap.stop="goStep(4)">
<template v-if="ui.n19.textContent !== undefined"><text>{{ ui.n19.textContent }}</text></template><template v-else>
<text>4</text>
</template>
</view>
<text>规则</text>
</template>
</view>
</template>
</view>
</template>
</view>
<scroll-view v-if="!ui.n20.removed" :class="classes(ui.n20.className)" :style="ui.n20.style" class="original-scroll" scroll-y :show-scrollbar="false">
<template v-if="ui.n20.textContent !== undefined"><text>{{ ui.n20.textContent }}</text></template><template v-else>
<view v-if="!ui.n21.removed" :class="classes(ui.n21.className)" :style="ui.n21.style" class="original-p">
<template v-if="ui.n21.textContent !== undefined"><text>{{ ui.n21.textContent }}</text></template><template v-else>
<text>项目建立后可邀请成员、设置阶段、分配任务，全流程自动留痕。</text>
</template>
</view>
<template v-if="controller.step === 1">
<view v-if="!ui.n22.removed" :class="classes(ui.n22.className)" :style="ui.n22.style" class="original-div">
<template v-if="ui.n22.textContent !== undefined"><text>{{ ui.n22.textContent }}</text></template><template v-else>
<view v-if="!ui.n23.removed" :class="classes(ui.n23.className)" :style="ui.n23.style" class="original-h2">
<template v-if="ui.n23.textContent !== undefined"><text>{{ ui.n23.textContent }}</text></template><template v-else>
<text>基础信息</text>
</template>
</view>
<view v-if="!ui.n24.removed" :class="classes(ui.n24.className)" :style="ui.n24.style" class="original-div">
<template v-if="ui.n24.textContent !== undefined"><text>{{ ui.n24.textContent }}</text></template><template v-else>
<view v-if="!ui.n25.removed" :class="classes(ui.n25.className)" :style="ui.n25.style" class="original-div">
<template v-if="ui.n25.textContent !== undefined"><text>{{ ui.n25.textContent }}</text></template><template v-else>
<view v-if="!ui.n26.removed" :class="classes(ui.n26.className)" :style="ui.n26.style" class="original-label">
<template v-if="ui.n26.textContent !== undefined"><text>{{ ui.n26.textContent }}</text></template><template v-else>
<text>项目名称</text>
</template>
</view>
<input v-if="!ui.n27.removed" :class="classes(ui.n27.className)" :style="ui.n27.style" class="original-input" :value="ui.n27.value" @input="ui.n27.value = $event.detail.value" placeholder="" :disabled="false" :maxlength="-1" />
</template>
</view>
<view v-if="!ui.n28.removed" :class="classes(ui.n28.className)" :style="ui.n28.style" class="original-div">
<template v-if="ui.n28.textContent !== undefined"><text>{{ ui.n28.textContent }}</text></template><template v-else>
<view v-if="!ui.n29.removed" :class="classes(ui.n29.className)" :style="ui.n29.style" class="original-label">
<template v-if="ui.n29.textContent !== undefined"><text>{{ ui.n29.textContent }}</text></template><template v-else>
</template>
</view>
</template>
</view>
<view v-if="!ui.n34.removed" :class="classes(ui.n34.className)" :style="ui.n34.style" class="original-div">
<template v-if="ui.n34.textContent !== undefined"><text>{{ ui.n34.textContent }}</text></template><template v-else>
<view v-if="!ui.n35.removed" :class="classes(ui.n35.className)" :style="ui.n35.style" class="original-label">
<template v-if="ui.n35.textContent !== undefined"><text>{{ ui.n35.textContent }}</text></template><template v-else>
<text>项目说明</text>
</template>
</view>
<textarea v-if="!ui.n36.removed" :class="classes(ui.n36.className)" :style="ui.n36.style" class="original-textarea" :value="ui.n36.value" @input="ui.n36.value = $event.detail.value" placeholder="" :disabled="false" :maxlength="-1" :auto-height="false" :fixed="true" :show-confirm-bar="false"></textarea>
</template>
</view>
</template>
</view>
</template>
</view>
</template>
<template v-if="controller.step === 2">
<view v-if="!ui.n52.removed" :class="classes(ui.n52.className)" :style="ui.n52.style" class="original-div">
<template v-if="ui.n52.textContent !== undefined"><text>{{ ui.n52.textContent }}</text></template><template v-else>
<view v-if="!ui.n53.removed" :class="classes(ui.n53.className)" :style="ui.n53.style" class="original-div">
<template v-if="ui.n53.textContent !== undefined"><text>{{ ui.n53.textContent }}</text></template><template v-else>
<view v-if="!ui.n54.removed" :class="classes(ui.n54.className)" :style="ui.n54.style" class="original-h2">
<template v-if="ui.n54.textContent !== undefined"><text>{{ ui.n54.textContent }}</text></template><template v-else>
<text>作业阶段</text>
</template>
</view>
<button v-if="!ui.n55.removed" :class="classes(ui.n55.className)" :style="ui.n55.style" class="original-button tap-target-lg" @tap.stop="tap('n55', $event)">
<template v-if="ui.n55.textContent !== undefined"><text>{{ ui.n55.textContent }}</text></template><template v-else>
<text>+ 添加</text>
</template>
</button>
<text class="stage-tpl-btn" @tap="controller.applyStageTemplate('course')">课程项目</text>
<text class="stage-tpl-btn" @tap="controller.applyStageTemplate('research')">调研报告</text>
<text class="stage-tpl-btn" @tap="controller.applyStageTemplate('agile')">敏捷迭代</text>
</template>
</view>
<view v-if="!ui.n56.removed" :class="classes(ui.n56.className)" :style="ui.n56.style" class="original-div">
<template v-if="ui.n56.textContent !== undefined"><text>{{ ui.n56.textContent }}</text></template><template v-else>
<view v-if="!ui.n57.removed" :class="classes(ui.n57.className)" :style="ui.n57.style" class="original-div">
<template v-if="ui.n57.textContent !== undefined"><text>{{ ui.n57.textContent }}</text></template><template v-else>
<view v-if="!ui.n58.removed" :class="classes(ui.n58.className)" :style="ui.n58.style" class="original-span">
<template v-if="ui.n58.textContent !== undefined"><text>{{ ui.n58.textContent }}</text></template><template v-else>
<text>01</text>
</template>
</view>
<input v-if="!ui.n59.removed" :class="classes(ui.n59.className)" :style="ui.n59.style" class="original-input" :value="ui.n59.value" @input="ui.n59.value = $event.detail.value" placeholder="" :disabled="false" :maxlength="-1" />
<!-- #ifdef H5 -->
<NativeDate v-if="!ui.n60.removed" :class="classes(ui.n60.className)" :style="ui.n60.style" v-model:value="ui.n60.value" />
<!-- #endif -->
<!-- #ifndef H5 -->
<picker v-if="!ui.n60.removed" :class="classes(ui.n60.className)" :style="ui.n60.style" class="original-date" mode="date" :value="ui.n60.value" @change="ui.n60.value = $event.detail.value"><view>{{ ui.n60.value ? ui.n60.value.replaceAll('-', '/') : '年/月/日' }}</view></picker>
<!-- #endif -->
<text class="stage-del" @tap="controller.removeRow('n56', 'n57')">×</text>
</template>
</view>
<view v-if="!ui.n61.removed" :class="classes(ui.n61.className)" :style="ui.n61.style" class="original-div">
<template v-if="ui.n61.textContent !== undefined"><text>{{ ui.n61.textContent }}</text></template><template v-else>
<view v-if="!ui.n62.removed" :class="classes(ui.n62.className)" :style="ui.n62.style" class="original-span">
<template v-if="ui.n62.textContent !== undefined"><text>{{ ui.n62.textContent }}</text></template><template v-else>
<text>02</text>
</template>
</view>
<input v-if="!ui.n63.removed" :class="classes(ui.n63.className)" :style="ui.n63.style" class="original-input" :value="ui.n63.value" @input="ui.n63.value = $event.detail.value" placeholder="" :disabled="false" :maxlength="-1" />
<!-- #ifdef H5 -->
<NativeDate v-if="!ui.n64.removed" :class="classes(ui.n64.className)" :style="ui.n64.style" v-model:value="ui.n64.value" />
<!-- #endif -->
<!-- #ifndef H5 -->
<picker v-if="!ui.n64.removed" :class="classes(ui.n64.className)" :style="ui.n64.style" class="original-date" mode="date" :value="ui.n64.value" @change="ui.n64.value = $event.detail.value"><view>{{ ui.n64.value ? ui.n64.value.replaceAll('-', '/') : '年/月/日' }}</view></picker>
<!-- #endif -->
<text class="stage-del" @tap="controller.removeRow('n56', 'n61')">×</text>
</template>
</view>
<view v-if="!ui.n65.removed" :class="classes(ui.n65.className)" :style="ui.n65.style" class="original-div">
<template v-if="ui.n65.textContent !== undefined"><text>{{ ui.n65.textContent }}</text></template><template v-else>
<view v-if="!ui.n66.removed" :class="classes(ui.n66.className)" :style="ui.n66.style" class="original-span">
<template v-if="ui.n66.textContent !== undefined"><text>{{ ui.n66.textContent }}</text></template><template v-else>
<text>03</text>
</template>
</view>
<input v-if="!ui.n67.removed" :class="classes(ui.n67.className)" :style="ui.n67.style" class="original-input" :value="ui.n67.value" @input="ui.n67.value = $event.detail.value" placeholder="" :disabled="false" :maxlength="-1" />
<!-- #ifdef H5 -->
<NativeDate v-if="!ui.n68.removed" :class="classes(ui.n68.className)" :style="ui.n68.style" v-model:value="ui.n68.value" />
<!-- #endif -->
<!-- #ifndef H5 -->
<picker v-if="!ui.n68.removed" :class="classes(ui.n68.className)" :style="ui.n68.style" class="original-date" mode="date" :value="ui.n68.value" @change="ui.n68.value = $event.detail.value"><view>{{ ui.n68.value ? ui.n68.value.replaceAll('-', '/') : '年/月/日' }}</view></picker>
<!-- #endif -->
<text class="stage-del" @tap="controller.removeRow('n56', 'n65')">×</text>
</template>
</view>
</template>
<NativeFragment v-for="childId in ui.n56.appended" :key="childId" :node-id="childId" removable />
</view>
</template>
</view>
</template>
<template v-if="controller.step === 3">
<view v-if="!ui.n37.removed" :class="classes(ui.n37.className)" :style="ui.n37.style" class="original-div">
<template v-if="ui.n37.textContent !== undefined"><text>{{ ui.n37.textContent }}</text></template><template v-else>
<view v-if="!ui.n38.removed" :class="classes(ui.n38.className)" :style="ui.n38.style" class="original-div">
<template v-if="ui.n38.textContent !== undefined"><text>{{ ui.n38.textContent }}</text></template><template v-else>
<view v-if="!ui.n39.removed" :class="classes(ui.n39.className)" :style="ui.n39.style" class="original-h2">
<template v-if="ui.n39.textContent !== undefined"><text>{{ ui.n39.textContent }}</text></template><template v-else>
<text>小组成员</text>
</template>
</view>
<text class="stage-tpl-btn" @tap="showGroupPopup = true">邀请成员</text>
</template>
</view>
<view v-if="!ui.n41.removed" :class="classes(ui.n41.className)" :style="ui.n41.style" class="original-div">
<template v-if="ui.n41.textContent !== undefined"><text>{{ ui.n41.textContent }}</text></template><template v-else>
<view v-if="!ui.n42.removed" :class="classes(ui.n42.className)" :style="ui.n42.style" class="original-div">
<template v-if="ui.n42.textContent !== undefined"><text>{{ ui.n42.textContent }}</text></template><template v-else>
<view v-if="!ui.n43.removed" :class="classes(ui.n43.className)" :style="ui.n43.style" class="original-span">
<template v-if="ui.n43.textContent !== undefined"><text>{{ ui.n43.textContent }}</text></template><template v-else>
<text>林</text>
</template>
</view>
<span v-if="!ui.n44.removed" :class="classes(ui.n44.className)" :style="ui.n44.style" class="original-span">
<template v-if="ui.n44.textContent !== undefined"><text>{{ ui.n44.textContent }}</text></template><template v-else>
<text>林知行</text>
</template>
</span>
<span v-if="!ui.n45.removed" :class="classes(ui.n45.className)" :style="ui.n45.style" class="original-span">
<template v-if="ui.n45.textContent !== undefined"><text>{{ ui.n45.textContent }}</text></template><template v-else>
<text>组长</text>
</template>
</span>
</template>
</view>
<view v-if="!ui.n46.removed" :class="classes(ui.n46.className)" :style="ui.n46.style" class="original-div">
<template v-if="ui.n46.textContent !== undefined"><text>{{ ui.n46.textContent }}</text></template><template v-else>
<span v-if="!ui.n47.removed" :class="classes(ui.n47.className)" :style="ui.n47.style" class="original-span">
<template v-if="ui.n47.textContent !== undefined"><text>{{ ui.n47.textContent }}</text></template><template v-else>
<text>许</text>
</template>
</span>
<view v-if="!ui.n48.removed" :class="classes(ui.n48.className)" :style="ui.n48.style" class="original-span">
<template v-if="ui.n48.textContent !== undefined"><text>{{ ui.n48.textContent }}</text></template><template v-else>
<text>许萌</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n49.removed" :class="classes(ui.n49.className)" :style="ui.n49.style" class="original-div">
<template v-if="ui.n49.textContent !== undefined"><text>{{ ui.n49.textContent }}</text></template><template v-else>
<span v-if="!ui.n50.removed" :class="classes(ui.n50.className)" :style="ui.n50.style" class="original-span">
<template v-if="ui.n50.textContent !== undefined"><text>{{ ui.n50.textContent }}</text></template><template v-else>
<text>王</text>
</template>
</span>
<span v-if="!ui.n51.removed" :class="classes(ui.n51.className)" :style="ui.n51.style" class="original-span">
<template v-if="ui.n51.textContent !== undefined"><text>{{ ui.n51.textContent }}</text></template><template v-else>
<text>王宁</text>
</template>
</span>
</template>
</view>
</template>
<view v-for="m in controller.rocMemberPool" :key="m.userId" class="member-chip" :class="m.selected ? 'member-chip-on' : ''" @tap="controller.toggleMember(m)">
<text class="member-chip-avatar">{{ m.nickname ? m.nickname.slice(0, 1) : '?' }}</text>
<text class="member-chip-name">{{ m.nickname }}</text>
<text class="member-chip-role">{{ m.role === 'leader' ? '组长' : (m.role === 'teacher' ? '教师' : '组员') }}</text>
</view>
<view v-if="(controller.rocMemberPool || []).length <= 1" class="member-tip">
<text class="member-tip-text">成员来自小组，邀请同学加入后自动显示在此</text>
</view>
</view>
</template>
</view>
<view v-if="!ui.n69.removed" :class="classes(ui.n69.className)" :style="ui.n69.style" class="original-div">
<template v-if="ui.n69.textContent !== undefined"><text>{{ ui.n69.textContent }}</text></template><template v-else>
<view v-if="!ui.n70.removed" :class="classes(ui.n70.className)" :style="ui.n70.style" class="original-div">
<template v-if="ui.n70.textContent !== undefined"><text>{{ ui.n70.textContent }}</text></template><template v-else>
<view v-if="!ui.n71.removed" :class="classes(ui.n71.className)" :style="ui.n71.style" class="original-h2">
<template v-if="ui.n71.textContent !== undefined"><text>{{ ui.n71.textContent }}</text></template><template v-else>
<text>任务分工</text>
</template>
</view>
<button v-if="!ui.n72.removed" :class="classes(ui.n72.className)" :style="ui.n72.style" class="original-button tap-target-lg" @tap.stop="tap('n72', $event)">
<template v-if="ui.n72.textContent !== undefined"><text>{{ ui.n72.textContent }}</text></template><template v-else>
<text>+ 添加</text>
</template>
</button>
<text class="stage-tpl-btn" @tap="controller.applyTaskTemplate('dev')">通用开发</text>
<text class="stage-tpl-btn" @tap="controller.applyTaskTemplate('report')">报告答辩</text>
</template>
</view>
<view v-if="!ui.n73.removed" :class="classes(ui.n73.className)" :style="ui.n73.style" class="original-div">
<template v-if="ui.n73.textContent !== undefined"><text>{{ ui.n73.textContent }}</text></template><template v-else>
<view v-if="!ui.n74.removed" :class="classes(ui.n74.className)" :style="ui.n74.style" class="original-div">
<template v-if="ui.n74.textContent !== undefined"><text>{{ ui.n74.textContent }}</text></template><template v-else>
<picker v-if="!ui.n75.removed" :class="classes(ui.n75.className)" :style="ui.n75.style" class="original-select" mode="selector" :range="memberNames" :value="ui.n75.value" @change="ui.n75.value = $event.detail.value"><view>{{ memberNames[ui.n75.value] || memberNames[0] }}</view></picker>
<input v-if="!ui.n76.removed" :class="classes(ui.n76.className)" :style="ui.n76.style" class="original-input" :value="ui.n76.value" @input="ui.n76.value = $event.detail.value" placeholder="" :disabled="false" :maxlength="-1" />
<!-- #ifdef H5 -->
<NativeDate v-if="!ui.n77.removed" :class="classes(ui.n77.className)" :style="ui.n77.style" v-model:value="ui.n77.value" />
<!-- #endif -->
<!-- #ifndef H5 -->
<picker v-if="!ui.n77.removed" :class="classes(ui.n77.className)" :style="ui.n77.style" class="original-date" mode="date" :value="ui.n77.value" @change="ui.n77.value = $event.detail.value"><view>{{ ui.n77.value ? ui.n77.value.replaceAll('-', '/') : '年/月/日' }}</view></picker>
<!-- #endif -->
<text class="stage-del tap-target-lg" @tap="controller.removeRow('n73', 'n74')">×</text>
</template>
</view>
<view v-if="!ui.n78.removed" :class="classes(ui.n78.className)" :style="ui.n78.style" class="original-div">
<template v-if="ui.n78.textContent !== undefined"><text>{{ ui.n78.textContent }}</text></template><template v-else>
<picker v-if="!ui.n79.removed" :class="classes(ui.n79.className)" :style="ui.n79.style" class="original-select" mode="selector" :range="memberNames" :value="ui.n79.value" @change="ui.n79.value = $event.detail.value"><view>{{ memberNames[ui.n79.value] || memberNames[0] }}</view></picker>
<input v-if="!ui.n80.removed" :class="classes(ui.n80.className)" :style="ui.n80.style" class="original-input" :value="ui.n80.value" @input="ui.n80.value = $event.detail.value" placeholder="" :disabled="false" :maxlength="-1" />
<!-- #ifdef H5 -->
<NativeDate v-if="!ui.n81.removed" :class="classes(ui.n81.className)" :style="ui.n81.style" v-model:value="ui.n81.value" />
<!-- #endif -->
<!-- #ifndef H5 -->
<picker v-if="!ui.n81.removed" :class="classes(ui.n81.className)" :style="ui.n81.style" class="original-date" mode="date" :value="ui.n81.value" @change="ui.n81.value = $event.detail.value"><view>{{ ui.n81.value ? ui.n81.value.replaceAll('-', '/') : '年/月/日' }}</view></picker>
<!-- #endif -->
<text class="stage-del tap-target-lg" @tap="controller.removeRow('n73', 'n78')">×</text>
</template>
</view>
</template>
<NativeFragment v-for="childId in ui.n73.appended" :key="childId" :node-id="childId" removable />
</view>
</template>
</view>
</template>
<template v-if="controller.step === 4">
<view v-if="!ui.n82.removed" :class="classes(ui.n82.className)" :style="ui.n82.style" class="original-div">
<template v-if="ui.n82.textContent !== undefined"><text>{{ ui.n82.textContent }}</text></template><template v-else>
<view v-if="!ui.n83.removed" :class="classes(ui.n83.className)" :style="ui.n83.style" class="original-h2">
<template v-if="ui.n83.textContent !== undefined"><text>{{ ui.n83.textContent }}</text></template><template v-else>
<text>项目规则</text>
</template>
</view>
<view v-if="!ui.n84.removed" :class="classes(ui.n84.className)" :style="ui.n84.style" class="original-div">
<template v-if="ui.n84.textContent !== undefined"><text>{{ ui.n84.textContent }}</text></template><template v-else>
<view v-if="!ui.n85.removed" :class="classes(ui.n85.className)" :style="ui.n85.style" class="original-label" @tap.stop="ui.n86.checked = !ui.n86.checked">
<template v-if="ui.n85.textContent !== undefined"><text>{{ ui.n85.textContent }}</text></template><template v-else>
<view v-if="!ui.n86.removed" :class="classes(ui.n86.className)" :style="ui.n86.style" class="original-checkbox" @tap.stop="ui.n86.checked = !ui.n86.checked"><image v-if="ui.n86.checked" src="/static/icons/check--white.png" style="width:100%;height:100%;background:#2563eb;border-radius:3px" /><view v-else class="checkbox-empty" /></view>
<view v-if="!ui.n87.removed" :class="classes(ui.n87.className)" :style="ui.n87.style" class="original-div">
<template v-if="ui.n87.textContent !== undefined"><text>{{ ui.n87.textContent }}</text></template><template v-else>
<view v-if="!ui.n88.removed" :class="classes(ui.n88.className)" :style="ui.n88.style" class="original-p">
<template v-if="ui.n88.textContent !== undefined"><text>{{ ui.n88.textContent }}</text></template><template v-else>
<text>挂单审核</text>
</template>
</view>
<view v-if="!ui.n89.removed" :class="classes(ui.n89.className)" :style="ui.n89.style" class="original-p">
<template v-if="ui.n89.textContent !== undefined"><text>{{ ui.n89.textContent }}</text></template><template v-else>
<text>所有上传需审核通过方入库</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n90.removed" :class="classes(ui.n90.className)" :style="ui.n90.style" class="original-label" @tap.stop="ui.n91.checked = !ui.n91.checked">
<template v-if="ui.n90.textContent !== undefined"><text>{{ ui.n90.textContent }}</text></template><template v-else>
<view v-if="!ui.n91.removed" :class="classes(ui.n91.className)" :style="ui.n91.style" class="original-checkbox" @tap.stop="ui.n91.checked = !ui.n91.checked"><image v-if="ui.n91.checked" src="/static/icons/check--white.png" style="width:100%;height:100%;background:#2563eb;border-radius:3px" /><view v-else class="checkbox-empty" /></view>
<view v-if="!ui.n92.removed" :class="classes(ui.n92.className)" :style="ui.n92.style" class="original-div">
<template v-if="ui.n92.textContent !== undefined"><text>{{ ui.n92.textContent }}</text></template><template v-else>
<view v-if="!ui.n93.removed" :class="classes(ui.n93.className)" :style="ui.n93.style" class="original-p">
<template v-if="ui.n93.textContent !== undefined"><text>{{ ui.n93.textContent }}</text></template><template v-else>
<text>截止提醒</text>
</template>
</view>
<view v-if="!ui.n94.removed" :class="classes(ui.n94.className)" :style="ui.n94.style" class="original-p">
<template v-if="ui.n94.textContent !== undefined"><text>{{ ui.n94.textContent }}</text></template><template v-else>
<text>截止前 3 天、1 天提醒全员</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n95.removed" :class="classes(ui.n95.className)" :style="ui.n95.style" class="original-label" @tap.stop="ui.n96.checked = !ui.n96.checked">
<template v-if="ui.n95.textContent !== undefined"><text>{{ ui.n95.textContent }}</text></template><template v-else>
<view v-if="!ui.n96.removed" :class="classes(ui.n96.className)" :style="ui.n96.style" class="original-checkbox" @tap.stop="ui.n96.checked = !ui.n96.checked"><image v-if="ui.n96.checked" src="/static/icons/check--white.png" style="width:100%;height:100%;background:#2563eb;border-radius:3px" /><view v-else class="checkbox-empty" /></view>
<view v-if="!ui.n97.removed" :class="classes(ui.n97.className)" :style="ui.n97.style" class="original-div">
<template v-if="ui.n97.textContent !== undefined"><text>{{ ui.n97.textContent }}</text></template><template v-else>
<view v-if="!ui.n98.removed" :class="classes(ui.n98.className)" :style="ui.n98.style" class="original-p">
<template v-if="ui.n98.textContent !== undefined"><text>{{ ui.n98.textContent }}</text></template><template v-else>
<text>自动存档</text>
</template>
</view>
<view v-if="!ui.n99.removed" :class="classes(ui.n99.className)" :style="ui.n99.style" class="original-p">
<template v-if="ui.n99.textContent !== undefined"><text>{{ ui.n99.textContent }}</text></template><template v-else>
<text>支持还原上次上传</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n100.removed" :class="classes(ui.n100.className)" :style="ui.n100.style" class="original-label" @tap.stop="ui.n101.checked = !ui.n101.checked">
<template v-if="ui.n100.textContent !== undefined"><text>{{ ui.n100.textContent }}</text></template><template v-else>
<view v-if="!ui.n101.removed" :class="classes(ui.n101.className)" :style="ui.n101.style" class="original-checkbox" @tap.stop="ui.n101.checked = !ui.n101.checked"><image v-if="ui.n101.checked" src="/static/icons/check--white.png" style="width:100%;height:100%;background:#2563eb;border-radius:3px" /><view v-else class="checkbox-empty" /></view>
<view v-if="!ui.n102.removed" :class="classes(ui.n102.className)" :style="ui.n102.style" class="original-div">
<template v-if="ui.n102.textContent !== undefined"><text>{{ ui.n102.textContent }}</text></template><template v-else>
<view v-if="!ui.n103.removed" :class="classes(ui.n103.className)" :style="ui.n103.style" class="original-p">
<template v-if="ui.n103.textContent !== undefined"><text>{{ ui.n103.textContent }}</text></template><template v-else>
<text>允许教师查阅</text>
</template>
</view>
<view v-if="!ui.n104.removed" :class="classes(ui.n104.className)" :style="ui.n104.style" class="original-p">
<template v-if="ui.n104.textContent !== undefined"><text>{{ ui.n104.textContent }}</text></template><template v-else>
<text>教师可查阅全部过程材料</text>
</template>
</view>
</template>
</view>
</template>
</view>
</template>
</view>
</template>
</view>
</template>
<view v-if="controller.step > 1" class="mt-4 flex items-center gap-3">
<button class="flex-1 bg-slate-100 text-slate-700 rounded-2xl py-3 text-[14px] font-medium" @tap="prevStep()"><text>上一步</text></button>
</view>
<view v-if="controller.step < 4" class="mt-3">
<button class="w-full bg-blue-600 text-white rounded-2xl py-3 text-[14px] font-semibold" @tap="nextStep()"><text>下一步</text></button>
</view>
<template v-if="controller.step === 4">
<button v-if="!ui.n105.removed" :class="classes(ui.n105.className)" :style="ui.n105.style" class="original-button" @tap.stop="tap('n105', $event)">
<template v-if="ui.n105.textContent !== undefined"><text>{{ ui.n105.textContent }}</text></template><template v-else>
<text>创建并进入项目</text>
</template>
</button>
</template>
</template>
</scroll-view>
<view v-if="!ui.n106.removed" :class="classes(ui.n106.className)" :style="ui.n106.style" class="original-div">
<template v-if="ui.n106.textContent !== undefined"><text>{{ ui.n106.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</view>
</view>
<GroupPopup :show="showGroupPopup" @close="showGroupPopup = false" @joined="onGroupPopupJoined" />
</template>

<script setup>
import { computed, provide, ref } from 'vue'
import { onUnload } from '@dcloudio/uni-app'
import NativeFragment from '../../components/NativeFragment.vue'
import GroupPopup from '../../components/GroupPopup.vue'
// #ifdef H5
import NativeDate from '../../components/NativeDate.vue'
// #endif
import createController from '../../controllers/create.js'
import { loadCreateMembers } from '../../utils/bind-api.js'
const controller = createController()
const { ui, classes, tap, iconSrc, onGroupChange, goStep, prevStep, nextStep } = controller
provide('pageController', controller)
// 邀请成员弹窗（小组邀请码管理）
const showGroupPopup = ref(false)
function onGroupPopupJoined() {
  // 新成员加入后刷新成员池：成员 chip 与任务负责人选择器同步更新
  const g = (controller.rocGroups || [])[0]
  if (g) loadCreateMembers(controller, g.groupId)
}
// 负责人选择器数据：从成员池映射昵称（与 NativeFragment 内一致），池空时兜底当前用户
const memberNames = computed(() => {
  const pool = controller.rocMemberPool || []
  const names = pool.map(m => m.nickname || '')
  if (!names.length && controller.currentUserNickname) names.push(controller.currentUserNickname)
  return names
})
onUnload(controller.dispose)
</script>
