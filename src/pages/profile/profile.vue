<template>
<view class="prototype-screen">
<view v-if="!ui.n0.removed" :class="classes(ui.n0.className)" :style="ui.n0.style" class="original-div">
<template v-if="ui.n0.textContent !== undefined"><text>{{ ui.n0.textContent }}</text></template><template v-else>
<view v-if="!ui.n1.removed" :class="classes(ui.n1.className)" :style="ui.n1.style" class="original-header">
<template v-if="ui.n1.textContent !== undefined"><text>{{ ui.n1.textContent }}</text></template><template v-else>
<view v-if="!ui.n2.removed" :class="classes(ui.n2.className)" :style="ui.n2.style" class="original-p">
<template v-if="ui.n2.textContent !== undefined"><text>{{ ui.n2.textContent }}</text></template><template v-else>
<text>个人空间</text>
</template>
</view>
<view v-if="!ui.n3.removed" :class="classes(ui.n3.className)" :style="ui.n3.style" class="original-h1">
<template v-if="ui.n3.textContent !== undefined"><text>{{ ui.n3.textContent }}</text></template><template v-else>
<text>我的</text>
</template>
</view>
</template>
</view>
<scroll-view v-if="!ui.n4.removed" :class="classes(ui.n4.className)" :style="ui.n4.style" class="original-scroll" scroll-y :show-scrollbar="false">
<template v-if="ui.n4.textContent !== undefined"><text>{{ ui.n4.textContent }}</text></template><template v-else>
<view v-if="!ui.n5.removed" :class="classes(ui.n5.className)" :style="ui.n5.style" class="original-div">
<template v-if="ui.n5.textContent !== undefined"><text>{{ ui.n5.textContent }}</text></template><template v-else>
<view v-if="!ui.n6.removed" :class="classes(ui.n6.className)" :style="ui.n6.style" class="original-div">
<template v-if="ui.n6.textContent !== undefined"><text>{{ ui.n6.textContent }}</text></template><template v-else>
<view v-if="!ui.n7.removed" :class="classes(ui.n7.className)" :style="ui.n7.style" class="original-div">
<template v-if="ui.n7.textContent !== undefined"><text>{{ ui.n7.textContent }}</text></template><template v-else>
<text>林</text>
</template>
</view>
<view v-if="!ui.n8.removed" :class="classes(ui.n8.className)" :style="ui.n8.style" class="original-div">
<template v-if="ui.n8.textContent !== undefined"><text>{{ ui.n8.textContent }}</text></template><template v-else>
<view v-if="!ui.n9.removed" :class="classes(ui.n9.className)" :style="ui.n9.style" class="original-h2">
<template v-if="ui.n9.textContent !== undefined"><text>{{ ui.n9.textContent }}</text></template><template v-else>
<text>林知行</text>
</template>
</view>
<view v-if="!ui.n10.removed" :class="classes(ui.n10.className)" :style="ui.n10.style" class="original-p">
<template v-if="ui.n10.textContent !== undefined"><text>{{ ui.n10.textContent }}</text></template><template v-else>
<text>软件工程 2026 · 3 个项目</text>
</template>
</view>
<view v-if="!ui.n11.removed" :class="classes(ui.n11.className)" :style="ui.n11.style" class="original-div">
<template v-if="ui.n11.textContent !== undefined"><text>{{ ui.n11.textContent }}</text></template><template v-else>
<span v-if="!ui.n12.removed" :class="classes(ui.n12.className)" :style="ui.n12.style" class="original-span">
<template v-if="ui.n12.textContent !== undefined"><text>{{ ui.n12.textContent }}</text></template><template v-else>
<text>组长</text>
</template>
</span>
<span v-if="!ui.n13.removed" :class="classes(ui.n13.className)" :style="ui.n13.style" class="original-span">
<template v-if="ui.n13.textContent !== undefined"><text>{{ ui.n13.textContent }}</text></template><template v-else>
<text>助教</text>
</template>
</span>
</template>
</view>
</template>
</view>
<button v-if="!ui.n14.removed" :class="classes(ui.n14.className)" :style="ui.n14.style" class="original-button" @tap.stop="tap('n14', $event)">
<template v-if="ui.n14.textContent !== undefined"><text>{{ ui.n14.textContent }}</text></template><template v-else>
<image v-if="!ui.n15.removed" :class="classes(ui.n15.className)" :style="ui.n15.style" class="original-icon" :src="iconSrc('n15')" style="width:16px;height:16px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n16.removed" :class="classes(ui.n16.className)" :style="ui.n16.style" class="original-div">
<template v-if="ui.n16.textContent !== undefined"><text>{{ ui.n16.textContent }}</text></template><template v-else>
<view v-if="!ui.n17.removed" :class="classes(ui.n17.className)" :style="ui.n17.style" class="original-div">
<template v-if="ui.n17.textContent !== undefined"><text>{{ ui.n17.textContent }}</text></template><template v-else>
<view v-if="!ui.n18.removed" :class="classes(ui.n18.className)" :style="ui.n18.style" class="original-p">
<template v-if="ui.n18.textContent !== undefined"><text>{{ ui.n18.textContent }}</text></template><template v-else>
<text>24</text>
</template>
</view>
<view v-if="!ui.n19.removed" :class="classes(ui.n19.className)" :style="ui.n19.style" class="original-p">
<template v-if="ui.n19.textContent !== undefined"><text>{{ ui.n19.textContent }}</text></template><template v-else>
<text>文件</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n20.removed" :class="classes(ui.n20.className)" :style="ui.n20.style" class="original-div">
<template v-if="ui.n20.textContent !== undefined"><text>{{ ui.n20.textContent }}</text></template><template v-else>
<view v-if="!ui.n21.removed" :class="classes(ui.n21.className)" :style="ui.n21.style" class="original-p">
<template v-if="ui.n21.textContent !== undefined"><text>{{ ui.n21.textContent }}</text></template><template v-else>
<text>18</text>
</template>
</view>
<view v-if="!ui.n22.removed" :class="classes(ui.n22.className)" :style="ui.n22.style" class="original-p">
<template v-if="ui.n22.textContent !== undefined"><text>{{ ui.n22.textContent }}</text></template><template v-else>
<text>版本</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n23.removed" :class="classes(ui.n23.className)" :style="ui.n23.style" class="original-div">
<template v-if="ui.n23.textContent !== undefined"><text>{{ ui.n23.textContent }}</text></template><template v-else>
<view v-if="!ui.n24.removed" :class="classes(ui.n24.className)" :style="ui.n24.style" class="original-p">
<template v-if="ui.n24.textContent !== undefined"><text>{{ ui.n24.textContent }}</text></template><template v-else>
<text>7</text>
</template>
</view>
<view v-if="!ui.n25.removed" :class="classes(ui.n25.className)" :style="ui.n25.style" class="original-p">
<template v-if="ui.n25.textContent !== undefined"><text>{{ ui.n25.textContent }}</text></template><template v-else>
<text>待审</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n26.removed" :class="classes(ui.n26.className)" :style="ui.n26.style" class="original-div">
<template v-if="ui.n26.textContent !== undefined"><text>{{ ui.n26.textContent }}</text></template><template v-else>
<view v-if="!ui.n27.removed" :class="classes(ui.n27.className)" :style="ui.n27.style" class="original-p">
<template v-if="ui.n27.textContent !== undefined"><text>{{ ui.n27.textContent }}</text></template><template v-else>
<text>4</text>
</template>
</view>
<view v-if="!ui.n28.removed" :class="classes(ui.n28.className)" :style="ui.n28.style" class="original-p">
<template v-if="ui.n28.textContent !== undefined"><text>{{ ui.n28.textContent }}</text></template><template v-else>
<text>归档</text>
</template>
</view>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n29.removed" :class="classes(ui.n29.className)" :style="ui.n29.style" class="original-a" @tap.stop="tap('n29', $event)">
<template v-if="ui.n29.textContent !== undefined"><text>{{ ui.n29.textContent }}</text></template><template v-else>
<view v-if="!ui.n30.removed" :class="classes(ui.n30.className)" :style="ui.n30.style" class="original-div">
<template v-if="ui.n30.textContent !== undefined"><text>{{ ui.n30.textContent }}</text></template><template v-else>
<view v-if="!ui.n31.removed" :class="classes(ui.n31.className)" :style="ui.n31.style" class="original-div">
<template v-if="ui.n31.textContent !== undefined"><text>{{ ui.n31.textContent }}</text></template><template v-else>
<image v-if="!ui.n32.removed" :class="classes(ui.n32.className)" :style="ui.n32.style" class="original-icon" :src="iconSrc('n32')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n33.removed" :class="classes(ui.n33.className)" :style="ui.n33.style" class="original-div">
<template v-if="ui.n33.textContent !== undefined"><text>{{ ui.n33.textContent }}</text></template><template v-else>
<view v-if="!ui.n34.removed" :class="classes(ui.n34.className)" :style="ui.n34.style" class="original-p">
<template v-if="ui.n34.textContent !== undefined"><text>{{ ui.n34.textContent }}</text></template><template v-else>
<text>7 份挂单待审核</text>
</template>
</view>
<view v-if="!ui.n35.removed" :class="classes(ui.n35.className)" :style="ui.n35.style" class="original-p">
<template v-if="ui.n35.textContent !== undefined"><text>{{ ui.n35.textContent }}</text></template><template v-else>
<text>作为组长，请尽快处理</text>
</template>
</view>
</template>
</view>
<image v-if="!ui.n36.removed" :class="classes(ui.n36.className)" :style="ui.n36.style" class="original-icon" :src="iconSrc('n36')" style="width:16px;height:16px" mode="aspectFit" />
</template>
</view>
</template>
</view>
<view v-if="!ui.n37.removed" :class="classes(ui.n37.className)" :style="ui.n37.style" class="original-div">
<template v-if="ui.n37.textContent !== undefined"><text>{{ ui.n37.textContent }}</text></template><template v-else>
<view v-if="!ui.n38.removed" :class="classes(ui.n38.className)" :style="ui.n38.style" class="original-h2">
<template v-if="ui.n38.textContent !== undefined"><text>{{ ui.n38.textContent }}</text></template><template v-else>
<text>我的项目</text>
</template>
</view>
<view v-if="!ui.n39.removed" :class="classes(ui.n39.className)" :style="ui.n39.style" class="original-div">
<template v-if="ui.n39.textContent !== undefined"><text>{{ ui.n39.textContent }}</text></template><template v-else>
<view v-if="!ui.n40.removed" :class="classes(ui.n40.className)" :style="ui.n40.style" class="original-a" @tap.stop="tap('n40', $event)">
<template v-if="ui.n40.textContent !== undefined"><text>{{ ui.n40.textContent }}</text></template><template v-else>
<view v-if="!ui.n41.removed" :class="classes(ui.n41.className)" :style="ui.n41.style" class="original-div">
<template v-if="ui.n41.textContent !== undefined"><text>{{ ui.n41.textContent }}</text></template><template v-else>
<image v-if="!ui.n42.removed" :class="classes(ui.n42.className)" :style="ui.n42.style" class="original-icon" :src="iconSrc('n42')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n43.removed" :class="classes(ui.n43.className)" :style="ui.n43.style" class="original-span">
<template v-if="ui.n43.textContent !== undefined"><text>{{ ui.n43.textContent }}</text></template><template v-else>
<text>进行中项目</text>
</template>
</view>
<span v-if="!ui.n44.removed" :class="classes(ui.n44.className)" :style="ui.n44.style" class="original-span">
<template v-if="ui.n44.textContent !== undefined"><text>{{ ui.n44.textContent }}</text></template><template v-else>
<text>3</text>
</template>
</span>
<image v-if="!ui.n45.removed" :class="classes(ui.n45.className)" :style="ui.n45.style" class="original-icon" :src="iconSrc('n45')" style="width:16px;height:16px" mode="aspectFit" />
</template>
</view>
<button v-if="!ui.n46.removed" :class="classes(ui.n46.className)" :style="ui.n46.style" class="original-button" @tap.stop="tap('n46', $event)">
<template v-if="ui.n46.textContent !== undefined"><text>{{ ui.n46.textContent }}</text></template><template v-else>
<view v-if="!ui.n47.removed" :class="classes(ui.n47.className)" :style="ui.n47.style" class="original-div">
<template v-if="ui.n47.textContent !== undefined"><text>{{ ui.n47.textContent }}</text></template><template v-else>
<image v-if="!ui.n48.removed" :class="classes(ui.n48.className)" :style="ui.n48.style" class="original-icon" :src="iconSrc('n48')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n49.removed" :class="classes(ui.n49.className)" :style="ui.n49.style" class="original-span">
<template v-if="ui.n49.textContent !== undefined"><text>{{ ui.n49.textContent }}</text></template><template v-else>
<text>操作记录</text>
</template>
</view>
<image v-if="!ui.n50.removed" :class="classes(ui.n50.className)" :style="ui.n50.style" class="original-icon" :src="iconSrc('n50')" style="width:16px;height:16px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n51.removed" :class="classes(ui.n51.className)" :style="ui.n51.style" class="original-button" @tap.stop="tap('n51', $event)">
<template v-if="ui.n51.textContent !== undefined"><text>{{ ui.n51.textContent }}</text></template><template v-else>
<view v-if="!ui.n52.removed" :class="classes(ui.n52.className)" :style="ui.n52.style" class="original-div">
<template v-if="ui.n52.textContent !== undefined"><text>{{ ui.n52.textContent }}</text></template><template v-else>
<image v-if="!ui.n53.removed" :class="classes(ui.n53.className)" :style="ui.n53.style" class="original-icon" :src="iconSrc('n53')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n54.removed" :class="classes(ui.n54.className)" :style="ui.n54.style" class="original-div">
<template v-if="ui.n54.textContent !== undefined"><text>{{ ui.n54.textContent }}</text></template><template v-else>
<view v-if="!ui.n55.removed" :class="classes(ui.n55.className)" :style="ui.n55.style" class="original-p">
<template v-if="ui.n55.textContent !== undefined"><text>{{ ui.n55.textContent }}</text></template><template v-else>
<text>期末归档</text>
</template>
</view>
<view v-if="!ui.n56.removed" :class="classes(ui.n56.className)" :style="ui.n56.style" class="original-p">
<template v-if="ui.n56.textContent !== undefined"><text>{{ ui.n56.textContent }}</text></template><template v-else>
<text>一键批量归档 · 4 个项目可归档</text>
</template>
</view>
</template>
</view>
<image v-if="!ui.n57.removed" :class="classes(ui.n57.className)" :style="ui.n57.style" class="original-icon" :src="iconSrc('n57')" style="width:16px;height:16px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n58.removed" :class="classes(ui.n58.className)" :style="ui.n58.style" class="original-button" @tap.stop="tap('n58', $event)">
<template v-if="ui.n58.textContent !== undefined"><text>{{ ui.n58.textContent }}</text></template><template v-else>
<view v-if="!ui.n59.removed" :class="classes(ui.n59.className)" :style="ui.n59.style" class="original-div">
<template v-if="ui.n59.textContent !== undefined"><text>{{ ui.n59.textContent }}</text></template><template v-else>
<image v-if="!ui.n60.removed" :class="classes(ui.n60.className)" :style="ui.n60.style" class="original-icon" :src="iconSrc('n60')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n61.removed" :class="classes(ui.n61.className)" :style="ui.n61.style" class="original-span">
<template v-if="ui.n61.textContent !== undefined"><text>{{ ui.n61.textContent }}</text></template><template v-else>
<text>存储与下载</text>
</template>
</view>
<image v-if="!ui.n62.removed" :class="classes(ui.n62.className)" :style="ui.n62.style" class="original-icon" :src="iconSrc('n62')" style="width:16px;height:16px" mode="aspectFit" />
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n63.removed" :class="classes(ui.n63.className)" :style="ui.n63.style" class="original-div">
<template v-if="ui.n63.textContent !== undefined"><text>{{ ui.n63.textContent }}</text></template><template v-else>
<view v-if="!ui.n64.removed" :class="classes(ui.n64.className)" :style="ui.n64.style" class="original-h2">
<template v-if="ui.n64.textContent !== undefined"><text>{{ ui.n64.textContent }}</text></template><template v-else>
<text>教学管理</text>
</template>
</view>
<view v-if="!ui.n65.removed" :class="classes(ui.n65.className)" :style="ui.n65.style" class="original-div">
<template v-if="ui.n65.textContent !== undefined"><text>{{ ui.n65.textContent }}</text></template><template v-else>
<button v-if="!ui.n66.removed" :class="classes(ui.n66.className)" :style="ui.n66.style" class="original-button" @tap.stop="tap('n66', $event)">
<template v-if="ui.n66.textContent !== undefined"><text>{{ ui.n66.textContent }}</text></template><template v-else>
<view v-if="!ui.n67.removed" :class="classes(ui.n67.className)" :style="ui.n67.style" class="original-div">
<template v-if="ui.n67.textContent !== undefined"><text>{{ ui.n67.textContent }}</text></template><template v-else>
<image v-if="!ui.n68.removed" :class="classes(ui.n68.className)" :style="ui.n68.style" class="original-icon" :src="iconSrc('n68')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n69.removed" :class="classes(ui.n69.className)" :style="ui.n69.style" class="original-div">
<template v-if="ui.n69.textContent !== undefined"><text>{{ ui.n69.textContent }}</text></template><template v-else>
<view v-if="!ui.n70.removed" :class="classes(ui.n70.className)" :style="ui.n70.style" class="original-p">
<template v-if="ui.n70.textContent !== undefined"><text>{{ ui.n70.textContent }}</text></template><template v-else>
<text>我指导的小组</text>
</template>
</view>
<view v-if="!ui.n71.removed" :class="classes(ui.n71.className)" :style="ui.n71.style" class="original-p">
<template v-if="ui.n71.textContent !== undefined"><text>{{ ui.n71.textContent }}</text></template><template v-else>
<text>8 个小组 · 查阅过程材料</text>
</template>
</view>
</template>
</view>
<image v-if="!ui.n72.removed" :class="classes(ui.n72.className)" :style="ui.n72.style" class="original-icon" :src="iconSrc('n72')" style="width:16px;height:16px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n73.removed" :class="classes(ui.n73.className)" :style="ui.n73.style" class="original-button" @tap.stop="tap('n73', $event)">
<template v-if="ui.n73.textContent !== undefined"><text>{{ ui.n73.textContent }}</text></template><template v-else>
<view v-if="!ui.n74.removed" :class="classes(ui.n74.className)" :style="ui.n74.style" class="original-div">
<template v-if="ui.n74.textContent !== undefined"><text>{{ ui.n74.textContent }}</text></template><template v-else>
<image v-if="!ui.n75.removed" :class="classes(ui.n75.className)" :style="ui.n75.style" class="original-icon" :src="iconSrc('n75')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n76.removed" :class="classes(ui.n76.className)" :style="ui.n76.style" class="original-div">
<template v-if="ui.n76.textContent !== undefined"><text>{{ ui.n76.textContent }}</text></template><template v-else>
<view v-if="!ui.n77.removed" :class="classes(ui.n77.className)" :style="ui.n77.style" class="original-p">
<template v-if="ui.n77.textContent !== undefined"><text>{{ ui.n77.textContent }}</text></template><template v-else>
<text>过程性评价</text>
</template>
</view>
<view v-if="!ui.n78.removed" :class="classes(ui.n78.className)" :style="ui.n78.style" class="original-p">
<template v-if="ui.n78.textContent !== undefined"><text>{{ ui.n78.textContent }}</text></template><template v-else>
<text>查看全部过程材料 · 给出评价</text>
</template>
</view>
</template>
</view>
<image v-if="!ui.n79.removed" :class="classes(ui.n79.className)" :style="ui.n79.style" class="original-icon" :src="iconSrc('n79')" style="width:16px;height:16px" mode="aspectFit" />
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n80.removed" :class="classes(ui.n80.className)" :style="ui.n80.style" class="original-div">
<template v-if="ui.n80.textContent !== undefined"><text>{{ ui.n80.textContent }}</text></template><template v-else>
<view v-if="!ui.n81.removed" :class="classes(ui.n81.className)" :style="ui.n81.style" class="original-h2">
<template v-if="ui.n81.textContent !== undefined"><text>{{ ui.n81.textContent }}</text></template><template v-else>
<text>通知</text>
</template>
</view>
<view v-if="!ui.n82.removed" :class="classes(ui.n82.className)" :style="ui.n82.style" class="original-div">
<template v-if="ui.n82.textContent !== undefined"><text>{{ ui.n82.textContent }}</text></template><template v-else>
<view v-if="!ui.n83.removed" :class="classes(ui.n83.className)" :style="ui.n83.style" class="original-div">
<template v-if="ui.n83.textContent !== undefined"><text>{{ ui.n83.textContent }}</text></template><template v-else>
<view v-if="!ui.n84.removed" :class="classes(ui.n84.className)" :style="ui.n84.style" class="original-div">
<template v-if="ui.n84.textContent !== undefined"><text>{{ ui.n84.textContent }}</text></template><template v-else>
<view v-if="!ui.n85.removed" :class="classes(ui.n85.className)" :style="ui.n85.style" class="original-p">
<template v-if="ui.n85.textContent !== undefined"><text>{{ ui.n85.textContent }}</text></template><template v-else>
<text>项目变更提醒</text>
</template>
</view>
<view v-if="!ui.n86.removed" :class="classes(ui.n86.className)" :style="ui.n86.style" class="original-p">
<template v-if="ui.n86.textContent !== undefined"><text>{{ ui.n86.textContent }}</text></template><template v-else>
<text>文件上传、版本更新与截止</text>
</template>
</view>
</template>
</view>
<button v-if="!ui.n87.removed" :class="classes(ui.n87.className)" :style="ui.n87.style" class="original-button" @tap.stop="tap('n87', $event)">
<template v-if="ui.n87.textContent !== undefined"><text>{{ ui.n87.textContent }}</text></template><template v-else>
<view v-if="!ui.n88.removed" :class="classes(ui.n88.className)" :style="ui.n88.style" class="original-span">
<template v-if="ui.n88.textContent !== undefined"><text>{{ ui.n88.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</button>
</template>
</view>
<view v-if="!ui.n89.removed" :class="classes(ui.n89.className)" :style="ui.n89.style" class="original-div">
<template v-if="ui.n89.textContent !== undefined"><text>{{ ui.n89.textContent }}</text></template><template v-else>
<view v-if="!ui.n90.removed" :class="classes(ui.n90.className)" :style="ui.n90.style" class="original-div">
<template v-if="ui.n90.textContent !== undefined"><text>{{ ui.n90.textContent }}</text></template><template v-else>
<view v-if="!ui.n91.removed" :class="classes(ui.n91.className)" :style="ui.n91.style" class="original-p">
<template v-if="ui.n91.textContent !== undefined"><text>{{ ui.n91.textContent }}</text></template><template v-else>
<text>审核结果通知</text>
</template>
</view>
<view v-if="!ui.n92.removed" :class="classes(ui.n92.className)" :style="ui.n92.style" class="original-p">
<template v-if="ui.n92.textContent !== undefined"><text>{{ ui.n92.textContent }}</text></template><template v-else>
<text>挂单通过 / 驳回</text>
</template>
</view>
</template>
</view>
<button v-if="!ui.n93.removed" :class="classes(ui.n93.className)" :style="ui.n93.style" class="original-button" @tap.stop="tap('n93', $event)">
<template v-if="ui.n93.textContent !== undefined"><text>{{ ui.n93.textContent }}</text></template><template v-else>
<view v-if="!ui.n94.removed" :class="classes(ui.n94.className)" :style="ui.n94.style" class="original-span">
<template v-if="ui.n94.textContent !== undefined"><text>{{ ui.n94.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n95.removed" :class="classes(ui.n95.className)" :style="ui.n95.style" class="original-div">
<template v-if="ui.n95.textContent !== undefined"><text>{{ ui.n95.textContent }}</text></template><template v-else>
<button v-if="!ui.n96.removed" :class="classes(ui.n96.className)" :style="ui.n96.style" class="original-button" @tap.stop="tap('n96', $event)">
<template v-if="ui.n96.textContent !== undefined"><text>{{ ui.n96.textContent }}</text></template><template v-else>
<text>退出登录</text>
</template>
</button>
</template>
</view>
<view v-if="!ui.n97.removed" :class="classes(ui.n97.className)" :style="ui.n97.style" class="original-div">
<template v-if="ui.n97.textContent !== undefined"><text>{{ ui.n97.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</scroll-view>
<view v-if="!ui.n98.removed" :class="classes(ui.n98.className)" :style="ui.n98.style" class="original-nav">
<template v-if="ui.n98.textContent !== undefined"><text>{{ ui.n98.textContent }}</text></template><template v-else>
<view v-if="!ui.n99.removed" :class="classes(ui.n99.className)" :style="ui.n99.style" class="original-a" @tap.stop="tap('n99', $event)">
<template v-if="ui.n99.textContent !== undefined"><text>{{ ui.n99.textContent }}</text></template><template v-else>
<image v-if="!ui.n100.removed" :class="classes(ui.n100.className)" :style="ui.n100.style" class="original-icon" :src="iconSrc('n100')" style="width:22px;height:22px" mode="aspectFit" />
<span v-if="!ui.n101.removed" :class="classes(ui.n101.className)" :style="ui.n101.style" class="original-span">
<template v-if="ui.n101.textContent !== undefined"><text>{{ ui.n101.textContent }}</text></template><template v-else>
<text>首页</text>
</template>
</span>
</template>
</view>
<view v-if="!ui.n102.removed" :class="classes(ui.n102.className)" :style="ui.n102.style" class="original-a" @tap.stop="tap('n102', $event)">
<template v-if="ui.n102.textContent !== undefined"><text>{{ ui.n102.textContent }}</text></template><template v-else>
<image v-if="!ui.n103.removed" :class="classes(ui.n103.className)" :style="ui.n103.style" class="original-icon" :src="iconSrc('n103')" style="width:22px;height:22px" mode="aspectFit" />
<view v-if="!ui.n104.removed" :class="classes(ui.n104.className)" :style="ui.n104.style" class="original-span">
<template v-if="ui.n104.textContent !== undefined"><text>{{ ui.n104.textContent }}</text></template><template v-else>
<text>消息</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n105.removed" :class="classes(ui.n105.className)" :style="ui.n105.style" class="original-a" @tap.stop="tap('n105', $event)">
<template v-if="ui.n105.textContent !== undefined"><text>{{ ui.n105.textContent }}</text></template><template v-else>
<image v-if="!ui.n106.removed" :class="classes(ui.n106.className)" :style="ui.n106.style" class="original-icon" :src="iconSrc('n106')" style="width:22px;height:22px" mode="aspectFit" />
<view v-if="!ui.n107.removed" :class="classes(ui.n107.className)" :style="ui.n107.style" class="original-span">
<template v-if="ui.n107.textContent !== undefined"><text>{{ ui.n107.textContent }}</text></template><template v-else>
<text>我的</text>
</template>
</view>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n108.removed" :class="classes(ui.n108.className)" :style="ui.n108.style" class="original-div" @tap.stop="tap('n108', $event)">
<template v-if="ui.n108.textContent !== undefined"><text>{{ ui.n108.textContent }}</text></template><template v-else>
<view v-if="!ui.n109.removed" :class="classes(ui.n109.className)" :style="ui.n109.style" class="original-div" @tap.stop="tap('n109', $event)">
<template v-if="ui.n109.textContent !== undefined"><text>{{ ui.n109.textContent }}</text></template><template v-else>
<view v-if="!ui.n110.removed" :class="classes(ui.n110.className)" :style="ui.n110.style" class="original-div">
<template v-if="ui.n110.textContent !== undefined"><text>{{ ui.n110.textContent }}</text></template><template v-else>
<text v-if="!ui.n111.removed" :class="classes(ui.n111.className)" :style="ui.n111.style" class="original-p">
<template v-if="ui.n111.textContent !== undefined"><text>{{ ui.n111.textContent }}</text></template><template v-else>
<text>期末整体归档</text>
</template>
</text>
<button v-if="!ui.n112.removed" :class="classes(ui.n112.className)" :style="ui.n112.style" class="original-button" @tap.stop="tap('n112', $event)">
<template v-if="ui.n112.textContent !== undefined"><text>{{ ui.n112.textContent }}</text></template><template v-else>
<image v-if="!ui.n113.removed" :class="classes(ui.n113.className)" :style="ui.n113.style" class="original-icon" :src="iconSrc('n113')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n114.removed" :class="classes(ui.n114.className)" :style="ui.n114.style" class="original-p">
<template v-if="ui.n114.textContent !== undefined"><text>{{ ui.n114.textContent }}</text></template><template v-else>
<text>勾选需要归档的项目，归档将打包全部阶段成果、研讨记录、版本历史，用于教学备查与案例复用。</text>
</template>
</view>
<view v-if="!ui.n115.removed" :class="classes(ui.n115.className)" :style="ui.n115.style" class="original-div">
<template v-if="ui.n115.textContent !== undefined"><text>{{ ui.n115.textContent }}</text></template><template v-else>
<view v-if="!ui.n116.removed" :class="classes(ui.n116.className)" :style="ui.n116.style" class="original-label" @tap.stop="ui.n117.checked = !ui.n117.checked">
<template v-if="ui.n116.textContent !== undefined"><text>{{ ui.n116.textContent }}</text></template><template v-else>
<view v-if="!ui.n117.removed" :class="classes(ui.n117.className)" :style="ui.n117.style" class="original-checkbox" @tap.stop="ui.n117.checked = !ui.n117.checked"><image v-if="ui.n117.checked" src="/static/icons/check--white.png" style="width:100%;height:100%;background:#2563eb;border-radius:3px" /><view v-else class="checkbox-empty" /></view>
<view v-if="!ui.n118.removed" :class="classes(ui.n118.className)" :style="ui.n118.style" class="original-div">
<template v-if="ui.n118.textContent !== undefined"><text>{{ ui.n118.textContent }}</text></template><template v-else>
<view v-if="!ui.n119.removed" :class="classes(ui.n119.className)" :style="ui.n119.style" class="original-p">
<template v-if="ui.n119.textContent !== undefined"><text>{{ ui.n119.textContent }}</text></template><template v-else>
<text>社区调研报告</text>
</template>
</view>
<view v-if="!ui.n120.removed" :class="classes(ui.n120.className)" :style="ui.n120.style" class="original-p">
<template v-if="ui.n120.textContent !== undefined"><text>{{ ui.n120.textContent }}</text></template><template v-else>
<text>12份资料 · 18个版本 · 3次研讨</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n121.removed" :class="classes(ui.n121.className)" :style="ui.n121.style" class="original-span">
<template v-if="ui.n121.textContent !== undefined"><text>{{ ui.n121.textContent }}</text></template><template v-else>
<text>可归档</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n122.removed" :class="classes(ui.n122.className)" :style="ui.n122.style" class="original-label" @tap.stop="ui.n123.checked = !ui.n123.checked">
<template v-if="ui.n122.textContent !== undefined"><text>{{ ui.n122.textContent }}</text></template><template v-else>
<view v-if="!ui.n123.removed" :class="classes(ui.n123.className)" :style="ui.n123.style" class="original-checkbox" @tap.stop="ui.n123.checked = !ui.n123.checked"><image v-if="ui.n123.checked" src="/static/icons/check--white.png" style="width:100%;height:100%;background:#2563eb;border-radius:3px" /><view v-else class="checkbox-empty" /></view>
<view v-if="!ui.n124.removed" :class="classes(ui.n124.className)" :style="ui.n124.style" class="original-div">
<template v-if="ui.n124.textContent !== undefined"><text>{{ ui.n124.textContent }}</text></template><template v-else>
<view v-if="!ui.n125.removed" :class="classes(ui.n125.className)" :style="ui.n125.style" class="original-p">
<template v-if="ui.n125.textContent !== undefined"><text>{{ ui.n125.textContent }}</text></template><template v-else>
<text>统计学小组作业</text>
</template>
</view>
<view v-if="!ui.n126.removed" :class="classes(ui.n126.className)" :style="ui.n126.style" class="original-p">
<template v-if="ui.n126.textContent !== undefined"><text>{{ ui.n126.textContent }}</text></template><template v-else>
<text>8份资料 · 5个版本 · 1次研讨</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n127.removed" :class="classes(ui.n127.className)" :style="ui.n127.style" class="original-span">
<template v-if="ui.n127.textContent !== undefined"><text>{{ ui.n127.textContent }}</text></template><template v-else>
<text>可归档</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n128.removed" :class="classes(ui.n128.className)" :style="ui.n128.style" class="original-label" @tap.stop="ui.n129.checked = !ui.n129.checked">
<template v-if="ui.n128.textContent !== undefined"><text>{{ ui.n128.textContent }}</text></template><template v-else>
<view v-if="!ui.n129.removed" :class="classes(ui.n129.className)" :style="ui.n129.style" class="original-checkbox" @tap.stop="ui.n129.checked = !ui.n129.checked"><image v-if="ui.n129.checked" src="/static/icons/check--white.png" style="width:100%;height:100%;background:#2563eb;border-radius:3px" /><view v-else class="checkbox-empty" /></view>
<view v-if="!ui.n130.removed" :class="classes(ui.n130.className)" :style="ui.n130.style" class="original-div">
<template v-if="ui.n130.textContent !== undefined"><text>{{ ui.n130.textContent }}</text></template><template v-else>
<view v-if="!ui.n131.removed" :class="classes(ui.n131.className)" :style="ui.n131.style" class="original-p">
<template v-if="ui.n131.textContent !== undefined"><text>{{ ui.n131.textContent }}</text></template><template v-else>
<text>校园文化调研</text>
</template>
</view>
<view v-if="!ui.n132.removed" :class="classes(ui.n132.className)" :style="ui.n132.style" class="original-p">
<template v-if="ui.n132.textContent !== undefined"><text>{{ ui.n132.textContent }}</text></template><template v-else>
<text>筹备中 · 0份资料</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n133.removed" :class="classes(ui.n133.className)" :style="ui.n133.style" class="original-span">
<template v-if="ui.n133.textContent !== undefined"><text>{{ ui.n133.textContent }}</text></template><template v-else>
<text>未开始</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n134.removed" :class="classes(ui.n134.className)" :style="ui.n134.style" class="original-label" @tap.stop="ui.n135.checked = !ui.n135.checked">
<template v-if="ui.n134.textContent !== undefined"><text>{{ ui.n134.textContent }}</text></template><template v-else>
<view v-if="!ui.n135.removed" :class="classes(ui.n135.className)" :style="ui.n135.style" class="original-checkbox" @tap.stop="ui.n135.checked = !ui.n135.checked"><image v-if="ui.n135.checked" src="/static/icons/check--white.png" style="width:100%;height:100%;background:#2563eb;border-radius:3px" /><view v-else class="checkbox-empty" /></view>
<view v-if="!ui.n136.removed" :class="classes(ui.n136.className)" :style="ui.n136.style" class="original-div">
<template v-if="ui.n136.textContent !== undefined"><text>{{ ui.n136.textContent }}</text></template><template v-else>
<view v-if="!ui.n137.removed" :class="classes(ui.n137.className)" :style="ui.n137.style" class="original-p">
<template v-if="ui.n137.textContent !== undefined"><text>{{ ui.n137.textContent }}</text></template><template v-else>
<text>数据库设计报告</text>
</template>
</view>
<view v-if="!ui.n138.removed" :class="classes(ui.n138.className)" :style="ui.n138.style" class="original-p">
<template v-if="ui.n138.textContent !== undefined"><text>{{ ui.n138.textContent }}</text></template><template v-else>
<text>3份资料 · 2个版本</text>
</template>
</view>
</template>
</view>
<span v-if="!ui.n139.removed" :class="classes(ui.n139.className)" :style="ui.n139.style" class="original-span">
<template v-if="ui.n139.textContent !== undefined"><text>{{ ui.n139.textContent }}</text></template><template v-else>
<text>可归档</text>
</template>
</span>
</template>
</view>
</template>
</view>
<view v-if="!ui.n140.removed" :class="classes(ui.n140.className)" :style="ui.n140.style" class="original-div">
<template v-if="ui.n140.textContent !== undefined"><text>{{ ui.n140.textContent }}</text></template><template v-else>
<view v-if="!ui.n141.removed" :class="classes(ui.n141.className)" :style="ui.n141.style" class="original-p">
<template v-if="ui.n141.textContent !== undefined"><text>{{ ui.n141.textContent }}</text></template><template v-else>
<text>归档将包含</text>
</template>
</view>
<view v-if="!ui.n142.removed" :class="classes(ui.n142.className)" :style="ui.n142.style" class="original-div">
<template v-if="ui.n142.textContent !== undefined"><text>{{ ui.n142.textContent }}</text></template><template v-else>
<view v-if="!ui.n143.removed" :class="classes(ui.n143.className)" :style="ui.n143.style" class="original-div">
<template v-if="ui.n143.textContent !== undefined"><text>{{ ui.n143.textContent }}</text></template><template v-else>
<image v-if="!ui.n144.removed" :class="classes(ui.n144.className)" :style="ui.n144.style" class="original-icon" :src="iconSrc('n144')" style="width:12px;height:12px" mode="aspectFit" />
<view v-if="!ui.n145.removed" :class="classes(ui.n145.className)" :style="ui.n145.style" class="original-span">
<template v-if="ui.n145.textContent !== undefined"><text>{{ ui.n145.textContent }}</text></template><template v-else>
<text>全部文件（23份）</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n146.removed" :class="classes(ui.n146.className)" :style="ui.n146.style" class="original-div">
<template v-if="ui.n146.textContent !== undefined"><text>{{ ui.n146.textContent }}</text></template><template v-else>
<image v-if="!ui.n147.removed" :class="classes(ui.n147.className)" :style="ui.n147.style" class="original-icon" :src="iconSrc('n147')" style="width:12px;height:12px" mode="aspectFit" />
<span v-if="!ui.n148.removed" :class="classes(ui.n148.className)" :style="ui.n148.style" class="original-span">
<template v-if="ui.n148.textContent !== undefined"><text>{{ ui.n148.textContent }}</text></template><template v-else>
<text>全部阶段（7个）</text>
</template>
</span>
</template>
</view>
<view v-if="!ui.n149.removed" :class="classes(ui.n149.className)" :style="ui.n149.style" class="original-div">
<template v-if="ui.n149.textContent !== undefined"><text>{{ ui.n149.textContent }}</text></template><template v-else>
<image v-if="!ui.n150.removed" :class="classes(ui.n150.className)" :style="ui.n150.style" class="original-icon" :src="iconSrc('n150')" style="width:12px;height:12px" mode="aspectFit" />
<span v-if="!ui.n151.removed" :class="classes(ui.n151.className)" :style="ui.n151.style" class="original-span">
<template v-if="ui.n151.textContent !== undefined"><text>{{ ui.n151.textContent }}</text></template><template v-else>
<text>版本记录（25条）</text>
</template>
</span>
</template>
</view>
<view v-if="!ui.n152.removed" :class="classes(ui.n152.className)" :style="ui.n152.style" class="original-div">
<template v-if="ui.n152.textContent !== undefined"><text>{{ ui.n152.textContent }}</text></template><template v-else>
<image v-if="!ui.n153.removed" :class="classes(ui.n153.className)" :style="ui.n153.style" class="original-icon" :src="iconSrc('n153')" style="width:12px;height:12px" mode="aspectFit" />
<span v-if="!ui.n154.removed" :class="classes(ui.n154.className)" :style="ui.n154.style" class="original-span">
<template v-if="ui.n154.textContent !== undefined"><text>{{ ui.n154.textContent }}</text></template><template v-else>
<text>研讨记录（4条）</text>
</template>
</span>
</template>
</view>
<view v-if="!ui.n155.removed" :class="classes(ui.n155.className)" :style="ui.n155.style" class="original-div">
<template v-if="ui.n155.textContent !== undefined"><text>{{ ui.n155.textContent }}</text></template><template v-else>
<image v-if="!ui.n156.removed" :class="classes(ui.n156.className)" :style="ui.n156.style" class="original-icon" :src="iconSrc('n156')" style="width:12px;height:12px" mode="aspectFit" />
<span v-if="!ui.n157.removed" :class="classes(ui.n157.className)" :style="ui.n157.style" class="original-span">
<template v-if="ui.n157.textContent !== undefined"><text>{{ ui.n157.textContent }}</text></template><template v-else>
<text>分工记录</text>
</template>
</span>
</template>
</view>
<view v-if="!ui.n158.removed" :class="classes(ui.n158.className)" :style="ui.n158.style" class="original-div">
<template v-if="ui.n158.textContent !== undefined"><text>{{ ui.n158.textContent }}</text></template><template v-else>
<image v-if="!ui.n159.removed" :class="classes(ui.n159.className)" :style="ui.n159.style" class="original-icon" :src="iconSrc('n159')" style="width:12px;height:12px" mode="aspectFit" />
<span v-if="!ui.n160.removed" :class="classes(ui.n160.className)" :style="ui.n160.style" class="original-span">
<template v-if="ui.n160.textContent !== undefined"><text>{{ ui.n160.textContent }}</text></template><template v-else>
<text>教师评价（1条）</text>
</template>
</span>
</template>
</view>
</template>
</view>
</template>
</view>
<button v-if="!ui.n161.removed" :class="classes(ui.n161.className)" :style="ui.n161.style" class="original-button" @tap.stop="tap('n161', $event)">
<template v-if="ui.n161.textContent !== undefined"><text>{{ ui.n161.textContent }}</text></template><template v-else>
<text>确认归档（2项）</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n162.removed" :class="classes(ui.n162.className)" :style="ui.n162.style" class="original-div">
<template v-if="ui.n162.textContent !== undefined"><text>{{ ui.n162.textContent }}</text></template><template v-else>

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
import createController from '../../controllers/profile.js'
const controller = createController()
const { ui, classes, tap, iconSrc } = controller
provide('pageController', controller)
onUnload(controller.dispose)
</script>
