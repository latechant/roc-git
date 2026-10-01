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
<text>5 条变更</text>
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
<text>本周 5 条项目变更</text>
</template>
</view>
<view v-if="!ui.n17.removed" :class="classes(ui.n17.className)" :style="ui.n17.style" class="original-p">
<template v-if="ui.n17.textContent !== undefined"><text>{{ ui.n17.textContent }}</text></template><template v-else>
<text>2 待审核 · 2 截止 · 1 成果更新</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n18.removed" :class="classes(ui.n18.className)" :style="ui.n18.style" class="original-div">
<template v-if="ui.n18.textContent !== undefined"><text>{{ ui.n18.textContent }}</text></template><template v-else>
<view v-if="!ui.n19.removed" :class="classes(ui.n19.className)" :style="ui.n19.style" class="original-p">
<template v-if="ui.n19.textContent !== undefined"><text>{{ ui.n19.textContent }}</text></template><template v-else>
<text>今天 · 9月10日</text>
</template>
</view>
<view v-if="!ui.n20.removed" :class="classes(ui.n20.className)" :style="ui.n20.style" class="original-div">
<template v-if="ui.n20.textContent !== undefined"><text>{{ ui.n20.textContent }}</text></template><template v-else>
<view v-if="!ui.n21.removed" :class="classes(ui.n21.className)" :style="ui.n21.style" class="original-a" @tap.stop="tap('n21', $event)">
<template v-if="ui.n21.textContent !== undefined"><text>{{ ui.n21.textContent }}</text></template><template v-else>
<view v-if="!ui.n22.removed" :class="classes(ui.n22.className)" :style="ui.n22.style" class="original-div">
<template v-if="ui.n22.textContent !== undefined"><text>{{ ui.n22.textContent }}</text></template><template v-else>
<image v-if="!ui.n23.removed" :class="classes(ui.n23.className)" :style="ui.n23.style" class="original-icon" :src="iconSrc('n23')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n24.removed" :class="classes(ui.n24.className)" :style="ui.n24.style" class="original-div">
<template v-if="ui.n24.textContent !== undefined"><text>{{ ui.n24.textContent }}</text></template><template v-else>
<view v-if="!ui.n25.removed" :class="classes(ui.n25.className)" :style="ui.n25.style" class="original-div">
<template v-if="ui.n25.textContent !== undefined"><text>{{ ui.n25.textContent }}</text></template><template v-else>
<view v-if="!ui.n26.removed" :class="classes(ui.n26.className)" :style="ui.n26.style" class="original-p">
<template v-if="ui.n26.textContent !== undefined"><text>{{ ui.n26.textContent }}</text></template><template v-else>
<text>许萌提交了访谈记录</text>
</template>
</view>
<view v-if="!ui.n27.removed" :class="classes(ui.n27.className)" :style="ui.n27.style" class="original-span">
<template v-if="ui.n27.textContent !== undefined"><text>{{ ui.n27.textContent }}</text></template><template v-else>
<text>待审核</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n28.removed" :class="classes(ui.n28.className)" :style="ui.n28.style" class="original-p">
<template v-if="ui.n28.textContent !== undefined"><text>{{ ui.n28.textContent }}</text></template><template v-else>
<text>访谈录音转写稿.docx · 请尽快审核入库</text>
</template>
</view>
<view v-if="!ui.n29.removed" :class="classes(ui.n29.className)" :style="ui.n29.style" class="original-p">
<template v-if="ui.n29.textContent !== undefined"><text>{{ ui.n29.textContent }}</text></template><template v-else>
<text>10:18 · 社区调研报告</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n30.removed" :class="classes(ui.n30.className)" :style="ui.n30.style" class="original-span">
<template v-if="ui.n30.textContent !== undefined"><text>{{ ui.n30.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</view>
<view v-if="!ui.n31.removed" :class="classes(ui.n31.className)" :style="ui.n31.style" class="original-a" @tap.stop="tap('n31', $event)">
<template v-if="ui.n31.textContent !== undefined"><text>{{ ui.n31.textContent }}</text></template><template v-else>
<view v-if="!ui.n32.removed" :class="classes(ui.n32.className)" :style="ui.n32.style" class="original-div">
<template v-if="ui.n32.textContent !== undefined"><text>{{ ui.n32.textContent }}</text></template><template v-else>
<image v-if="!ui.n33.removed" :class="classes(ui.n33.className)" :style="ui.n33.style" class="original-icon" :src="iconSrc('n33')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n34.removed" :class="classes(ui.n34.className)" :style="ui.n34.style" class="original-div">
<template v-if="ui.n34.textContent !== undefined"><text>{{ ui.n34.textContent }}</text></template><template v-else>
<view v-if="!ui.n35.removed" :class="classes(ui.n35.className)" :style="ui.n35.style" class="original-div">
<template v-if="ui.n35.textContent !== undefined"><text>{{ ui.n35.textContent }}</text></template><template v-else>
<view v-if="!ui.n36.removed" :class="classes(ui.n36.className)" :style="ui.n36.style" class="original-p">
<template v-if="ui.n36.textContent !== undefined"><text>{{ ui.n36.textContent }}</text></template><template v-else>
<text>资料归集还剩 4 天</text>
</template>
</view>
<view v-if="!ui.n37.removed" :class="classes(ui.n37.className)" :style="ui.n37.style" class="original-span">
<template v-if="ui.n37.textContent !== undefined"><text>{{ ui.n37.textContent }}</text></template><template v-else>
<text>临期</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n38.removed" :class="classes(ui.n38.className)" :style="ui.n38.style" class="original-p">
<template v-if="ui.n38.textContent !== undefined"><text>{{ ui.n38.textContent }}</text></template><template v-else>
<text>请确认资料完整性，按时推进</text>
</template>
</view>
<view v-if="!ui.n39.removed" :class="classes(ui.n39.className)" :style="ui.n39.style" class="original-p">
<template v-if="ui.n39.textContent !== undefined"><text>{{ ui.n39.textContent }}</text></template><template v-else>
<text>09:00 · 社区调研报告</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n40.removed" :class="classes(ui.n40.className)" :style="ui.n40.style" class="original-span">
<template v-if="ui.n40.textContent !== undefined"><text>{{ ui.n40.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</view>
<view v-if="!ui.n41.removed" :class="classes(ui.n41.className)" :style="ui.n41.style" class="original-a" @tap.stop="tap('n41', $event)">
<template v-if="ui.n41.textContent !== undefined"><text>{{ ui.n41.textContent }}</text></template><template v-else>
<view v-if="!ui.n42.removed" :class="classes(ui.n42.className)" :style="ui.n42.style" class="original-div">
<template v-if="ui.n42.textContent !== undefined"><text>{{ ui.n42.textContent }}</text></template><template v-else>
<image v-if="!ui.n43.removed" :class="classes(ui.n43.className)" :style="ui.n43.style" class="original-icon" :src="iconSrc('n43')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n44.removed" :class="classes(ui.n44.className)" :style="ui.n44.style" class="original-div">
<template v-if="ui.n44.textContent !== undefined"><text>{{ ui.n44.textContent }}</text></template><template v-else>
<view v-if="!ui.n45.removed" :class="classes(ui.n45.className)" :style="ui.n45.style" class="original-div">
<template v-if="ui.n45.textContent !== undefined"><text>{{ ui.n45.textContent }}</text></template><template v-else>
<text v-if="!ui.n46.removed" :class="classes(ui.n46.className)" :style="ui.n46.style" class="original-p">
<template v-if="ui.n46.textContent !== undefined"><text>{{ ui.n46.textContent }}</text></template><template v-else>
<text>王宁更新了成果大纲</text>
</template>
</text>
<span v-if="!ui.n47.removed" :class="classes(ui.n47.className)" :style="ui.n47.style" class="original-span">
<template v-if="ui.n47.textContent !== undefined"><text>{{ ui.n47.textContent }}</text></template><template v-else>
<text>v3→v4</text>
</template>
</span>
</template>
</view>
<view v-if="!ui.n48.removed" :class="classes(ui.n48.className)" :style="ui.n48.style" class="original-p">
<template v-if="ui.n48.textContent !== undefined"><text>{{ ui.n48.textContent }}</text></template><template v-else>
<text>新增问题分析章节，已生成版本</text>
</template>
</view>
<view v-if="!ui.n49.removed" :class="classes(ui.n49.className)" :style="ui.n49.style" class="original-p">
<template v-if="ui.n49.textContent !== undefined"><text>{{ ui.n49.textContent }}</text></template><template v-else>
<text>10:36 · 社区调研报告</text>
</template>
</view>
</template>
</view>
<span v-if="!ui.n50.removed" :class="classes(ui.n50.className)" :style="ui.n50.style" class="original-span">
<template v-if="ui.n50.textContent !== undefined"><text>{{ ui.n50.textContent }}</text></template><template v-else>

</template>
</span>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n51.removed" :class="classes(ui.n51.className)" :style="ui.n51.style" class="original-div">
<template v-if="ui.n51.textContent !== undefined"><text>{{ ui.n51.textContent }}</text></template><template v-else>
<view v-if="!ui.n52.removed" :class="classes(ui.n52.className)" :style="ui.n52.style" class="original-p">
<template v-if="ui.n52.textContent !== undefined"><text>{{ ui.n52.textContent }}</text></template><template v-else>
<text>昨天</text>
</template>
</view>
<view v-if="!ui.n53.removed" :class="classes(ui.n53.className)" :style="ui.n53.style" class="original-div">
<template v-if="ui.n53.textContent !== undefined"><text>{{ ui.n53.textContent }}</text></template><template v-else>
<view v-if="!ui.n54.removed" :class="classes(ui.n54.className)" :style="ui.n54.style" class="original-a" @tap.stop="tap('n54', $event)">
<template v-if="ui.n54.textContent !== undefined"><text>{{ ui.n54.textContent }}</text></template><template v-else>
<view v-if="!ui.n55.removed" :class="classes(ui.n55.className)" :style="ui.n55.style" class="original-div">
<template v-if="ui.n55.textContent !== undefined"><text>{{ ui.n55.textContent }}</text></template><template v-else>
<image v-if="!ui.n56.removed" :class="classes(ui.n56.className)" :style="ui.n56.style" class="original-icon" :src="iconSrc('n56')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n57.removed" :class="classes(ui.n57.className)" :style="ui.n57.style" class="original-div">
<template v-if="ui.n57.textContent !== undefined"><text>{{ ui.n57.textContent }}</text></template><template v-else>
<view v-if="!ui.n58.removed" :class="classes(ui.n58.className)" :style="ui.n58.style" class="original-p">
<template v-if="ui.n58.textContent !== undefined"><text>{{ ui.n58.textContent }}</text></template><template v-else>
<text>第 3 次线上研讨已记录</text>
</template>
</view>
<view v-if="!ui.n59.removed" :class="classes(ui.n59.className)" :style="ui.n59.style" class="original-p">
<template v-if="ui.n59.textContent !== undefined"><text>{{ ui.n59.textContent }}</text></template><template v-else>
<text>访谈提纲修订，新增第 5 题</text>
</template>
</view>
<view v-if="!ui.n60.removed" :class="classes(ui.n60.className)" :style="ui.n60.style" class="original-p">
<template v-if="ui.n60.textContent !== undefined"><text>{{ ui.n60.textContent }}</text></template><template v-else>
<text>19:30 · 社区调研报告</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n61.removed" :class="classes(ui.n61.className)" :style="ui.n61.style" class="original-a" @tap.stop="tap('n61', $event)">
<template v-if="ui.n61.textContent !== undefined"><text>{{ ui.n61.textContent }}</text></template><template v-else>
<view v-if="!ui.n62.removed" :class="classes(ui.n62.className)" :style="ui.n62.style" class="original-div">
<template v-if="ui.n62.textContent !== undefined"><text>{{ ui.n62.textContent }}</text></template><template v-else>
<image v-if="!ui.n63.removed" :class="classes(ui.n63.className)" :style="ui.n63.style" class="original-icon" :src="iconSrc('n63')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n64.removed" :class="classes(ui.n64.className)" :style="ui.n64.style" class="original-div">
<template v-if="ui.n64.textContent !== undefined"><text>{{ ui.n64.textContent }}</text></template><template v-else>
<view v-if="!ui.n65.removed" :class="classes(ui.n65.className)" :style="ui.n65.style" class="original-p">
<template v-if="ui.n65.textContent !== undefined"><text>{{ ui.n65.textContent }}</text></template><template v-else>
<text>许萌的访谈整理即将到期</text>
</template>
</view>
<view v-if="!ui.n66.removed" :class="classes(ui.n66.className)" :style="ui.n66.style" class="original-p">
<template v-if="ui.n66.textContent !== undefined"><text>{{ ui.n66.textContent }}</text></template><template v-else>
<text>将于 09/12 截止</text>
</template>
</view>
<view v-if="!ui.n67.removed" :class="classes(ui.n67.className)" :style="ui.n67.style" class="original-p">
<template v-if="ui.n67.textContent !== undefined"><text>{{ ui.n67.textContent }}</text></template><template v-else>
<text>08:00 · 系统提醒</text>
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
<view v-if="!ui.n68.removed" :class="classes(ui.n68.className)" :style="ui.n68.style" class="original-div">
<template v-if="ui.n68.textContent !== undefined"><text>{{ ui.n68.textContent }}</text></template><template v-else>
<view v-if="!ui.n69.removed" :class="classes(ui.n69.className)" :style="ui.n69.style" class="original-p">
<template v-if="ui.n69.textContent !== undefined"><text>{{ ui.n69.textContent }}</text></template><template v-else>
<text>更早</text>
</template>
</view>
<view v-if="!ui.n70.removed" :class="classes(ui.n70.className)" :style="ui.n70.style" class="original-div">
<template v-if="ui.n70.textContent !== undefined"><text>{{ ui.n70.textContent }}</text></template><template v-else>
<view v-if="!ui.n71.removed" :class="classes(ui.n71.className)" :style="ui.n71.style" class="original-div">
<template v-if="ui.n71.textContent !== undefined"><text>{{ ui.n71.textContent }}</text></template><template v-else>
<image v-if="!ui.n72.removed" :class="classes(ui.n72.className)" :style="ui.n72.style" class="original-icon" :src="iconSrc('n72')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n73.removed" :class="classes(ui.n73.className)" :style="ui.n73.style" class="original-div">
<template v-if="ui.n73.textContent !== undefined"><text>{{ ui.n73.textContent }}</text></template><template v-else>
<view v-if="!ui.n74.removed" :class="classes(ui.n74.className)" :style="ui.n74.style" class="original-p">
<template v-if="ui.n74.textContent !== undefined"><text>{{ ui.n74.textContent }}</text></template><template v-else>
<text>你确认了「选题与分工」成果</text>
</template>
</view>
<view v-if="!ui.n75.removed" :class="classes(ui.n75.className)" :style="ui.n75.style" class="original-p">
<template v-if="ui.n75.textContent !== undefined"><text>{{ ui.n75.textContent }}</text></template><template v-else>
<text>记录已归档</text>
</template>
</view>
<view v-if="!ui.n76.removed" :class="classes(ui.n76.className)" :style="ui.n76.style" class="original-p">
<template v-if="ui.n76.textContent !== undefined"><text>{{ ui.n76.textContent }}</text></template><template v-else>
<text>9月03日 · 社区调研报告</text>
</template>
</view>
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
