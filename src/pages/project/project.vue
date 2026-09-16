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
<view v-if="!ui.n6.removed" :class="classes(ui.n6.className)" :style="ui.n6.style" class="original-h1">
<template v-if="ui.n6.textContent !== undefined"><text>{{ ui.n6.textContent }}</text></template><template v-else>
<text>社区调研报告</text>
</template>
</view>
<view v-if="!ui.n7.removed" :class="classes(ui.n7.className)" :style="ui.n7.style" class="original-p">
<template v-if="ui.n7.textContent !== undefined"><text>{{ ui.n7.textContent }}</text></template><template v-else>
<text>软件工程实践 · 第 2 阶段</text>
</template>
</view>
</template>
</view>
<button v-if="!ui.n8.removed" :class="classes(ui.n8.className)" :style="ui.n8.style" class="original-button" @tap.stop="tap('n8', $event)">
<template v-if="ui.n8.textContent !== undefined"><text>{{ ui.n8.textContent }}</text></template><template v-else>
<image v-if="!ui.n9.removed" :class="classes(ui.n9.className)" :style="ui.n9.style" class="original-icon" :src="iconSrc('n9')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n10.removed" :class="classes(ui.n10.className)" :style="ui.n10.style" class="original-div">
<template v-if="ui.n10.textContent !== undefined"><text>{{ ui.n10.textContent }}</text></template><template v-else>
<view v-if="!ui.n11.removed" :class="classes(ui.n11.className)" :style="ui.n11.style" class="original-div">
<template v-if="ui.n11.textContent !== undefined"><text>{{ ui.n11.textContent }}</text></template><template v-else>
<view v-if="!ui.n12.removed" :class="classes(ui.n12.className)" :style="ui.n12.style" class="original-span">
<template v-if="ui.n12.textContent !== undefined"><text>{{ ui.n12.textContent }}</text></template><template v-else>
<text>总体进度</text>
</template>
</view>
<view v-if="!ui.n13.removed" :class="classes(ui.n13.className)" :style="ui.n13.style" class="original-span">
<template v-if="ui.n13.textContent !== undefined"><text>{{ ui.n13.textContent }}</text></template><template v-else>
<text>68%</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n14.removed" :class="classes(ui.n14.className)" :style="ui.n14.style" class="original-div">
<template v-if="ui.n14.textContent !== undefined"><text>{{ ui.n14.textContent }}</text></template><template v-else>
<view v-if="!ui.n15.removed" :class="classes(ui.n15.className)" :style="ui.n15.style" class="original-div">
<template v-if="ui.n15.textContent !== undefined"><text>{{ ui.n15.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n16.removed" :class="classes(ui.n16.className)" :style="ui.n16.style" class="original-div">
<template v-if="ui.n16.textContent !== undefined"><text>{{ ui.n16.textContent }}</text></template><template v-else>
<view v-if="!ui.n17.removed" :class="classes(ui.n17.className)" :style="ui.n17.style" class="original-div">
<template v-if="ui.n17.textContent !== undefined"><text>{{ ui.n17.textContent }}</text></template><template v-else>
<view v-if="!ui.n18.removed" :class="classes(ui.n18.className)" :style="ui.n18.style" class="original-span">
<template v-if="ui.n18.textContent !== undefined"><text>{{ ui.n18.textContent }}</text></template><template v-else>
<text>林</text>
</template>
</view>
<view v-if="!ui.n19.removed" :class="classes(ui.n19.className)" :style="ui.n19.style" class="original-span">
<template v-if="ui.n19.textContent !== undefined"><text>{{ ui.n19.textContent }}</text></template><template v-else>
<text>许</text>
</template>
</view>
<view v-if="!ui.n20.removed" :class="classes(ui.n20.className)" :style="ui.n20.style" class="original-span">
<template v-if="ui.n20.textContent !== undefined"><text>{{ ui.n20.textContent }}</text></template><template v-else>
<text>王</text>
</template>
</view>
<view v-if="!ui.n21.removed" :class="classes(ui.n21.className)" :style="ui.n21.style" class="original-span">
<template v-if="ui.n21.textContent !== undefined"><text>{{ ui.n21.textContent }}</text></template><template v-else>
<text>+1</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n22.removed" :class="classes(ui.n22.className)" :style="ui.n22.style" class="original-div">
<template v-if="ui.n22.textContent !== undefined"><text>{{ ui.n22.textContent }}</text></template><template v-else>
<view v-if="!ui.n23.removed" :class="classes(ui.n23.className)" :style="ui.n23.style" class="original-span">
<template v-if="ui.n23.textContent !== undefined"><text>{{ ui.n23.textContent }}</text></template><template v-else>
<text>12资料</text>
</template>
</view>
<view v-if="!ui.n24.removed" :class="classes(ui.n24.className)" :style="ui.n24.style" class="original-span">
<template v-if="ui.n24.textContent !== undefined"><text>{{ ui.n24.textContent }}</text></template><template v-else>
<text>4待审</text>
</template>
</view>
<view v-if="!ui.n25.removed" :class="classes(ui.n25.className)" :style="ui.n25.style" class="original-span">
<template v-if="ui.n25.textContent !== undefined"><text>{{ ui.n25.textContent }}</text></template><template v-else>
<text>18版本</text>
</template>
</view>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n26.removed" :class="classes(ui.n26.className)" :style="ui.n26.style" class="original-div">
<template v-if="ui.n26.textContent !== undefined"><text>{{ ui.n26.textContent }}</text></template><template v-else>
<button v-if="!ui.n27.removed" :class="classes(ui.n27.className)" :style="ui.n27.style" class="original-button" @tap.stop="tap('n27', $event)">
<template v-if="ui.n27.textContent !== undefined"><text>{{ ui.n27.textContent }}</text></template><template v-else>
<text>文件</text>
</template>
</button>
<button v-if="!ui.n28.removed" :class="classes(ui.n28.className)" :style="ui.n28.style" class="original-button" @tap.stop="tap('n28', $event)">
<template v-if="ui.n28.textContent !== undefined"><text>{{ ui.n28.textContent }}</text></template><template v-else>
<text>阶段</text>
</template>
</button>
<button v-if="!ui.n29.removed" :class="classes(ui.n29.className)" :style="ui.n29.style" class="original-button" @tap.stop="tap('n29', $event)">
<template v-if="ui.n29.textContent !== undefined"><text>{{ ui.n29.textContent }}</text></template><template v-else>
<text>成员</text>
</template>
</button>
<button v-if="!ui.n30.removed" :class="classes(ui.n30.className)" :style="ui.n30.style" class="original-button" @tap.stop="tap('n30', $event)">
<template v-if="ui.n30.textContent !== undefined"><text>{{ ui.n30.textContent }}</text></template><template v-else>
<text>溯源</text>
</template>
</button>
</template>
</view>
<scroll-view v-if="!ui.n31.removed" :class="classes(ui.n31.className)" :style="ui.n31.style" class="original-scroll" scroll-y :show-scrollbar="false">
<template v-if="ui.n31.textContent !== undefined"><text>{{ ui.n31.textContent }}</text></template><template v-else>
<view v-if="!ui.n32.removed" :class="classes(ui.n32.className)" :style="ui.n32.style" class="original-div">
<template v-if="ui.n32.textContent !== undefined"><text>{{ ui.n32.textContent }}</text></template><template v-else>
<button v-if="!ui.n33.removed" :class="classes(ui.n33.className)" :style="ui.n33.style" class="original-button" @tap.stop="tap('n33', $event)">
<template v-if="ui.n33.textContent !== undefined"><text>{{ ui.n33.textContent }}</text></template><template v-else>
<image v-if="!ui.n34.removed" :class="classes(ui.n34.className)" :style="ui.n34.style" class="original-icon" :src="iconSrc('n34')" style="width:18px;height:18px" mode="aspectFit" />
<text>上传资料 </text>
</template>
</button>
<view v-if="!ui.n35.removed" :class="classes(ui.n35.className)" :style="ui.n35.style" class="original-div">
<template v-if="ui.n35.textContent !== undefined"><text>{{ ui.n35.textContent }}</text></template><template v-else>
<button v-if="!ui.n36.removed" :class="classes(ui.n36.className)" :style="ui.n36.style" class="original-button" @tap.stop="tap('n36', $event)">
<template v-if="ui.n36.textContent !== undefined"><text>{{ ui.n36.textContent }}</text></template><template v-else>
<image v-if="!ui.n37.removed" :class="classes(ui.n37.className)" :style="ui.n37.style" class="original-icon" :src="iconSrc('n37')" style="width:15px;height:15px" mode="aspectFit" />
<text>关键词搜索… </text>
</template>
</button>
<button v-if="!ui.n38.removed" :class="classes(ui.n38.className)" :style="ui.n38.style" class="original-button" @tap.stop="tap('n38', $event)">
<template v-if="ui.n38.textContent !== undefined"><text>{{ ui.n38.textContent }}</text></template><template v-else>
<image v-if="!ui.n39.removed" :class="classes(ui.n39.className)" :style="ui.n39.style" class="original-icon" :src="iconSrc('n39')" style="width:16px;height:16px" mode="aspectFit" />
<view v-if="!ui.n40.removed" :class="classes(ui.n40.className)" :style="ui.n40.style" class="original-span">
<template v-if="ui.n40.textContent !== undefined"><text>{{ ui.n40.textContent }}</text></template><template v-else>
<text>3</text>
</template>
</view>
</template>
</button>
<button v-if="!ui.n41.removed" :class="classes(ui.n41.className)" :style="ui.n41.style" class="original-button" @tap.stop="tap('n41', $event)">
<template v-if="ui.n41.textContent !== undefined"><text>{{ ui.n41.textContent }}</text></template><template v-else>
<image v-if="!ui.n42.removed" :class="classes(ui.n42.className)" :style="ui.n42.style" class="original-icon" :src="iconSrc('n42')" style="width:16px;height:16px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n43.removed" :class="classes(ui.n43.className)" :style="ui.n43.style" class="original-div">
<template v-if="ui.n43.textContent !== undefined"><text>{{ ui.n43.textContent }}</text></template><template v-else>
<view v-if="!ui.n44.removed" :class="classes(ui.n44.className)" :style="ui.n44.style" class="original-span">
<template v-if="ui.n44.textContent !== undefined"><text>{{ ui.n44.textContent }}</text></template><template v-else>
<text>×全部阶段 </text>
<button v-if="!ui.n45.removed" :class="classes(ui.n45.className)" :style="ui.n45.style" class="original-button" @tap.stop="tap('n45', $event)">
<template v-if="ui.n45.textContent !== undefined"><text>{{ ui.n45.textContent }}</text></template><template v-else>
<image v-if="!ui.n46.removed" :class="classes(ui.n46.className)" :style="ui.n46.style" class="original-icon" :src="iconSrc('n46')" style="width:10px;height:10px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n47.removed" :class="classes(ui.n47.className)" :style="ui.n47.style" class="original-span">
<template v-if="ui.n47.textContent !== undefined"><text>{{ ui.n47.textContent }}</text></template><template v-else>
<text>×.docx </text>
<button v-if="!ui.n48.removed" :class="classes(ui.n48.className)" :style="ui.n48.style" class="original-button" @tap.stop="tap('n48', $event)">
<template v-if="ui.n48.textContent !== undefined"><text>{{ ui.n48.textContent }}</text></template><template v-else>
<image v-if="!ui.n49.removed" :class="classes(ui.n49.className)" :style="ui.n49.style" class="original-icon" :src="iconSrc('n49')" style="width:10px;height:10px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n50.removed" :class="classes(ui.n50.className)" :style="ui.n50.style" class="original-span">
<template v-if="ui.n50.textContent !== undefined"><text>{{ ui.n50.textContent }}</text></template><template v-else>
<text>×未隐藏 </text>
<button v-if="!ui.n51.removed" :class="classes(ui.n51.className)" :style="ui.n51.style" class="original-button" @tap.stop="tap('n51', $event)">
<template v-if="ui.n51.textContent !== undefined"><text>{{ ui.n51.textContent }}</text></template><template v-else>
<image v-if="!ui.n52.removed" :class="classes(ui.n52.className)" :style="ui.n52.style" class="original-icon" :src="iconSrc('n52')" style="width:10px;height:10px" mode="aspectFit" />
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n53.removed" :class="classes(ui.n53.className)" :style="ui.n53.style" class="original-div">
<template v-if="ui.n53.textContent !== undefined"><text>{{ ui.n53.textContent }}</text></template><template v-else>
<view v-if="!ui.n54.removed" :class="classes(ui.n54.className)" :style="ui.n54.style" class="original-span">
<template v-if="ui.n54.textContent !== undefined"><text>{{ ui.n54.textContent }}</text></template><template v-else>
<view v-if="!ui.n55.removed" :class="classes(ui.n55.className)" :style="ui.n55.style" class="original-span">
<template v-if="ui.n55.textContent !== undefined"><text>{{ ui.n55.textContent }}</text></template><template v-else>
<text>0</text>
</template>
</view>
<text> 项已选</text>
</template>
</view>
<view v-if="!ui.n56.removed" :class="classes(ui.n56.className)" :style="ui.n56.style" class="original-div">
<template v-if="ui.n56.textContent !== undefined"><text>{{ ui.n56.textContent }}</text></template><template v-else>
<button v-if="!ui.n57.removed" :class="classes(ui.n57.className)" :style="ui.n57.style" class="original-button" @tap.stop="tap('n57', $event)">
<template v-if="ui.n57.textContent !== undefined"><text>{{ ui.n57.textContent }}</text></template><template v-else>
<text>归档隐藏</text>
</template>
</button>
<button v-if="!ui.n58.removed" :class="classes(ui.n58.className)" :style="ui.n58.style" class="original-button" @tap.stop="tap('n58', $event)">
<template v-if="ui.n58.textContent !== undefined"><text>{{ ui.n58.textContent }}</text></template><template v-else>
<text>批量清理</text>
</template>
</button>
<button v-if="!ui.n59.removed" :class="classes(ui.n59.className)" :style="ui.n59.style" class="original-button" @tap.stop="tap('n59', $event)">
<template v-if="ui.n59.textContent !== undefined"><text>{{ ui.n59.textContent }}</text></template><template v-else>
<text>取消</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n60.removed" :class="classes(ui.n60.className)" :style="ui.n60.style" class="original-div">
<template v-if="ui.n60.textContent !== undefined"><text>{{ ui.n60.textContent }}</text></template><template v-else>
<view v-if="!ui.n61.removed" :class="classes(ui.n61.className)" :style="ui.n61.style" class="original-div">
<template v-if="ui.n61.textContent !== undefined"><text>{{ ui.n61.textContent }}</text></template><template v-else>
<view v-if="!ui.n62.removed" :class="classes(ui.n62.className)" :style="ui.n62.style" class="original-div">
<template v-if="ui.n62.textContent !== undefined"><text>{{ ui.n62.textContent }}</text></template><template v-else>
<view v-if="!ui.n63.removed" :class="classes(ui.n63.className)" :style="ui.n63.style" class="original-label" @tap.stop="tap('n63', $event)">
<template v-if="ui.n63.textContent !== undefined"><text>{{ ui.n63.textContent }}</text></template><template v-else>
<image v-if="!ui.n64.removed" :class="classes(ui.n64.className)" :style="ui.n64.style" class="original-icon" :src="iconSrc('n64')" style="width:12px;height:12px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n65.removed" :class="classes(ui.n65.className)" :style="ui.n65.style" class="original-div">
<template v-if="ui.n65.textContent !== undefined"><text>{{ ui.n65.textContent }}</text></template><template v-else>
<image v-if="!ui.n66.removed" :class="classes(ui.n66.className)" :style="ui.n66.style" class="original-icon" :src="iconSrc('n66')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n67.removed" :class="classes(ui.n67.className)" :style="ui.n67.style" class="original-div" @tap.stop="tap('n67', $event)">
<template v-if="ui.n67.textContent !== undefined"><text>{{ ui.n67.textContent }}</text></template><template v-else>
<view v-if="!ui.n68.removed" :class="classes(ui.n68.className)" :style="ui.n68.style" class="original-p">
<template v-if="ui.n68.textContent !== undefined"><text>{{ ui.n68.textContent }}</text></template><template v-else>
<text>社区访谈整理.docx</text>
</template>
</view>
<view v-if="!ui.n69.removed" :class="classes(ui.n69.className)" :style="ui.n69.style" class="original-p">
<template v-if="ui.n69.textContent !== undefined"><text>{{ ui.n69.textContent }}</text></template><template v-else>
<text>许萌 · v2 · 18 KB · 资料归集</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n70.removed" :class="classes(ui.n70.className)" :style="ui.n70.style" class="original-span">
<template v-if="ui.n70.textContent !== undefined"><text>{{ ui.n70.textContent }}</text></template><template v-else>
<text>已入库</text>
</template>
</view>
<view v-if="!ui.n71.removed" :class="classes(ui.n71.className)" :style="ui.n71.style" class="original-div">
<template v-if="ui.n71.textContent !== undefined"><text>{{ ui.n71.textContent }}</text></template><template v-else>
<button v-if="!ui.n72.removed" :class="classes(ui.n72.className)" :style="ui.n72.style" class="original-button" @tap.stop="tap('n72', $event)">
<template v-if="ui.n72.textContent !== undefined"><text>{{ ui.n72.textContent }}</text></template><template v-else>
<image v-if="!ui.n73.removed" :class="classes(ui.n73.className)" :style="ui.n73.style" class="original-icon" :src="iconSrc('n73')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n74.removed" :class="classes(ui.n74.className)" :style="ui.n74.style" class="original-button" @tap.stop="tap('n74', $event)">
<template v-if="ui.n74.textContent !== undefined"><text>{{ ui.n74.textContent }}</text></template><template v-else>
<image v-if="!ui.n75.removed" :class="classes(ui.n75.className)" :style="ui.n75.style" class="original-icon" :src="iconSrc('n75')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n76.removed" :class="classes(ui.n76.className)" :style="ui.n76.style" class="original-button" @tap.stop="tap('n76', $event)">
<template v-if="ui.n76.textContent !== undefined"><text>{{ ui.n76.textContent }}</text></template><template v-else>
<image v-if="!ui.n77.removed" :class="classes(ui.n77.className)" :style="ui.n77.style" class="original-icon" :src="iconSrc('n77')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n78.removed" :class="classes(ui.n78.className)" :style="ui.n78.style" class="original-div">
<template v-if="ui.n78.textContent !== undefined"><text>{{ ui.n78.textContent }}</text></template><template v-else>
<view v-if="!ui.n79.removed" :class="classes(ui.n79.className)" :style="ui.n79.style" class="original-div">
<template v-if="ui.n79.textContent !== undefined"><text>{{ ui.n79.textContent }}</text></template><template v-else>
<view v-if="!ui.n80.removed" :class="classes(ui.n80.className)" :style="ui.n80.style" class="original-label" @tap.stop="tap('n80', $event)">
<template v-if="ui.n80.textContent !== undefined"><text>{{ ui.n80.textContent }}</text></template><template v-else>
<image v-if="!ui.n81.removed" :class="classes(ui.n81.className)" :style="ui.n81.style" class="original-icon" :src="iconSrc('n81')" style="width:12px;height:12px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n82.removed" :class="classes(ui.n82.className)" :style="ui.n82.style" class="original-div">
<template v-if="ui.n82.textContent !== undefined"><text>{{ ui.n82.textContent }}</text></template><template v-else>
<image v-if="!ui.n83.removed" :class="classes(ui.n83.className)" :style="ui.n83.style" class="original-icon" :src="iconSrc('n83')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n84.removed" :class="classes(ui.n84.className)" :style="ui.n84.style" class="original-div" @tap.stop="tap('n84', $event)">
<template v-if="ui.n84.textContent !== undefined"><text>{{ ui.n84.textContent }}</text></template><template v-else>
<view v-if="!ui.n85.removed" :class="classes(ui.n85.className)" :style="ui.n85.style" class="original-p">
<template v-if="ui.n85.textContent !== undefined"><text>{{ ui.n85.textContent }}</text></template><template v-else>
<text>文献综述参考资料.pdf</text>
</template>
</view>
<view v-if="!ui.n86.removed" :class="classes(ui.n86.className)" :style="ui.n86.style" class="original-p">
<template v-if="ui.n86.textContent !== undefined"><text>{{ ui.n86.textContent }}</text></template><template v-else>
<text>王宁 · v1 · 256 KB · 选题与分工</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n87.removed" :class="classes(ui.n87.className)" :style="ui.n87.style" class="original-span">
<template v-if="ui.n87.textContent !== undefined"><text>{{ ui.n87.textContent }}</text></template><template v-else>
<text>闲置</text>
</template>
</view>
<view v-if="!ui.n88.removed" :class="classes(ui.n88.className)" :style="ui.n88.style" class="original-div">
<template v-if="ui.n88.textContent !== undefined"><text>{{ ui.n88.textContent }}</text></template><template v-else>
<button v-if="!ui.n89.removed" :class="classes(ui.n89.className)" :style="ui.n89.style" class="original-button" @tap.stop="tap('n89', $event)">
<template v-if="ui.n89.textContent !== undefined"><text>{{ ui.n89.textContent }}</text></template><template v-else>
<image v-if="!ui.n90.removed" :class="classes(ui.n90.className)" :style="ui.n90.style" class="original-icon" :src="iconSrc('n90')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n91.removed" :class="classes(ui.n91.className)" :style="ui.n91.style" class="original-button" @tap.stop="tap('n91', $event)">
<template v-if="ui.n91.textContent !== undefined"><text>{{ ui.n91.textContent }}</text></template><template v-else>
<image v-if="!ui.n92.removed" :class="classes(ui.n92.className)" :style="ui.n92.style" class="original-icon" :src="iconSrc('n92')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n93.removed" :class="classes(ui.n93.className)" :style="ui.n93.style" class="original-button" @tap.stop="tap('n93', $event)">
<template v-if="ui.n93.textContent !== undefined"><text>{{ ui.n93.textContent }}</text></template><template v-else>
<image v-if="!ui.n94.removed" :class="classes(ui.n94.className)" :style="ui.n94.style" class="original-icon" :src="iconSrc('n94')" style="width:14px;height:14px" mode="aspectFit" />
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
<view v-if="!ui.n96.removed" :class="classes(ui.n96.className)" :style="ui.n96.style" class="original-div">
<template v-if="ui.n96.textContent !== undefined"><text>{{ ui.n96.textContent }}</text></template><template v-else>
<view v-if="!ui.n97.removed" :class="classes(ui.n97.className)" :style="ui.n97.style" class="original-label" @tap.stop="tap('n97', $event)">
<template v-if="ui.n97.textContent !== undefined"><text>{{ ui.n97.textContent }}</text></template><template v-else>
<image v-if="!ui.n98.removed" :class="classes(ui.n98.className)" :style="ui.n98.style" class="original-icon" :src="iconSrc('n98')" style="width:12px;height:12px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n99.removed" :class="classes(ui.n99.className)" :style="ui.n99.style" class="original-div">
<template v-if="ui.n99.textContent !== undefined"><text>{{ ui.n99.textContent }}</text></template><template v-else>
<image v-if="!ui.n100.removed" :class="classes(ui.n100.className)" :style="ui.n100.style" class="original-icon" :src="iconSrc('n100')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n101.removed" :class="classes(ui.n101.className)" :style="ui.n101.style" class="original-div" @tap.stop="tap('n101', $event)">
<template v-if="ui.n101.textContent !== undefined"><text>{{ ui.n101.textContent }}</text></template><template v-else>
<view v-if="!ui.n102.removed" :class="classes(ui.n102.className)" :style="ui.n102.style" class="original-p">
<template v-if="ui.n102.textContent !== undefined"><text>{{ ui.n102.textContent }}</text></template><template v-else>
<text>访谈录音转写稿.docx</text>
</template>
</view>
<view v-if="!ui.n103.removed" :class="classes(ui.n103.className)" :style="ui.n103.style" class="original-p">
<template v-if="ui.n103.textContent !== undefined"><text>{{ ui.n103.textContent }}</text></template><template v-else>
<text>许萌 · 今天 09:42 · 资料归集</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n104.removed" :class="classes(ui.n104.className)" :style="ui.n104.style" class="original-span">
<template v-if="ui.n104.textContent !== undefined"><text>{{ ui.n104.textContent }}</text></template><template v-else>
<text>待审核</text>
</template>
</view>
<view v-if="!ui.n105.removed" :class="classes(ui.n105.className)" :style="ui.n105.style" class="original-div">
<template v-if="ui.n105.textContent !== undefined"><text>{{ ui.n105.textContent }}</text></template><template v-else>
<button v-if="!ui.n106.removed" :class="classes(ui.n106.className)" :style="ui.n106.style" class="original-button" @tap.stop="tap('n106', $event)">
<template v-if="ui.n106.textContent !== undefined"><text>{{ ui.n106.textContent }}</text></template><template v-else>
<image v-if="!ui.n107.removed" :class="classes(ui.n107.className)" :style="ui.n107.style" class="original-icon" :src="iconSrc('n107')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n108.removed" :class="classes(ui.n108.className)" :style="ui.n108.style" class="original-button" @tap.stop="tap('n108', $event)">
<template v-if="ui.n108.textContent !== undefined"><text>{{ ui.n108.textContent }}</text></template><template v-else>
<image v-if="!ui.n109.removed" :class="classes(ui.n109.className)" :style="ui.n109.style" class="original-icon" :src="iconSrc('n109')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n110.removed" :class="classes(ui.n110.className)" :style="ui.n110.style" class="original-button" @tap.stop="tap('n110', $event)">
<template v-if="ui.n110.textContent !== undefined"><text>{{ ui.n110.textContent }}</text></template><template v-else>
<image v-if="!ui.n111.removed" :class="classes(ui.n111.className)" :style="ui.n111.style" class="original-icon" :src="iconSrc('n111')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n112.removed" :class="classes(ui.n112.className)" :style="ui.n112.style" class="original-div">
<template v-if="ui.n112.textContent !== undefined"><text>{{ ui.n112.textContent }}</text></template><template v-else>
<view v-if="!ui.n113.removed" :class="classes(ui.n113.className)" :style="ui.n113.style" class="original-div">
<template v-if="ui.n113.textContent !== undefined"><text>{{ ui.n113.textContent }}</text></template><template v-else>
<view v-if="!ui.n114.removed" :class="classes(ui.n114.className)" :style="ui.n114.style" class="original-label" @tap.stop="tap('n114', $event)">
<template v-if="ui.n114.textContent !== undefined"><text>{{ ui.n114.textContent }}</text></template><template v-else>
<image v-if="!ui.n115.removed" :class="classes(ui.n115.className)" :style="ui.n115.style" class="original-icon" :src="iconSrc('n115')" style="width:12px;height:12px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n116.removed" :class="classes(ui.n116.className)" :style="ui.n116.style" class="original-div">
<template v-if="ui.n116.textContent !== undefined"><text>{{ ui.n116.textContent }}</text></template><template v-else>
<image v-if="!ui.n117.removed" :class="classes(ui.n117.className)" :style="ui.n117.style" class="original-icon" :src="iconSrc('n117')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n118.removed" :class="classes(ui.n118.className)" :style="ui.n118.style" class="original-div" @tap.stop="tap('n118', $event)">
<template v-if="ui.n118.textContent !== undefined"><text>{{ ui.n118.textContent }}</text></template><template v-else>
<view v-if="!ui.n119.removed" :class="classes(ui.n119.className)" :style="ui.n119.style" class="original-p">
<template v-if="ui.n119.textContent !== undefined"><text>{{ ui.n119.textContent }}</text></template><template v-else>
<text>调研问卷原始数据.xlsx</text>
</template>
</view>
<view v-if="!ui.n120.removed" :class="classes(ui.n120.className)" :style="ui.n120.style" class="original-p">
<template v-if="ui.n120.textContent !== undefined"><text>{{ ui.n120.textContent }}</text></template><template v-else>
<text>林知行 · 草稿暂存</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n121.removed" :class="classes(ui.n121.className)" :style="ui.n121.style" class="original-span">
<template v-if="ui.n121.textContent !== undefined"><text>{{ ui.n121.textContent }}</text></template><template v-else>
<text>草稿</text>
</template>
</view>
<button v-if="!ui.n122.removed" :class="classes(ui.n122.className)" :style="ui.n122.style" class="original-button" @tap.stop="tap('n122', $event)">
<template v-if="ui.n122.textContent !== undefined"><text>{{ ui.n122.textContent }}</text></template><template v-else>
<image v-if="!ui.n123.removed" :class="classes(ui.n123.className)" :style="ui.n123.style" class="original-icon" :src="iconSrc('n123')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n124.removed" :class="classes(ui.n124.className)" :style="ui.n124.style" class="original-div">
<template v-if="ui.n124.textContent !== undefined"><text>{{ ui.n124.textContent }}</text></template><template v-else>
<view v-if="!ui.n125.removed" :class="classes(ui.n125.className)" :style="ui.n125.style" class="original-div">
<template v-if="ui.n125.textContent !== undefined"><text>{{ ui.n125.textContent }}</text></template><template v-else>
<view v-if="!ui.n126.removed" :class="classes(ui.n126.className)" :style="ui.n126.style" class="original-div">
<template v-if="ui.n126.textContent !== undefined"><text>{{ ui.n126.textContent }}</text></template><template v-else>
<image v-if="!ui.n127.removed" :class="classes(ui.n127.className)" :style="ui.n127.style" class="original-icon" :src="iconSrc('n127')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n128.removed" :class="classes(ui.n128.className)" :style="ui.n128.style" class="original-div">
<template v-if="ui.n128.textContent !== undefined"><text>{{ ui.n128.textContent }}</text></template><template v-else>
<view v-if="!ui.n129.removed" :class="classes(ui.n129.className)" :style="ui.n129.style" class="original-p">
<template v-if="ui.n129.textContent !== undefined"><text>{{ ui.n129.textContent }}</text></template><template v-else>
<text>文献综述参考资料.pdf</text>
</template>
</view>
<view v-if="!ui.n130.removed" :class="classes(ui.n130.className)" :style="ui.n130.style" class="original-p">
<template v-if="ui.n130.textContent !== undefined"><text>{{ ui.n130.textContent }}</text></template><template v-else>
<text>王宁 · v1 · 256 KB</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n131.removed" :class="classes(ui.n131.className)" :style="ui.n131.style" class="original-span">
<template v-if="ui.n131.textContent !== undefined"><text>{{ ui.n131.textContent }}</text></template><template v-else>
<text>已入库</text>
</template>
</view>
<view v-if="!ui.n132.removed" :class="classes(ui.n132.className)" :style="ui.n132.style" class="original-div">
<template v-if="ui.n132.textContent !== undefined"><text>{{ ui.n132.textContent }}</text></template><template v-else>
<button v-if="!ui.n133.removed" :class="classes(ui.n133.className)" :style="ui.n133.style" class="original-button" @tap.stop="tap('n133', $event)">
<template v-if="ui.n133.textContent !== undefined"><text>{{ ui.n133.textContent }}</text></template><template v-else>
<image v-if="!ui.n134.removed" :class="classes(ui.n134.className)" :style="ui.n134.style" class="original-icon" :src="iconSrc('n134')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n135.removed" :class="classes(ui.n135.className)" :style="ui.n135.style" class="original-button" @tap.stop="tap('n135', $event)">
<template v-if="ui.n135.textContent !== undefined"><text>{{ ui.n135.textContent }}</text></template><template v-else>
<image v-if="!ui.n136.removed" :class="classes(ui.n136.className)" :style="ui.n136.style" class="original-icon" :src="iconSrc('n136')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n137.removed" :class="classes(ui.n137.className)" :style="ui.n137.style" class="original-div">
<template v-if="ui.n137.textContent !== undefined"><text>{{ ui.n137.textContent }}</text></template><template v-else>
<view v-if="!ui.n138.removed" :class="classes(ui.n138.className)" :style="ui.n138.style" class="original-div">
<template v-if="ui.n138.textContent !== undefined"><text>{{ ui.n138.textContent }}</text></template><template v-else>
<view v-if="!ui.n139.removed" :class="classes(ui.n139.className)" :style="ui.n139.style" class="original-label" @tap.stop="tap('n139', $event)">
<template v-if="ui.n139.textContent !== undefined"><text>{{ ui.n139.textContent }}</text></template><template v-else>
<image v-if="!ui.n140.removed" :class="classes(ui.n140.className)" :style="ui.n140.style" class="original-icon" :src="iconSrc('n140')" style="width:12px;height:12px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n141.removed" :class="classes(ui.n141.className)" :style="ui.n141.style" class="original-div">
<template v-if="ui.n141.textContent !== undefined"><text>{{ ui.n141.textContent }}</text></template><template v-else>
<image v-if="!ui.n142.removed" :class="classes(ui.n142.className)" :style="ui.n142.style" class="original-icon" :src="iconSrc('n142')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n143.removed" :class="classes(ui.n143.className)" :style="ui.n143.style" class="original-div" @tap.stop="tap('n143', $event)">
<template v-if="ui.n143.textContent !== undefined"><text>{{ ui.n143.textContent }}</text></template><template v-else>
<view v-if="!ui.n144.removed" :class="classes(ui.n144.className)" :style="ui.n144.style" class="original-p">
<template v-if="ui.n144.textContent !== undefined"><text>{{ ui.n144.textContent }}</text></template><template v-else>
<text>调研报告大纲.docx</text>
</template>
</view>
<view v-if="!ui.n145.removed" :class="classes(ui.n145.className)" :style="ui.n145.style" class="original-p">
<template v-if="ui.n145.textContent !== undefined"><text>{{ ui.n145.textContent }}</text></template><template v-else>
<text>王宁 · v4 · 资料归集</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n146.removed" :class="classes(ui.n146.className)" :style="ui.n146.style" class="original-span">
<template v-if="ui.n146.textContent !== undefined"><text>{{ ui.n146.textContent }}</text></template><template v-else>
<text>待审核</text>
</template>
</view>
<view v-if="!ui.n147.removed" :class="classes(ui.n147.className)" :style="ui.n147.style" class="original-div">
<template v-if="ui.n147.textContent !== undefined"><text>{{ ui.n147.textContent }}</text></template><template v-else>
<button v-if="!ui.n148.removed" :class="classes(ui.n148.className)" :style="ui.n148.style" class="original-button" @tap.stop="tap('n148', $event)">
<template v-if="ui.n148.textContent !== undefined"><text>{{ ui.n148.textContent }}</text></template><template v-else>
<image v-if="!ui.n149.removed" :class="classes(ui.n149.className)" :style="ui.n149.style" class="original-icon" :src="iconSrc('n149')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n150.removed" :class="classes(ui.n150.className)" :style="ui.n150.style" class="original-button" @tap.stop="tap('n150', $event)">
<template v-if="ui.n150.textContent !== undefined"><text>{{ ui.n150.textContent }}</text></template><template v-else>
<image v-if="!ui.n151.removed" :class="classes(ui.n151.className)" :style="ui.n151.style" class="original-icon" :src="iconSrc('n151')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n152.removed" :class="classes(ui.n152.className)" :style="ui.n152.style" class="original-button" @tap.stop="tap('n152', $event)">
<template v-if="ui.n152.textContent !== undefined"><text>{{ ui.n152.textContent }}</text></template><template v-else>
<image v-if="!ui.n153.removed" :class="classes(ui.n153.className)" :style="ui.n153.style" class="original-icon" :src="iconSrc('n153')" style="width:14px;height:14px" mode="aspectFit" />
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n154.removed" :class="classes(ui.n154.className)" :style="ui.n154.style" class="original-div">
<template v-if="ui.n154.textContent !== undefined"><text>{{ ui.n154.textContent }}</text></template><template v-else>
<button v-if="!ui.n155.removed" :class="classes(ui.n155.className)" :style="ui.n155.style" class="original-button" @tap.stop="tap('n155', $event)">
<template v-if="ui.n155.textContent !== undefined"><text>{{ ui.n155.textContent }}</text></template><template v-else>
<image v-if="!ui.n156.removed" :class="classes(ui.n156.className)" :style="ui.n156.style" class="original-icon" :src="iconSrc('n156')" style="width:12px;height:12px" mode="aspectFit" />
<view v-if="!ui.n157.removed" :class="classes(ui.n157.className)" :style="ui.n157.style" class="original-span">
<template v-if="ui.n157.textContent !== undefined"><text>{{ ui.n157.textContent }}</text></template><template v-else>
<text>查看已隐藏文件</text>
</template>
</view>
<view v-if="!ui.n158.removed" :class="classes(ui.n158.className)" :style="ui.n158.style" class="original-span">
<template v-if="ui.n158.textContent !== undefined"><text>{{ ui.n158.textContent }}</text></template><template v-else>
<text>（2）</text>
</template>
</view>
</template>
</button>
<view v-if="!ui.n159.removed" :class="classes(ui.n159.className)" :style="ui.n159.style" class="original-div">
<template v-if="ui.n159.textContent !== undefined"><text>{{ ui.n159.textContent }}</text></template><template v-else>
<view v-if="!ui.n160.removed" :class="classes(ui.n160.className)" :style="ui.n160.style" class="original-div">
<template v-if="ui.n160.textContent !== undefined"><text>{{ ui.n160.textContent }}</text></template><template v-else>
<view v-if="!ui.n161.removed" :class="classes(ui.n161.className)" :style="ui.n161.style" class="original-div">
<template v-if="ui.n161.textContent !== undefined"><text>{{ ui.n161.textContent }}</text></template><template v-else>
<image v-if="!ui.n162.removed" :class="classes(ui.n162.className)" :style="ui.n162.style" class="original-icon" :src="iconSrc('n162')" style="width:14px;height:14px" mode="aspectFit" />
<view v-if="!ui.n163.removed" :class="classes(ui.n163.className)" :style="ui.n163.style" class="original-div">
<template v-if="ui.n163.textContent !== undefined"><text>{{ ui.n163.textContent }}</text></template><template v-else>
<image v-if="!ui.n164.removed" :class="classes(ui.n164.className)" :style="ui.n164.style" class="original-icon" :src="iconSrc('n164')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n165.removed" :class="classes(ui.n165.className)" :style="ui.n165.style" class="original-div">
<template v-if="ui.n165.textContent !== undefined"><text>{{ ui.n165.textContent }}</text></template><template v-else>
<view v-if="!ui.n166.removed" :class="classes(ui.n166.className)" :style="ui.n166.style" class="original-p">
<template v-if="ui.n166.textContent !== undefined"><text>{{ ui.n166.textContent }}</text></template><template v-else>
<text>旧版访谈大纲.docx</text>
</template>
</view>
<view v-if="!ui.n167.removed" :class="classes(ui.n167.className)" :style="ui.n167.style" class="original-p">
<template v-if="ui.n167.textContent !== undefined"><text>{{ ui.n167.textContent }}</text></template><template v-else>
<text>已归档隐藏 · 闲置 &gt; 30 天</text>
</template>
</view>
</template>
</view>
<button v-if="!ui.n168.removed" :class="classes(ui.n168.className)" :style="ui.n168.style" class="original-button" @tap.stop="tap('n168', $event)">
<template v-if="ui.n168.textContent !== undefined"><text>{{ ui.n168.textContent }}</text></template><template v-else>
<text>恢复</text>
</template>
</button>
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
</view>
<view v-if="!ui.n169.removed" :class="classes(ui.n169.className)" :style="ui.n169.style" class="original-div">
<template v-if="ui.n169.textContent !== undefined"><text>{{ ui.n169.textContent }}</text></template><template v-else>
<view v-if="!ui.n170.removed" :class="classes(ui.n170.className)" :style="ui.n170.style" class="original-div">
<template v-if="ui.n170.textContent !== undefined"><text>{{ ui.n170.textContent }}</text></template><template v-else>
<view v-if="!ui.n171.removed" :class="classes(ui.n171.className)" :style="ui.n171.style" class="original-h2">
<template v-if="ui.n171.textContent !== undefined"><text>{{ ui.n171.textContent }}</text></template><template v-else>
<text>作业阶段</text>
</template>
</view>
<button v-if="!ui.n172.removed" :class="classes(ui.n172.className)" :style="ui.n172.style" class="original-button" @tap.stop="tap('n172', $event)">
<template v-if="ui.n172.textContent !== undefined"><text>{{ ui.n172.textContent }}</text></template><template v-else>
<text>+ 添加（最多 10）</text>
</template>
</button>
</template>
</view>
<view v-if="!ui.n173.removed" :class="classes(ui.n173.className)" :style="ui.n173.style" class="original-div">
<template v-if="ui.n173.textContent !== undefined"><text>{{ ui.n173.textContent }}</text></template><template v-else>
<view v-if="!ui.n174.removed" :class="classes(ui.n174.className)" :style="ui.n174.style" class="original-div">
<template v-if="ui.n174.textContent !== undefined"><text>{{ ui.n174.textContent }}</text></template><template v-else>
<view v-if="!ui.n175.removed" :class="classes(ui.n175.className)" :style="ui.n175.style" class="original-div">
<template v-if="ui.n175.textContent !== undefined"><text>{{ ui.n175.textContent }}</text></template><template v-else>
<view v-if="!ui.n176.removed" :class="classes(ui.n176.className)" :style="ui.n176.style" class="original-div">
<template v-if="ui.n176.textContent !== undefined"><text>{{ ui.n176.textContent }}</text></template><template v-else>
<view v-if="!ui.n177.removed" :class="classes(ui.n177.className)" :style="ui.n177.style" class="original-span">
<template v-if="ui.n177.textContent !== undefined"><text>{{ ui.n177.textContent }}</text></template><template v-else>
<text>1</text>
</template>
</view>
<view v-if="!ui.n178.removed" :class="classes(ui.n178.className)" :style="ui.n178.style" class="original-p">
<template v-if="ui.n178.textContent !== undefined"><text>{{ ui.n178.textContent }}</text></template><template v-else>
<text>选题与分工</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n179.removed" :class="classes(ui.n179.className)" :style="ui.n179.style" class="original-div">
<template v-if="ui.n179.textContent !== undefined"><text>{{ ui.n179.textContent }}</text></template><template v-else>
<button v-if="!ui.n180.removed" :class="classes(ui.n180.className)" :style="ui.n180.style" class="original-button" @tap.stop="tap('n180', $event)">
<template v-if="ui.n180.textContent !== undefined"><text>{{ ui.n180.textContent }}</text></template><template v-else>
<image v-if="!ui.n181.removed" :class="classes(ui.n181.className)" :style="ui.n181.style" class="original-icon" :src="iconSrc('n181')" style="width:13px;height:13px" mode="aspectFit" />
</template>
</button>
<view v-if="!ui.n182.removed" :class="classes(ui.n182.className)" :style="ui.n182.style" class="original-span">
<template v-if="ui.n182.textContent !== undefined"><text>{{ ui.n182.textContent }}</text></template><template v-else>
<text>已完成</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n183.removed" :class="classes(ui.n183.className)" :style="ui.n183.style" class="original-p">
<template v-if="ui.n183.textContent !== undefined"><text>{{ ui.n183.textContent }}</text></template><template v-else>
<text>9月03日确认 · 2份成果</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n184.removed" :class="classes(ui.n184.className)" :style="ui.n184.style" class="original-div">
<template v-if="ui.n184.textContent !== undefined"><text>{{ ui.n184.textContent }}</text></template><template v-else>
<view v-if="!ui.n185.removed" :class="classes(ui.n185.className)" :style="ui.n185.style" class="original-div">
<template v-if="ui.n185.textContent !== undefined"><text>{{ ui.n185.textContent }}</text></template><template v-else>
<view v-if="!ui.n186.removed" :class="classes(ui.n186.className)" :style="ui.n186.style" class="original-div">
<template v-if="ui.n186.textContent !== undefined"><text>{{ ui.n186.textContent }}</text></template><template v-else>
<view v-if="!ui.n187.removed" :class="classes(ui.n187.className)" :style="ui.n187.style" class="original-span">
<template v-if="ui.n187.textContent !== undefined"><text>{{ ui.n187.textContent }}</text></template><template v-else>
<text>2</text>
</template>
</view>
<view v-if="!ui.n188.removed" :class="classes(ui.n188.className)" :style="ui.n188.style" class="original-p">
<template v-if="ui.n188.textContent !== undefined"><text>{{ ui.n188.textContent }}</text></template><template v-else>
<text>资料归集</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n189.removed" :class="classes(ui.n189.className)" :style="ui.n189.style" class="original-div">
<template v-if="ui.n189.textContent !== undefined"><text>{{ ui.n189.textContent }}</text></template><template v-else>
<button v-if="!ui.n190.removed" :class="classes(ui.n190.className)" :style="ui.n190.style" class="original-button" @tap.stop="tap('n190', $event)">
<template v-if="ui.n190.textContent !== undefined"><text>{{ ui.n190.textContent }}</text></template><template v-else>
<image v-if="!ui.n191.removed" :class="classes(ui.n191.className)" :style="ui.n191.style" class="original-icon" :src="iconSrc('n191')" style="width:13px;height:13px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n192.removed" :class="classes(ui.n192.className)" :style="ui.n192.style" class="original-button" @tap.stop="tap('n192', $event)">
<template v-if="ui.n192.textContent !== undefined"><text>{{ ui.n192.textContent }}</text></template><template v-else>
<image v-if="!ui.n193.removed" :class="classes(ui.n193.className)" :style="ui.n193.style" class="original-icon" :src="iconSrc('n193')" style="width:13px;height:13px" mode="aspectFit" />
</template>
</button>
<view v-if="!ui.n194.removed" :class="classes(ui.n194.className)" :style="ui.n194.style" class="original-span">
<template v-if="ui.n194.textContent !== undefined"><text>{{ ui.n194.textContent }}</text></template><template v-else>
<text>进行中</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n195.removed" :class="classes(ui.n195.className)" :style="ui.n195.style" class="original-p">
<template v-if="ui.n195.textContent !== undefined"><text>{{ ui.n195.textContent }}</text></template><template v-else>
<text>截止 09/14 · 还剩 4 天</text>
</template>
</view>
<view v-if="!ui.n196.removed" :class="classes(ui.n196.className)" :style="ui.n196.style" class="original-div">
<template v-if="ui.n196.textContent !== undefined"><text>{{ ui.n196.textContent }}</text></template><template v-else>
<view v-if="!ui.n197.removed" :class="classes(ui.n197.className)" :style="ui.n197.style" class="original-div">
<template v-if="ui.n197.textContent !== undefined"><text>{{ ui.n197.textContent }}</text></template><template v-else>
<view v-if="!ui.n198.removed" :class="classes(ui.n198.className)" :style="ui.n198.style" class="original-span">
<template v-if="ui.n198.textContent !== undefined"><text>{{ ui.n198.textContent }}</text></template><template v-else>
<text>进度</text>
</template>
</view>
<view v-if="!ui.n199.removed" :class="classes(ui.n199.className)" :style="ui.n199.style" class="original-span">
<template v-if="ui.n199.textContent !== undefined"><text>{{ ui.n199.textContent }}</text></template><template v-else>
<text>70%</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n200.removed" :class="classes(ui.n200.className)" :style="ui.n200.style" class="original-div">
<template v-if="ui.n200.textContent !== undefined"><text>{{ ui.n200.textContent }}</text></template><template v-else>
<view v-if="!ui.n201.removed" :class="classes(ui.n201.className)" :style="ui.n201.style" class="original-div">
<template v-if="ui.n201.textContent !== undefined"><text>{{ ui.n201.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n202.removed" :class="classes(ui.n202.className)" :style="ui.n202.style" class="original-div">
<template v-if="ui.n202.textContent !== undefined"><text>{{ ui.n202.textContent }}</text></template><template v-else>
<view v-if="!ui.n203.removed" :class="classes(ui.n203.className)" :style="ui.n203.style" class="original-div">
<template v-if="ui.n203.textContent !== undefined"><text>{{ ui.n203.textContent }}</text></template><template v-else>
<view v-if="!ui.n204.removed" :class="classes(ui.n204.className)" :style="ui.n204.style" class="original-p">
<template v-if="ui.n204.textContent !== undefined"><text>{{ ui.n204.textContent }}</text></template><template v-else>
<text>任务分工</text>
</template>
</view>
<button v-if="!ui.n205.removed" :class="classes(ui.n205.className)" :style="ui.n205.style" class="original-button" @tap.stop="tap('n205', $event)">
<template v-if="ui.n205.textContent !== undefined"><text>{{ ui.n205.textContent }}</text></template><template v-else>
<text>+ 添加</text>
</template>
</button>
</template>
</view>
<view v-if="!ui.n206.removed" :class="classes(ui.n206.className)" :style="ui.n206.style" class="original-div">
<template v-if="ui.n206.textContent !== undefined"><text>{{ ui.n206.textContent }}</text></template><template v-else>
<view v-if="!ui.n207.removed" :class="classes(ui.n207.className)" :style="ui.n207.style" class="original-span">
<template v-if="ui.n207.textContent !== undefined"><text>{{ ui.n207.textContent }}</text></template><template v-else>
<text>许</text>
</template>
</view>
<view v-if="!ui.n208.removed" :class="classes(ui.n208.className)" :style="ui.n208.style" class="original-span">
<template v-if="ui.n208.textContent !== undefined"><text>{{ ui.n208.textContent }}</text></template><template v-else>
<text>访谈整理</text>
</template>
</view>
<view v-if="!ui.n209.removed" :class="classes(ui.n209.className)" :style="ui.n209.style" class="original-span">
<template v-if="ui.n209.textContent !== undefined"><text>{{ ui.n209.textContent }}</text></template><template v-else>
<text>09/12</text>
</template>
</view>
<button v-if="!ui.n210.removed" :class="classes(ui.n210.className)" :style="ui.n210.style" class="original-button" @tap.stop="tap('n210', $event)">
<template v-if="ui.n210.textContent !== undefined"><text>{{ ui.n210.textContent }}</text></template><template v-else>
<image v-if="!ui.n211.removed" :class="classes(ui.n211.className)" :style="ui.n211.style" class="original-icon" :src="iconSrc('n211')" style="width:11px;height:11px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n212.removed" :class="classes(ui.n212.className)" :style="ui.n212.style" class="original-div">
<template v-if="ui.n212.textContent !== undefined"><text>{{ ui.n212.textContent }}</text></template><template v-else>
<view v-if="!ui.n213.removed" :class="classes(ui.n213.className)" :style="ui.n213.style" class="original-span">
<template v-if="ui.n213.textContent !== undefined"><text>{{ ui.n213.textContent }}</text></template><template v-else>
<text>王</text>
</template>
</view>
<view v-if="!ui.n214.removed" :class="classes(ui.n214.className)" :style="ui.n214.style" class="original-span">
<template v-if="ui.n214.textContent !== undefined"><text>{{ ui.n214.textContent }}</text></template><template v-else>
<text>问卷分析</text>
</template>
</view>
<view v-if="!ui.n215.removed" :class="classes(ui.n215.className)" :style="ui.n215.style" class="original-span">
<template v-if="ui.n215.textContent !== undefined"><text>{{ ui.n215.textContent }}</text></template><template v-else>
<text>09/14</text>
</template>
</view>
<button v-if="!ui.n216.removed" :class="classes(ui.n216.className)" :style="ui.n216.style" class="original-button" @tap.stop="tap('n216', $event)">
<template v-if="ui.n216.textContent !== undefined"><text>{{ ui.n216.textContent }}</text></template><template v-else>
<image v-if="!ui.n217.removed" :class="classes(ui.n217.className)" :style="ui.n217.style" class="original-icon" :src="iconSrc('n217')" style="width:11px;height:11px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n218.removed" :class="classes(ui.n218.className)" :style="ui.n218.style" class="original-div">
<template v-if="ui.n218.textContent !== undefined"><text>{{ ui.n218.textContent }}</text></template><template v-else>
<view v-if="!ui.n219.removed" :class="classes(ui.n219.className)" :style="ui.n219.style" class="original-span">
<template v-if="ui.n219.textContent !== undefined"><text>{{ ui.n219.textContent }}</text></template><template v-else>
<text>林</text>
</template>
</view>
<view v-if="!ui.n220.removed" :class="classes(ui.n220.className)" :style="ui.n220.style" class="original-span">
<template v-if="ui.n220.textContent !== undefined"><text>{{ ui.n220.textContent }}</text></template><template v-else>
<text>大纲撰写</text>
</template>
</view>
<view v-if="!ui.n221.removed" :class="classes(ui.n221.className)" :style="ui.n221.style" class="original-span">
<template v-if="ui.n221.textContent !== undefined"><text>{{ ui.n221.textContent }}</text></template><template v-else>
<text>09/14</text>
</template>
</view>
<button v-if="!ui.n222.removed" :class="classes(ui.n222.className)" :style="ui.n222.style" class="original-button" @tap.stop="tap('n222', $event)">
<template v-if="ui.n222.textContent !== undefined"><text>{{ ui.n222.textContent }}</text></template><template v-else>
<image v-if="!ui.n223.removed" :class="classes(ui.n223.className)" :style="ui.n223.style" class="original-icon" :src="iconSrc('n223')" style="width:11px;height:11px" mode="aspectFit" />
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n224.removed" :class="classes(ui.n224.className)" :style="ui.n224.style" class="original-div">
<template v-if="ui.n224.textContent !== undefined"><text>{{ ui.n224.textContent }}</text></template><template v-else>
<view v-if="!ui.n225.removed" :class="classes(ui.n225.className)" :style="ui.n225.style" class="original-div">
<template v-if="ui.n225.textContent !== undefined"><text>{{ ui.n225.textContent }}</text></template><template v-else>
<view v-if="!ui.n226.removed" :class="classes(ui.n226.className)" :style="ui.n226.style" class="original-div">
<template v-if="ui.n226.textContent !== undefined"><text>{{ ui.n226.textContent }}</text></template><template v-else>
<view v-if="!ui.n227.removed" :class="classes(ui.n227.className)" :style="ui.n227.style" class="original-span">
<template v-if="ui.n227.textContent !== undefined"><text>{{ ui.n227.textContent }}</text></template><template v-else>
<text>3</text>
</template>
</view>
<view v-if="!ui.n228.removed" :class="classes(ui.n228.className)" :style="ui.n228.style" class="original-p">
<template v-if="ui.n228.textContent !== undefined"><text>{{ ui.n228.textContent }}</text></template><template v-else>
<text>分析与成稿</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n229.removed" :class="classes(ui.n229.className)" :style="ui.n229.style" class="original-div">
<template v-if="ui.n229.textContent !== undefined"><text>{{ ui.n229.textContent }}</text></template><template v-else>
<button v-if="!ui.n230.removed" :class="classes(ui.n230.className)" :style="ui.n230.style" class="original-button" @tap.stop="tap('n230', $event)">
<template v-if="ui.n230.textContent !== undefined"><text>{{ ui.n230.textContent }}</text></template><template v-else>
<image v-if="!ui.n231.removed" :class="classes(ui.n231.className)" :style="ui.n231.style" class="original-icon" :src="iconSrc('n231')" style="width:13px;height:13px" mode="aspectFit" />
</template>
</button>
<button v-if="!ui.n232.removed" :class="classes(ui.n232.className)" :style="ui.n232.style" class="original-button" @tap.stop="tap('n232', $event)">
<template v-if="ui.n232.textContent !== undefined"><text>{{ ui.n232.textContent }}</text></template><template v-else>
<image v-if="!ui.n233.removed" :class="classes(ui.n233.className)" :style="ui.n233.style" class="original-icon" :src="iconSrc('n233')" style="width:13px;height:13px" mode="aspectFit" />
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n234.removed" :class="classes(ui.n234.className)" :style="ui.n234.style" class="original-p">
<template v-if="ui.n234.textContent !== undefined"><text>{{ ui.n234.textContent }}</text></template><template v-else>
<text>按资料整理结果撰写报告</text>
</template>
</view>
</template>
</view>
<button v-if="!ui.n235.removed" :class="classes(ui.n235.className)" :style="ui.n235.style" class="original-button" @tap.stop="tap('n235', $event)">
<template v-if="ui.n235.textContent !== undefined"><text>{{ ui.n235.textContent }}</text></template><template v-else>
<image v-if="!ui.n236.removed" :class="classes(ui.n236.className)" :style="ui.n236.style" class="original-icon" :src="iconSrc('n236')" style="width:15px;height:15px" mode="aspectFit" />
<text>应用系统默认阶段模板 </text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n237.removed" :class="classes(ui.n237.className)" :style="ui.n237.style" class="original-div">
<template v-if="ui.n237.textContent !== undefined"><text>{{ ui.n237.textContent }}</text></template><template v-else>
<view v-if="!ui.n238.removed" :class="classes(ui.n238.className)" :style="ui.n238.style" class="original-div">
<template v-if="ui.n238.textContent !== undefined"><text>{{ ui.n238.textContent }}</text></template><template v-else>
<view v-if="!ui.n239.removed" :class="classes(ui.n239.className)" :style="ui.n239.style" class="original-h2">
<template v-if="ui.n239.textContent !== undefined"><text>{{ ui.n239.textContent }}</text></template><template v-else>
<text>小组成员</text>
</template>
</view>
<button v-if="!ui.n240.removed" :class="classes(ui.n240.className)" :style="ui.n240.style" class="original-button" @tap.stop="tap('n240', $event)">
<template v-if="ui.n240.textContent !== undefined"><text>{{ ui.n240.textContent }}</text></template><template v-else>
<text>+ 邀请</text>
</template>
</button>
</template>
</view>
<view v-if="!ui.n241.removed" :class="classes(ui.n241.className)" :style="ui.n241.style" class="original-div">
<template v-if="ui.n241.textContent !== undefined"><text>{{ ui.n241.textContent }}</text></template><template v-else>
<view v-if="!ui.n242.removed" :class="classes(ui.n242.className)" :style="ui.n242.style" class="original-div">
<template v-if="ui.n242.textContent !== undefined"><text>{{ ui.n242.textContent }}</text></template><template v-else>
<view v-if="!ui.n243.removed" :class="classes(ui.n243.className)" :style="ui.n243.style" class="original-p">
<template v-if="ui.n243.textContent !== undefined"><text>{{ ui.n243.textContent }}</text></template><template v-else>
<text>邀请新成员</text>
</template>
</view>
<button v-if="!ui.n244.removed" :class="classes(ui.n244.className)" :style="ui.n244.style" class="original-button" @tap.stop="tap('n244', $event)">
<template v-if="ui.n244.textContent !== undefined"><text>{{ ui.n244.textContent }}</text></template><template v-else>
<image v-if="!ui.n245.removed" :class="classes(ui.n245.className)" :style="ui.n245.style" class="original-icon" :src="iconSrc('n245')" style="width:15px;height:15px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n246.removed" :class="classes(ui.n246.className)" :style="ui.n246.style" class="original-div">
<template v-if="ui.n246.textContent !== undefined"><text>{{ ui.n246.textContent }}</text></template><template v-else>
<view v-if="!ui.n247.removed" :class="classes(ui.n247.className)" :style="ui.n247.style" class="original-p">
<template v-if="ui.n247.textContent !== undefined"><text>{{ ui.n247.textContent }}</text></template><template v-else>
<text>邀请链接（任选方式）</text>
</template>
</view>
<view v-if="!ui.n248.removed" :class="classes(ui.n248.className)" :style="ui.n248.style" class="original-div">
<template v-if="ui.n248.textContent !== undefined"><text>{{ ui.n248.textContent }}</text></template><template v-else>
<input v-if="!ui.n249.removed" :class="classes(ui.n249.className)" :style="ui.n249.style" class="original-input" :value="ui.n249.value" @input="ui.n249.value = $event.detail.value" placeholder="" :disabled="false" :maxlength="-1" />
<button v-if="!ui.n250.removed" :class="classes(ui.n250.className)" :style="ui.n250.style" class="original-button" @tap.stop="tap('n250', $event)">
<template v-if="ui.n250.textContent !== undefined"><text>{{ ui.n250.textContent }}</text></template><template v-else>
<text>复制</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n251.removed" :class="classes(ui.n251.className)" :style="ui.n251.style" class="original-p">
<template v-if="ui.n251.textContent !== undefined"><text>{{ ui.n251.textContent }}</text></template><template v-else>
<text>分享至</text>
</template>
</view>
<view v-if="!ui.n252.removed" :class="classes(ui.n252.className)" :style="ui.n252.style" class="original-div">
<template v-if="ui.n252.textContent !== undefined"><text>{{ ui.n252.textContent }}</text></template><template v-else>
<button v-if="!ui.n253.removed" :class="classes(ui.n253.className)" :style="ui.n253.style" class="original-button" @tap.stop="tap('n253', $event)">
<template v-if="ui.n253.textContent !== undefined"><text>{{ ui.n253.textContent }}</text></template><template v-else>
<image v-if="!ui.n254.removed" :class="classes(ui.n254.className)" :style="ui.n254.style" class="original-icon" :src="iconSrc('n254')" style="width:13px;height:13px" mode="aspectFit" />
<text>微信 </text>
</template>
</button>
<button v-if="!ui.n255.removed" :class="classes(ui.n255.className)" :style="ui.n255.style" class="original-button" @tap.stop="tap('n255', $event)">
<template v-if="ui.n255.textContent !== undefined"><text>{{ ui.n255.textContent }}</text></template><template v-else>
<image v-if="!ui.n256.removed" :class="classes(ui.n256.className)" :style="ui.n256.style" class="original-icon" :src="iconSrc('n256')" style="width:13px;height:13px" mode="aspectFit" />
<text>二维码 </text>
</template>
</button>
<button v-if="!ui.n257.removed" :class="classes(ui.n257.className)" :style="ui.n257.style" class="original-button" @tap.stop="tap('n257', $event)">
<template v-if="ui.n257.textContent !== undefined"><text>{{ ui.n257.textContent }}</text></template><template v-else>
<image v-if="!ui.n258.removed" :class="classes(ui.n258.className)" :style="ui.n258.style" class="original-icon" :src="iconSrc('n258')" style="width:13px;height:13px" mode="aspectFit" />
<text>链接 </text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n259.removed" :class="classes(ui.n259.className)" :style="ui.n259.style" class="original-div">
<template v-if="ui.n259.textContent !== undefined"><text>{{ ui.n259.textContent }}</text></template><template v-else>
<view v-if="!ui.n260.removed" :class="classes(ui.n260.className)" :style="ui.n260.style" class="original-div">
<template v-if="ui.n260.textContent !== undefined"><text>{{ ui.n260.textContent }}</text></template><template v-else>
<view v-if="!ui.n261.removed" :class="classes(ui.n261.className)" :style="ui.n261.style" class="original-span">
<template v-if="ui.n261.textContent !== undefined"><text>{{ ui.n261.textContent }}</text></template><template v-else>
<text>林</text>
</template>
</view>
<view v-if="!ui.n262.removed" :class="classes(ui.n262.className)" :style="ui.n262.style" class="original-div">
<template v-if="ui.n262.textContent !== undefined"><text>{{ ui.n262.textContent }}</text></template><template v-else>
<view v-if="!ui.n263.removed" :class="classes(ui.n263.className)" :style="ui.n263.style" class="original-p">
<template v-if="ui.n263.textContent !== undefined"><text>{{ ui.n263.textContent }}</text></template><template v-else>
<text>林知行</text>
</template>
</view>
<view v-if="!ui.n264.removed" :class="classes(ui.n264.className)" :style="ui.n264.style" class="original-p">
<template v-if="ui.n264.textContent !== undefined"><text>{{ ui.n264.textContent }}</text></template><template v-else>
<text>组长 · 负责大纲撰写</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n265.removed" :class="classes(ui.n265.className)" :style="ui.n265.style" class="original-span">
<template v-if="ui.n265.textContent !== undefined"><text>{{ ui.n265.textContent }}</text></template><template v-else>
<text>组长</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n266.removed" :class="classes(ui.n266.className)" :style="ui.n266.style" class="original-div">
<template v-if="ui.n266.textContent !== undefined"><text>{{ ui.n266.textContent }}</text></template><template v-else>
<view v-if="!ui.n267.removed" :class="classes(ui.n267.className)" :style="ui.n267.style" class="original-span">
<template v-if="ui.n267.textContent !== undefined"><text>{{ ui.n267.textContent }}</text></template><template v-else>
<text>许</text>
</template>
</view>
<view v-if="!ui.n268.removed" :class="classes(ui.n268.className)" :style="ui.n268.style" class="original-div">
<template v-if="ui.n268.textContent !== undefined"><text>{{ ui.n268.textContent }}</text></template><template v-else>
<view v-if="!ui.n269.removed" :class="classes(ui.n269.className)" :style="ui.n269.style" class="original-p">
<template v-if="ui.n269.textContent !== undefined"><text>{{ ui.n269.textContent }}</text></template><template v-else>
<text>许萌</text>
</template>
</view>
<view v-if="!ui.n270.removed" :class="classes(ui.n270.className)" :style="ui.n270.style" class="original-p">
<template v-if="ui.n270.textContent !== undefined"><text>{{ ui.n270.textContent }}</text></template><template v-else>
<text>组员 · 负责访谈整理</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n271.removed" :class="classes(ui.n271.className)" :style="ui.n271.style" class="original-span">
<template v-if="ui.n271.textContent !== undefined"><text>{{ ui.n271.textContent }}</text></template><template v-else>
<text>组员</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n272.removed" :class="classes(ui.n272.className)" :style="ui.n272.style" class="original-div">
<template v-if="ui.n272.textContent !== undefined"><text>{{ ui.n272.textContent }}</text></template><template v-else>
<view v-if="!ui.n273.removed" :class="classes(ui.n273.className)" :style="ui.n273.style" class="original-span">
<template v-if="ui.n273.textContent !== undefined"><text>{{ ui.n273.textContent }}</text></template><template v-else>
<text>王</text>
</template>
</view>
<view v-if="!ui.n274.removed" :class="classes(ui.n274.className)" :style="ui.n274.style" class="original-div">
<template v-if="ui.n274.textContent !== undefined"><text>{{ ui.n274.textContent }}</text></template><template v-else>
<view v-if="!ui.n275.removed" :class="classes(ui.n275.className)" :style="ui.n275.style" class="original-p">
<template v-if="ui.n275.textContent !== undefined"><text>{{ ui.n275.textContent }}</text></template><template v-else>
<text>王宁</text>
</template>
</view>
<view v-if="!ui.n276.removed" :class="classes(ui.n276.className)" :style="ui.n276.style" class="original-p">
<template v-if="ui.n276.textContent !== undefined"><text>{{ ui.n276.textContent }}</text></template><template v-else>
<text>组员 · 负责问卷分析</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n277.removed" :class="classes(ui.n277.className)" :style="ui.n277.style" class="original-span">
<template v-if="ui.n277.textContent !== undefined"><text>{{ ui.n277.textContent }}</text></template><template v-else>
<text>组员</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n278.removed" :class="classes(ui.n278.className)" :style="ui.n278.style" class="original-div">
<template v-if="ui.n278.textContent !== undefined"><text>{{ ui.n278.textContent }}</text></template><template v-else>
<view v-if="!ui.n279.removed" :class="classes(ui.n279.className)" :style="ui.n279.style" class="original-span">
<template v-if="ui.n279.textContent !== undefined"><text>{{ ui.n279.textContent }}</text></template><template v-else>
<text>?</text>
</template>
</view>
<view v-if="!ui.n280.removed" :class="classes(ui.n280.className)" :style="ui.n280.style" class="original-div">
<template v-if="ui.n280.textContent !== undefined"><text>{{ ui.n280.textContent }}</text></template><template v-else>
<view v-if="!ui.n281.removed" :class="classes(ui.n281.className)" :style="ui.n281.style" class="original-p">
<template v-if="ui.n281.textContent !== undefined"><text>{{ ui.n281.textContent }}</text></template><template v-else>
<text>待确认成员</text>
</template>
</view>
<view v-if="!ui.n282.removed" :class="classes(ui.n282.className)" :style="ui.n282.style" class="original-p">
<template v-if="ui.n282.textContent !== undefined"><text>{{ ui.n282.textContent }}</text></template><template v-else>
<text>已发送邀请</text>
</template>
</view>
</template>
</view>
<button v-if="!ui.n283.removed" :class="classes(ui.n283.className)" :style="ui.n283.style" class="original-button" @tap.stop="tap('n283', $event)">
<template v-if="ui.n283.textContent !== undefined"><text>{{ ui.n283.textContent }}</text></template><template v-else>
<text>取消</text>
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n284.removed" :class="classes(ui.n284.className)" :style="ui.n284.style" class="original-div">
<template v-if="ui.n284.textContent !== undefined"><text>{{ ui.n284.textContent }}</text></template><template v-else>
<view v-if="!ui.n285.removed" :class="classes(ui.n285.className)" :style="ui.n285.style" class="original-div">
<template v-if="ui.n285.textContent !== undefined"><text>{{ ui.n285.textContent }}</text></template><template v-else>
<view v-if="!ui.n286.removed" :class="classes(ui.n286.className)" :style="ui.n286.style" class="original-div">
<template v-if="ui.n286.textContent !== undefined"><text>{{ ui.n286.textContent }}</text></template><template v-else>
<image v-if="!ui.n287.removed" :class="classes(ui.n287.className)" :style="ui.n287.style" class="original-icon" :src="iconSrc('n287')" style="width:15px;height:15px" mode="aspectFit" />
<input v-if="!ui.n288.removed" :class="classes(ui.n288.className)" :style="ui.n288.style" class="original-input" :value="ui.n288.value" @input="ui.n288.value = $event.detail.value" placeholder="搜索操作记录、文件名、成员…" :disabled="false" :maxlength="-1" />
</template>
</view>
</template>
</view>
<view v-if="!ui.n289.removed" :class="classes(ui.n289.className)" :style="ui.n289.style" class="original-div">
<template v-if="ui.n289.textContent !== undefined"><text>{{ ui.n289.textContent }}</text></template><template v-else>
<view v-if="!ui.n290.removed" :class="classes(ui.n290.className)" :style="ui.n290.style" class="original-p">
<template v-if="ui.n290.textContent !== undefined"><text>{{ ui.n290.textContent }}</text></template><template v-else>
<text>全流程可视化追溯</text>
</template>
</view>
<view v-if="!ui.n291.removed" :class="classes(ui.n291.className)" :style="ui.n291.style" class="original-p">
<template v-if="ui.n291.textContent !== undefined"><text>{{ ui.n291.textContent }}</text></template><template v-else>
<text>搜索查询所有资料、成果、操作记录</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n292.removed" :class="classes(ui.n292.className)" :style="ui.n292.style" class="original-div">
<template v-if="ui.n292.textContent !== undefined"><text>{{ ui.n292.textContent }}</text></template><template v-else>
<view v-if="!ui.n293.removed" :class="classes(ui.n293.className)" :style="ui.n293.style" class="original-div">
<template v-if="ui.n293.textContent !== undefined"><text>{{ ui.n293.textContent }}</text></template><template v-else>
<view v-if="!ui.n294.removed" :class="classes(ui.n294.className)" :style="ui.n294.style" class="original-div">
<template v-if="ui.n294.textContent !== undefined"><text>{{ ui.n294.textContent }}</text></template><template v-else>
<view v-if="!ui.n295.removed" :class="classes(ui.n295.className)" :style="ui.n295.style" class="original-span">
<template v-if="ui.n295.textContent !== undefined"><text>{{ ui.n295.textContent }}</text></template><template v-else>
<image v-if="!ui.n296.removed" :class="classes(ui.n296.className)" :style="ui.n296.style" class="original-icon" :src="iconSrc('n296')" style="width:13px;height:13px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n297.removed" :class="classes(ui.n297.className)" :style="ui.n297.style" class="original-div">
<template v-if="ui.n297.textContent !== undefined"><text>{{ ui.n297.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</view>
<view v-if="!ui.n298.removed" :class="classes(ui.n298.className)" :style="ui.n298.style" class="original-div">
<template v-if="ui.n298.textContent !== undefined"><text>{{ ui.n298.textContent }}</text></template><template v-else>
<view v-if="!ui.n299.removed" :class="classes(ui.n299.className)" :style="ui.n299.style" class="original-p">
<template v-if="ui.n299.textContent !== undefined"><text>{{ ui.n299.textContent }}</text></template><template v-else>
<text>阶段成果归档确认</text>
</template>
</view>
<view v-if="!ui.n300.removed" :class="classes(ui.n300.className)" :style="ui.n300.style" class="original-p">
<template v-if="ui.n300.textContent !== undefined"><text>{{ ui.n300.textContent }}</text></template><template v-else>
<text>「01 选题与分工」已完成 · 9月03日 · 林知行</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n301.removed" :class="classes(ui.n301.className)" :style="ui.n301.style" class="original-div">
<template v-if="ui.n301.textContent !== undefined"><text>{{ ui.n301.textContent }}</text></template><template v-else>
<view v-if="!ui.n302.removed" :class="classes(ui.n302.className)" :style="ui.n302.style" class="original-div">
<template v-if="ui.n302.textContent !== undefined"><text>{{ ui.n302.textContent }}</text></template><template v-else>
<view v-if="!ui.n303.removed" :class="classes(ui.n303.className)" :style="ui.n303.style" class="original-span">
<template v-if="ui.n303.textContent !== undefined"><text>{{ ui.n303.textContent }}</text></template><template v-else>
<image v-if="!ui.n304.removed" :class="classes(ui.n304.className)" :style="ui.n304.style" class="original-icon" :src="iconSrc('n304')" style="width:13px;height:13px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n305.removed" :class="classes(ui.n305.className)" :style="ui.n305.style" class="original-div">
<template v-if="ui.n305.textContent !== undefined"><text>{{ ui.n305.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</view>
<view v-if="!ui.n306.removed" :class="classes(ui.n306.className)" :style="ui.n306.style" class="original-div">
<template v-if="ui.n306.textContent !== undefined"><text>{{ ui.n306.textContent }}</text></template><template v-else>
<view v-if="!ui.n307.removed" :class="classes(ui.n307.className)" :style="ui.n307.style" class="original-div">
<template v-if="ui.n307.textContent !== undefined"><text>{{ ui.n307.textContent }}</text></template><template v-else>
<view v-if="!ui.n308.removed" :class="classes(ui.n308.className)" :style="ui.n308.style" class="original-p">
<template v-if="ui.n308.textContent !== undefined"><text>{{ ui.n308.textContent }}</text></template><template v-else>
<text>王宁更新成果大纲</text>
</template>
</view>
<button v-if="!ui.n309.removed" :class="classes(ui.n309.className)" :style="ui.n309.style" class="original-button" @tap.stop="tap('n309', $event)">
<template v-if="ui.n309.textContent !== undefined"><text>{{ ui.n309.textContent }}</text></template><template v-else>
<text>v3→v4</text>
</template>
</button>
</template>
</view>
<view v-if="!ui.n310.removed" :class="classes(ui.n310.className)" :style="ui.n310.style" class="original-p">
<template v-if="ui.n310.textContent !== undefined"><text>{{ ui.n310.textContent }}</text></template><template v-else>
<text>新增问题分析章节 · 昨天 20:36</text>
</template>
</view>
<button v-if="!ui.n311.removed" :class="classes(ui.n311.className)" :style="ui.n311.style" class="original-button" @tap.stop="tap('n311', $event)">
<template v-if="ui.n311.textContent !== undefined"><text>{{ ui.n311.textContent }}</text></template><template v-else>
<text>查看差异 →</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n312.removed" :class="classes(ui.n312.className)" :style="ui.n312.style" class="original-div">
<template v-if="ui.n312.textContent !== undefined"><text>{{ ui.n312.textContent }}</text></template><template v-else>
<view v-if="!ui.n313.removed" :class="classes(ui.n313.className)" :style="ui.n313.style" class="original-div">
<template v-if="ui.n313.textContent !== undefined"><text>{{ ui.n313.textContent }}</text></template><template v-else>
<view v-if="!ui.n314.removed" :class="classes(ui.n314.className)" :style="ui.n314.style" class="original-span">
<template v-if="ui.n314.textContent !== undefined"><text>{{ ui.n314.textContent }}</text></template><template v-else>
<image v-if="!ui.n315.removed" :class="classes(ui.n315.className)" :style="ui.n315.style" class="original-icon" :src="iconSrc('n315')" style="width:13px;height:13px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n316.removed" :class="classes(ui.n316.className)" :style="ui.n316.style" class="original-div">
<template v-if="ui.n316.textContent !== undefined"><text>{{ ui.n316.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</view>
<view v-if="!ui.n317.removed" :class="classes(ui.n317.className)" :style="ui.n317.style" class="original-div">
<template v-if="ui.n317.textContent !== undefined"><text>{{ ui.n317.textContent }}</text></template><template v-else>
<view v-if="!ui.n318.removed" :class="classes(ui.n318.className)" :style="ui.n318.style" class="original-p">
<template v-if="ui.n318.textContent !== undefined"><text>{{ ui.n318.textContent }}</text></template><template v-else>
<text>第 3 次线上研讨</text>
</template>
</view>
<view v-if="!ui.n319.removed" :class="classes(ui.n319.className)" :style="ui.n319.style" class="original-p">
<template v-if="ui.n319.textContent !== undefined"><text>{{ ui.n319.textContent }}</text></template><template v-else>
<text>访谈提纲修订 · 9月08日 19:30 · 3人参与</text>
</template>
</view>
<button v-if="!ui.n320.removed" :class="classes(ui.n320.className)" :style="ui.n320.style" class="original-button" @tap.stop="tap('n320', $event)">
<template v-if="ui.n320.textContent !== undefined"><text>{{ ui.n320.textContent }}</text></template><template v-else>
<text>查看研讨记录 →</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n321.removed" :class="classes(ui.n321.className)" :style="ui.n321.style" class="original-div">
<template v-if="ui.n321.textContent !== undefined"><text>{{ ui.n321.textContent }}</text></template><template v-else>
<view v-if="!ui.n322.removed" :class="classes(ui.n322.className)" :style="ui.n322.style" class="original-div">
<template v-if="ui.n322.textContent !== undefined"><text>{{ ui.n322.textContent }}</text></template><template v-else>
<view v-if="!ui.n323.removed" :class="classes(ui.n323.className)" :style="ui.n323.style" class="original-span">
<template v-if="ui.n323.textContent !== undefined"><text>{{ ui.n323.textContent }}</text></template><template v-else>
<image v-if="!ui.n324.removed" :class="classes(ui.n324.className)" :style="ui.n324.style" class="original-icon" :src="iconSrc('n324')" style="width:13px;height:13px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n325.removed" :class="classes(ui.n325.className)" :style="ui.n325.style" class="original-div">
<template v-if="ui.n325.textContent !== undefined"><text>{{ ui.n325.textContent }}</text></template><template v-else>

</template>
</view>
</template>
</view>
<view v-if="!ui.n326.removed" :class="classes(ui.n326.className)" :style="ui.n326.style" class="original-div">
<template v-if="ui.n326.textContent !== undefined"><text>{{ ui.n326.textContent }}</text></template><template v-else>
<view v-if="!ui.n327.removed" :class="classes(ui.n327.className)" :style="ui.n327.style" class="original-p">
<template v-if="ui.n327.textContent !== undefined"><text>{{ ui.n327.textContent }}</text></template><template v-else>
<text>许萌上传访谈记录 v2</text>
</template>
</view>
<view v-if="!ui.n328.removed" :class="classes(ui.n328.className)" :style="ui.n328.style" class="original-p">
<template v-if="ui.n328.textContent !== undefined"><text>{{ ui.n328.textContent }}</text></template><template v-else>
<text>今天 10:18</text>
</template>
</view>
<button v-if="!ui.n329.removed" :class="classes(ui.n329.className)" :style="ui.n329.style" class="original-button" @tap.stop="tap('n329', $event)">
<template v-if="ui.n329.textContent !== undefined"><text>{{ ui.n329.textContent }}</text></template><template v-else>
<text>版本历史 →</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n330.removed" :class="classes(ui.n330.className)" :style="ui.n330.style" class="original-div">
<template v-if="ui.n330.textContent !== undefined"><text>{{ ui.n330.textContent }}</text></template><template v-else>
<view v-if="!ui.n331.removed" :class="classes(ui.n331.className)" :style="ui.n331.style" class="original-div">
<template v-if="ui.n331.textContent !== undefined"><text>{{ ui.n331.textContent }}</text></template><template v-else>
<view v-if="!ui.n332.removed" :class="classes(ui.n332.className)" :style="ui.n332.style" class="original-span">
<template v-if="ui.n332.textContent !== undefined"><text>{{ ui.n332.textContent }}</text></template><template v-else>
<image v-if="!ui.n333.removed" :class="classes(ui.n333.className)" :style="ui.n333.style" class="original-icon" :src="iconSrc('n333')" style="width:13px;height:13px" mode="aspectFit" />
</template>
</view>
</template>
</view>
<view v-if="!ui.n334.removed" :class="classes(ui.n334.className)" :style="ui.n334.style" class="original-div">
<template v-if="ui.n334.textContent !== undefined"><text>{{ ui.n334.textContent }}</text></template><template v-else>
<view v-if="!ui.n335.removed" :class="classes(ui.n335.className)" :style="ui.n335.style" class="original-p">
<template v-if="ui.n335.textContent !== undefined"><text>{{ ui.n335.textContent }}</text></template><template v-else>
<text>项目创建</text>
</template>
</view>
<view v-if="!ui.n336.removed" :class="classes(ui.n336.className)" :style="ui.n336.style" class="original-p">
<template v-if="ui.n336.textContent !== undefined"><text>{{ ui.n336.textContent }}</text></template><template v-else>
<text>林知行 · 9月01日 · 4 个阶段</text>
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
<view v-if="!ui.n337.removed" :class="classes(ui.n337.className)" :style="ui.n337.style" class="original-div" @tap.stop="tap('n337', $event)">
<template v-if="ui.n337.textContent !== undefined"><text>{{ ui.n337.textContent }}</text></template><template v-else>
<view v-if="!ui.n338.removed" :class="classes(ui.n338.className)" :style="ui.n338.style" class="original-div" @tap.stop="tap('n338', $event)">
<template v-if="ui.n338.textContent !== undefined"><text>{{ ui.n338.textContent }}</text></template><template v-else>
<view v-if="!ui.n339.removed" :class="classes(ui.n339.className)" :style="ui.n339.style" class="original-div">
<template v-if="ui.n339.textContent !== undefined"><text>{{ ui.n339.textContent }}</text></template><template v-else>
<view v-if="!ui.n340.removed" :class="classes(ui.n340.className)" :style="ui.n340.style" class="original-p">
<template v-if="ui.n340.textContent !== undefined"><text>{{ ui.n340.textContent }}</text></template><template v-else>
<text>版本对比</text>
</template>
</view>
<button v-if="!ui.n341.removed" :class="classes(ui.n341.className)" :style="ui.n341.style" class="original-button" @tap.stop="tap('n341', $event)">
<template v-if="ui.n341.textContent !== undefined"><text>{{ ui.n341.textContent }}</text></template><template v-else>
<image v-if="!ui.n342.removed" :class="classes(ui.n342.className)" :style="ui.n342.style" class="original-icon" :src="iconSrc('n342')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n343.removed" :class="classes(ui.n343.className)" :style="ui.n343.style" class="original-div">
<template v-if="ui.n343.textContent !== undefined"><text>{{ ui.n343.textContent }}</text></template><template v-else>
<view v-if="!ui.n344.removed" :class="classes(ui.n344.className)" :style="ui.n344.style" class="original-div">
<template v-if="ui.n344.textContent !== undefined"><text>{{ ui.n344.textContent }}</text></template><template v-else>
<view v-if="!ui.n345.removed" :class="classes(ui.n345.className)" :style="ui.n345.style" class="original-div">
<template v-if="ui.n345.textContent !== undefined"><text>{{ ui.n345.textContent }}</text></template><template v-else>
<view v-if="!ui.n346.removed" :class="classes(ui.n346.className)" :style="ui.n346.style" class="original-p">
<template v-if="ui.n346.textContent !== undefined"><text>{{ ui.n346.textContent }}</text></template><template v-else>
<text>v3</text>
</template>
</view>
<view v-if="!ui.n347.removed" :class="classes(ui.n347.className)" :style="ui.n347.style" class="original-p">
<template v-if="ui.n347.textContent !== undefined"><text>{{ ui.n347.textContent }}</text></template><template v-else>
<text>9月07日</text>
</template>
</view>
</template>
</view>
<image v-if="!ui.n348.removed" :class="classes(ui.n348.className)" :style="ui.n348.style" class="original-icon" :src="iconSrc('n348')" style="width:16px;height:16px" mode="aspectFit" />
<view v-if="!ui.n349.removed" :class="classes(ui.n349.className)" :style="ui.n349.style" class="original-div">
<template v-if="ui.n349.textContent !== undefined"><text>{{ ui.n349.textContent }}</text></template><template v-else>
<view v-if="!ui.n350.removed" :class="classes(ui.n350.className)" :style="ui.n350.style" class="original-p">
<template v-if="ui.n350.textContent !== undefined"><text>{{ ui.n350.textContent }}</text></template><template v-else>
<text>v4（当前）</text>
</template>
</view>
<view v-if="!ui.n351.removed" :class="classes(ui.n351.className)" :style="ui.n351.style" class="original-p">
<template v-if="ui.n351.textContent !== undefined"><text>{{ ui.n351.textContent }}</text></template><template v-else>
<text>昨天 20:36</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n352.removed" :class="classes(ui.n352.className)" :style="ui.n352.style" class="original-p">
<template v-if="ui.n352.textContent !== undefined"><text>{{ ui.n352.textContent }}</text></template><template v-else>
<text>修改人：王宁</text>
</template>
</view>
</template>
</view>
<view v-if="!ui.n353.removed" :class="classes(ui.n353.className)" :style="ui.n353.style" class="original-div">
<template v-if="ui.n353.textContent !== undefined"><text>{{ ui.n353.textContent }}</text></template><template v-else>
<view v-if="!ui.n354.removed" :class="classes(ui.n354.className)" :style="ui.n354.style" class="original-div">
<template v-if="ui.n354.textContent !== undefined"><text>{{ ui.n354.textContent }}</text></template><template v-else>
<view v-if="!ui.n355.removed" :class="classes(ui.n355.className)" :style="ui.n355.style" class="original-span">
<template v-if="ui.n355.textContent !== undefined"><text>{{ ui.n355.textContent }}</text></template><template v-else>
<text>+</text>
</template>
</view>
<view v-if="!ui.n356.removed" :class="classes(ui.n356.className)" :style="ui.n356.style" class="original-div">
<template v-if="ui.n356.textContent !== undefined"><text>{{ ui.n356.textContent }}</text></template><template v-else>
<view v-if="!ui.n357.removed" :class="classes(ui.n357.className)" :style="ui.n357.style" class="original-p">
<template v-if="ui.n357.textContent !== undefined"><text>{{ ui.n357.textContent }}</text></template><template v-else>
<text>新增第 4 章「问题分析」</text>
</template>
</view>
<view v-if="!ui.n358.removed" :class="classes(ui.n358.className)" :style="ui.n358.style" class="original-p">
<template v-if="ui.n358.textContent !== undefined"><text>{{ ui.n358.textContent }}</text></template><template v-else>
<text>+ 约 1200 字</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n359.removed" :class="classes(ui.n359.className)" :style="ui.n359.style" class="original-div">
<template v-if="ui.n359.textContent !== undefined"><text>{{ ui.n359.textContent }}</text></template><template v-else>
<view v-if="!ui.n360.removed" :class="classes(ui.n360.className)" :style="ui.n360.style" class="original-span">
<template v-if="ui.n360.textContent !== undefined"><text>{{ ui.n360.textContent }}</text></template><template v-else>
<text>~</text>
</template>
</view>
<view v-if="!ui.n361.removed" :class="classes(ui.n361.className)" :style="ui.n361.style" class="original-div">
<template v-if="ui.n361.textContent !== undefined"><text>{{ ui.n361.textContent }}</text></template><template v-else>
<view v-if="!ui.n362.removed" :class="classes(ui.n362.className)" :style="ui.n362.style" class="original-p">
<template v-if="ui.n362.textContent !== undefined"><text>{{ ui.n362.textContent }}</text></template><template v-else>
<text>修改第 2 章结构，新增小节</text>
</template>
</view>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n363.removed" :class="classes(ui.n363.className)" :style="ui.n363.style" class="original-div">
<template v-if="ui.n363.textContent !== undefined"><text>{{ ui.n363.textContent }}</text></template><template v-else>
<button v-if="!ui.n364.removed" :class="classes(ui.n364.className)" :style="ui.n364.style" class="original-button" @tap.stop="tap('n364', $event)">
<template v-if="ui.n364.textContent !== undefined"><text>{{ ui.n364.textContent }}</text></template><template v-else>
<text>回滚至 v3</text>
</template>
</button>
<button v-if="!ui.n365.removed" :class="classes(ui.n365.className)" :style="ui.n365.style" class="original-button" @tap.stop="tap('n365', $event)">
<template v-if="ui.n365.textContent !== undefined"><text>{{ ui.n365.textContent }}</text></template><template v-else>
<text>了解</text>
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n366.removed" :class="classes(ui.n366.className)" :style="ui.n366.style" class="original-div" @tap.stop="tap('n366', $event)">
<template v-if="ui.n366.textContent !== undefined"><text>{{ ui.n366.textContent }}</text></template><template v-else>
<view v-if="!ui.n367.removed" :class="classes(ui.n367.className)" :style="ui.n367.style" class="original-div" @tap.stop="tap('n367', $event)">
<template v-if="ui.n367.textContent !== undefined"><text>{{ ui.n367.textContent }}</text></template><template v-else>
<view v-if="!ui.n368.removed" :class="classes(ui.n368.className)" :style="ui.n368.style" class="original-div">
<template v-if="ui.n368.textContent !== undefined"><text>{{ ui.n368.textContent }}</text></template><template v-else>
<view v-if="!ui.n369.removed" :class="classes(ui.n369.className)" :style="ui.n369.style" class="original-p">
<template v-if="ui.n369.textContent !== undefined"><text>{{ ui.n369.textContent }}</text></template><template v-else>
<text>编辑阶段</text>
</template>
</view>
<button v-if="!ui.n370.removed" :class="classes(ui.n370.className)" :style="ui.n370.style" class="original-button" @tap.stop="tap('n370', $event)">
<template v-if="ui.n370.textContent !== undefined"><text>{{ ui.n370.textContent }}</text></template><template v-else>
<image v-if="!ui.n371.removed" :class="classes(ui.n371.className)" :style="ui.n371.style" class="original-icon" :src="iconSrc('n371')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n372.removed" :class="classes(ui.n372.className)" :style="ui.n372.style" class="original-div">
<template v-if="ui.n372.textContent !== undefined"><text>{{ ui.n372.textContent }}</text></template><template v-else>
<view v-if="!ui.n373.removed" :class="classes(ui.n373.className)" :style="ui.n373.style" class="original-div">
<template v-if="ui.n373.textContent !== undefined"><text>{{ ui.n373.textContent }}</text></template><template v-else>
<view v-if="!ui.n374.removed" :class="classes(ui.n374.className)" :style="ui.n374.style" class="original-label">
<template v-if="ui.n374.textContent !== undefined"><text>{{ ui.n374.textContent }}</text></template><template v-else>
<text>阶段名称</text>
</template>
</view>
<input v-if="!ui.n375.removed" :class="classes(ui.n375.className)" :style="ui.n375.style" class="original-input" :value="ui.n375.value" @input="ui.n375.value = $event.detail.value" placeholder="" :disabled="false" :maxlength="-1" />
</template>
</view>
<view v-if="!ui.n376.removed" :class="classes(ui.n376.className)" :style="ui.n376.style" class="original-div">
<template v-if="ui.n376.textContent !== undefined"><text>{{ ui.n376.textContent }}</text></template><template v-else>
<view v-if="!ui.n377.removed" :class="classes(ui.n377.className)" :style="ui.n377.style" class="original-label">
<template v-if="ui.n377.textContent !== undefined"><text>{{ ui.n377.textContent }}</text></template><template v-else>
<text>截止日期</text>
</template>
</view>
<!-- #ifdef H5 -->
<NativeDate v-if="!ui.n378.removed" :class="classes(ui.n378.className)" :style="ui.n378.style" v-model:value="ui.n378.value" />
<!-- #endif -->
<!-- #ifndef H5 -->
<picker v-if="!ui.n378.removed" :class="classes(ui.n378.className)" :style="ui.n378.style" class="original-date" mode="date" :value="ui.n378.value" @change="ui.n378.value = $event.detail.value"><view>{{ ui.n378.value ? ui.n378.value.replaceAll('-', '/') : '年/月/日' }}</view></picker>
<!-- #endif -->
</template>
</view>
<view v-if="!ui.n379.removed" :class="classes(ui.n379.className)" :style="ui.n379.style" class="original-div">
<template v-if="ui.n379.textContent !== undefined"><text>{{ ui.n379.textContent }}</text></template><template v-else>
<view v-if="!ui.n380.removed" :class="classes(ui.n380.className)" :style="ui.n380.style" class="original-label">
<template v-if="ui.n380.textContent !== undefined"><text>{{ ui.n380.textContent }}</text></template><template v-else>
<text>阶段描述</text>
</template>
</view>
<input v-if="!ui.n381.removed" :class="classes(ui.n381.className)" :style="ui.n381.style" class="original-input" :value="ui.n381.value" @input="ui.n381.value = $event.detail.value" placeholder="" :disabled="false" :maxlength="-1" />
</template>
</view>
</template>
</view>
<view v-if="!ui.n382.removed" :class="classes(ui.n382.className)" :style="ui.n382.style" class="original-div">
<template v-if="ui.n382.textContent !== undefined"><text>{{ ui.n382.textContent }}</text></template><template v-else>
<button v-if="!ui.n383.removed" :class="classes(ui.n383.className)" :style="ui.n383.style" class="original-button" @tap.stop="tap('n383', $event)">
<template v-if="ui.n383.textContent !== undefined"><text>{{ ui.n383.textContent }}</text></template><template v-else>
<text>删除阶段</text>
</template>
</button>
<button v-if="!ui.n384.removed" :class="classes(ui.n384.className)" :style="ui.n384.style" class="original-button" @tap.stop="tap('n384', $event)">
<template v-if="ui.n384.textContent !== undefined"><text>{{ ui.n384.textContent }}</text></template><template v-else>
<text>保存</text>
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n385.removed" :class="classes(ui.n385.className)" :style="ui.n385.style" class="original-div" @tap.stop="tap('n385', $event)">
<template v-if="ui.n385.textContent !== undefined"><text>{{ ui.n385.textContent }}</text></template><template v-else>
<view v-if="!ui.n386.removed" :class="classes(ui.n386.className)" :style="ui.n386.style" class="original-div" @tap.stop="tap('n386', $event)">
<template v-if="ui.n386.textContent !== undefined"><text>{{ ui.n386.textContent }}</text></template><template v-else>
<view v-if="!ui.n387.removed" :class="classes(ui.n387.className)" :style="ui.n387.style" class="original-div">
<template v-if="ui.n387.textContent !== undefined"><text>{{ ui.n387.textContent }}</text></template><template v-else>
<view v-if="!ui.n388.removed" :class="classes(ui.n388.className)" :style="ui.n388.style" class="original-p">
<template v-if="ui.n388.textContent !== undefined"><text>{{ ui.n388.textContent }}</text></template><template v-else>
<text>新增阶段</text>
</template>
</view>
<button v-if="!ui.n389.removed" :class="classes(ui.n389.className)" :style="ui.n389.style" class="original-button" @tap.stop="tap('n389', $event)">
<template v-if="ui.n389.textContent !== undefined"><text>{{ ui.n389.textContent }}</text></template><template v-else>
<image v-if="!ui.n390.removed" :class="classes(ui.n390.className)" :style="ui.n390.style" class="original-icon" :src="iconSrc('n390')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n391.removed" :class="classes(ui.n391.className)" :style="ui.n391.style" class="original-div">
<template v-if="ui.n391.textContent !== undefined"><text>{{ ui.n391.textContent }}</text></template><template v-else>
<view v-if="!ui.n392.removed" :class="classes(ui.n392.className)" :style="ui.n392.style" class="original-div">
<template v-if="ui.n392.textContent !== undefined"><text>{{ ui.n392.textContent }}</text></template><template v-else>
<view v-if="!ui.n393.removed" :class="classes(ui.n393.className)" :style="ui.n393.style" class="original-label">
<template v-if="ui.n393.textContent !== undefined"><text>{{ ui.n393.textContent }}</text></template><template v-else>
<text>阶段名称</text>
</template>
</view>
<input v-if="!ui.n394.removed" :class="classes(ui.n394.className)" :style="ui.n394.style" class="original-input" :value="ui.n394.value" @input="ui.n394.value = $event.detail.value" placeholder="输入阶段名称…" :disabled="false" :maxlength="-1" />
</template>
</view>
<view v-if="!ui.n395.removed" :class="classes(ui.n395.className)" :style="ui.n395.style" class="original-div">
<template v-if="ui.n395.textContent !== undefined"><text>{{ ui.n395.textContent }}</text></template><template v-else>
<view v-if="!ui.n396.removed" :class="classes(ui.n396.className)" :style="ui.n396.style" class="original-label">
<template v-if="ui.n396.textContent !== undefined"><text>{{ ui.n396.textContent }}</text></template><template v-else>
<text>截止日期</text>
</template>
</view>
<!-- #ifdef H5 -->
<NativeDate v-if="!ui.n397.removed" :class="classes(ui.n397.className)" :style="ui.n397.style" v-model:value="ui.n397.value" />
<!-- #endif -->
<!-- #ifndef H5 -->
<picker v-if="!ui.n397.removed" :class="classes(ui.n397.className)" :style="ui.n397.style" class="original-date" mode="date" :value="ui.n397.value" @change="ui.n397.value = $event.detail.value"><view>{{ ui.n397.value ? ui.n397.value.replaceAll('-', '/') : '年/月/日' }}</view></picker>
<!-- #endif -->
</template>
</view>
<view v-if="!ui.n398.removed" :class="classes(ui.n398.className)" :style="ui.n398.style" class="original-div">
<template v-if="ui.n398.textContent !== undefined"><text>{{ ui.n398.textContent }}</text></template><template v-else>
<view v-if="!ui.n399.removed" :class="classes(ui.n399.className)" :style="ui.n399.style" class="original-label">
<template v-if="ui.n399.textContent !== undefined"><text>{{ ui.n399.textContent }}</text></template><template v-else>
<text>阶段描述</text>
</template>
</view>
<input v-if="!ui.n400.removed" :class="classes(ui.n400.className)" :style="ui.n400.style" class="original-input" :value="ui.n400.value" @input="ui.n400.value = $event.detail.value" placeholder="简要描述阶段目标…" :disabled="false" :maxlength="-1" />
</template>
</view>
</template>
</view>
<button v-if="!ui.n401.removed" :class="classes(ui.n401.className)" :style="ui.n401.style" class="original-button" @tap.stop="tap('n401', $event)">
<template v-if="ui.n401.textContent !== undefined"><text>{{ ui.n401.textContent }}</text></template><template v-else>
<text>确认添加</text>
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</scroll-view>
<view v-if="!ui.n402.removed" :class="classes(ui.n402.className)" :style="ui.n402.style" class="original-div" @tap.stop="tap('n402', $event)">
<template v-if="ui.n402.textContent !== undefined"><text>{{ ui.n402.textContent }}</text></template><template v-else>
<view v-if="!ui.n403.removed" :class="classes(ui.n403.className)" :style="ui.n403.style" class="original-div" @tap.stop="tap('n403', $event)">
<template v-if="ui.n403.textContent !== undefined"><text>{{ ui.n403.textContent }}</text></template><template v-else>
<view v-if="!ui.n404.removed" :class="classes(ui.n404.className)" :style="ui.n404.style" class="original-div">
<template v-if="ui.n404.textContent !== undefined"><text>{{ ui.n404.textContent }}</text></template><template v-else>
<view v-if="!ui.n405.removed" :class="classes(ui.n405.className)" :style="ui.n405.style" class="original-p">
<template v-if="ui.n405.textContent !== undefined"><text>{{ ui.n405.textContent }}</text></template><template v-else>
<text>多维筛选</text>
</template>
</view>
<button v-if="!ui.n406.removed" :class="classes(ui.n406.className)" :style="ui.n406.style" class="original-button" @tap.stop="tap('n406', $event)">
<template v-if="ui.n406.textContent !== undefined"><text>{{ ui.n406.textContent }}</text></template><template v-else>
<image v-if="!ui.n407.removed" :class="classes(ui.n407.className)" :style="ui.n407.style" class="original-icon" :src="iconSrc('n407')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<view v-if="!ui.n408.removed" :class="classes(ui.n408.className)" :style="ui.n408.style" class="original-div">
<template v-if="ui.n408.textContent !== undefined"><text>{{ ui.n408.textContent }}</text></template><template v-else>
<view v-if="!ui.n409.removed" :class="classes(ui.n409.className)" :style="ui.n409.style" class="original-p">
<template v-if="ui.n409.textContent !== undefined"><text>{{ ui.n409.textContent }}</text></template><template v-else>
<text>关键词</text>
</template>
</view>
<view v-if="!ui.n410.removed" :class="classes(ui.n410.className)" :style="ui.n410.style" class="original-div">
<template v-if="ui.n410.textContent !== undefined"><text>{{ ui.n410.textContent }}</text></template><template v-else>
<image v-if="!ui.n411.removed" :class="classes(ui.n411.className)" :style="ui.n411.style" class="original-icon" :src="iconSrc('n411')" style="width:15px;height:15px" mode="aspectFit" />
<input v-if="!ui.n412.removed" :class="classes(ui.n412.className)" :style="ui.n412.style" class="original-input" :value="ui.n412.value" @input="ui.n412.value = $event.detail.value" placeholder="搜索文件名、备注、标签…" :disabled="false" :maxlength="-1" />
</template>
</view>
</template>
</view>
<view v-if="!ui.n413.removed" :class="classes(ui.n413.className)" :style="ui.n413.style" class="original-div">
<template v-if="ui.n413.textContent !== undefined"><text>{{ ui.n413.textContent }}</text></template><template v-else>
<view v-if="!ui.n414.removed" :class="classes(ui.n414.className)" :style="ui.n414.style" class="original-p">
<template v-if="ui.n414.textContent !== undefined"><text>{{ ui.n414.textContent }}</text></template><template v-else>
<text>文件格式</text>
</template>
</view>
<view v-if="!ui.n415.removed" :class="classes(ui.n415.className)" :style="ui.n415.style" class="original-div">
<template v-if="ui.n415.textContent !== undefined"><text>{{ ui.n415.textContent }}</text></template><template v-else>
<button v-if="!ui.n416.removed" :class="classes(ui.n416.className)" :style="ui.n416.style" class="original-button">
<template v-if="ui.n416.textContent !== undefined"><text>{{ ui.n416.textContent }}</text></template><template v-else>
<text>全部</text>
</template>
</button>
<button v-if="!ui.n417.removed" :class="classes(ui.n417.className)" :style="ui.n417.style" class="original-button" @tap.stop="tap('n417', $event)">
<template v-if="ui.n417.textContent !== undefined"><text>{{ ui.n417.textContent }}</text></template><template v-else>
<text>.docx</text>
</template>
</button>
<button v-if="!ui.n418.removed" :class="classes(ui.n418.className)" :style="ui.n418.style" class="original-button" @tap.stop="tap('n418', $event)">
<template v-if="ui.n418.textContent !== undefined"><text>{{ ui.n418.textContent }}</text></template><template v-else>
<text>.xlsx</text>
</template>
</button>
<button v-if="!ui.n419.removed" :class="classes(ui.n419.className)" :style="ui.n419.style" class="original-button" @tap.stop="tap('n419', $event)">
<template v-if="ui.n419.textContent !== undefined"><text>{{ ui.n419.textContent }}</text></template><template v-else>
<text>.pdf</text>
</template>
</button>
<button v-if="!ui.n420.removed" :class="classes(ui.n420.className)" :style="ui.n420.style" class="original-button" @tap.stop="tap('n420', $event)">
<template v-if="ui.n420.textContent !== undefined"><text>{{ ui.n420.textContent }}</text></template><template v-else>
<text>.png</text>
</template>
</button>
<button v-if="!ui.n421.removed" :class="classes(ui.n421.className)" :style="ui.n421.style" class="original-button" @tap.stop="tap('n421', $event)">
<template v-if="ui.n421.textContent !== undefined"><text>{{ ui.n421.textContent }}</text></template><template v-else>
<text>.mp4</text>
</template>
</button>
<button v-if="!ui.n422.removed" :class="classes(ui.n422.className)" :style="ui.n422.style" class="original-button" @tap.stop="tap('n422', $event)">
<template v-if="ui.n422.textContent !== undefined"><text>{{ ui.n422.textContent }}</text></template><template v-else>
<text>代码</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n423.removed" :class="classes(ui.n423.className)" :style="ui.n423.style" class="original-div">
<template v-if="ui.n423.textContent !== undefined"><text>{{ ui.n423.textContent }}</text></template><template v-else>
<view v-if="!ui.n424.removed" :class="classes(ui.n424.className)" :style="ui.n424.style" class="original-p">
<template v-if="ui.n424.textContent !== undefined"><text>{{ ui.n424.textContent }}</text></template><template v-else>
<text>所属阶段</text>
</template>
</view>
<view v-if="!ui.n425.removed" :class="classes(ui.n425.className)" :style="ui.n425.style" class="original-div">
<template v-if="ui.n425.textContent !== undefined"><text>{{ ui.n425.textContent }}</text></template><template v-else>
<button v-if="!ui.n426.removed" :class="classes(ui.n426.className)" :style="ui.n426.style" class="original-button">
<template v-if="ui.n426.textContent !== undefined"><text>{{ ui.n426.textContent }}</text></template><template v-else>
<text>全部</text>
</template>
</button>
<button v-if="!ui.n427.removed" :class="classes(ui.n427.className)" :style="ui.n427.style" class="original-button" @tap.stop="tap('n427', $event)">
<template v-if="ui.n427.textContent !== undefined"><text>{{ ui.n427.textContent }}</text></template><template v-else>
<text>01 选题与分工</text>
</template>
</button>
<button v-if="!ui.n428.removed" :class="classes(ui.n428.className)" :style="ui.n428.style" class="original-button" @tap.stop="tap('n428', $event)">
<template v-if="ui.n428.textContent !== undefined"><text>{{ ui.n428.textContent }}</text></template><template v-else>
<text>02 资料归集</text>
</template>
</button>
<button v-if="!ui.n429.removed" :class="classes(ui.n429.className)" :style="ui.n429.style" class="original-button" @tap.stop="tap('n429', $event)">
<template v-if="ui.n429.textContent !== undefined"><text>{{ ui.n429.textContent }}</text></template><template v-else>
<text>03 分析与成稿</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n430.removed" :class="classes(ui.n430.className)" :style="ui.n430.style" class="original-div">
<template v-if="ui.n430.textContent !== undefined"><text>{{ ui.n430.textContent }}</text></template><template v-else>
<view v-if="!ui.n431.removed" :class="classes(ui.n431.className)" :style="ui.n431.style" class="original-p">
<template v-if="ui.n431.textContent !== undefined"><text>{{ ui.n431.textContent }}</text></template><template v-else>
<text>上传者</text>
</template>
</view>
<view v-if="!ui.n432.removed" :class="classes(ui.n432.className)" :style="ui.n432.style" class="original-div">
<template v-if="ui.n432.textContent !== undefined"><text>{{ ui.n432.textContent }}</text></template><template v-else>
<button v-if="!ui.n433.removed" :class="classes(ui.n433.className)" :style="ui.n433.style" class="original-button">
<template v-if="ui.n433.textContent !== undefined"><text>{{ ui.n433.textContent }}</text></template><template v-else>
<text>全体成员</text>
</template>
</button>
<button v-if="!ui.n434.removed" :class="classes(ui.n434.className)" :style="ui.n434.style" class="original-button" @tap.stop="tap('n434', $event)">
<template v-if="ui.n434.textContent !== undefined"><text>{{ ui.n434.textContent }}</text></template><template v-else>
<view v-if="!ui.n435.removed" :class="classes(ui.n435.className)" :style="ui.n435.style" class="original-span">
<template v-if="ui.n435.textContent !== undefined"><text>{{ ui.n435.textContent }}</text></template><template v-else>

</template>
</view>
<text>林知行 </text>
</template>
</button>
<button v-if="!ui.n436.removed" :class="classes(ui.n436.className)" :style="ui.n436.style" class="original-button" @tap.stop="tap('n436', $event)">
<template v-if="ui.n436.textContent !== undefined"><text>{{ ui.n436.textContent }}</text></template><template v-else>
<view v-if="!ui.n437.removed" :class="classes(ui.n437.className)" :style="ui.n437.style" class="original-span">
<template v-if="ui.n437.textContent !== undefined"><text>{{ ui.n437.textContent }}</text></template><template v-else>

</template>
</view>
<text>许萌 </text>
</template>
</button>
<button v-if="!ui.n438.removed" :class="classes(ui.n438.className)" :style="ui.n438.style" class="original-button" @tap.stop="tap('n438', $event)">
<template v-if="ui.n438.textContent !== undefined"><text>{{ ui.n438.textContent }}</text></template><template v-else>
<view v-if="!ui.n439.removed" :class="classes(ui.n439.className)" :style="ui.n439.style" class="original-span">
<template v-if="ui.n439.textContent !== undefined"><text>{{ ui.n439.textContent }}</text></template><template v-else>

</template>
</view>
<text>王宁 </text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n440.removed" :class="classes(ui.n440.className)" :style="ui.n440.style" class="original-div">
<template v-if="ui.n440.textContent !== undefined"><text>{{ ui.n440.textContent }}</text></template><template v-else>
<view v-if="!ui.n441.removed" :class="classes(ui.n441.className)" :style="ui.n441.style" class="original-p">
<template v-if="ui.n441.textContent !== undefined"><text>{{ ui.n441.textContent }}</text></template><template v-else>
<text>文件状态</text>
</template>
</view>
<view v-if="!ui.n442.removed" :class="classes(ui.n442.className)" :style="ui.n442.style" class="original-div">
<template v-if="ui.n442.textContent !== undefined"><text>{{ ui.n442.textContent }}</text></template><template v-else>
<button v-if="!ui.n443.removed" :class="classes(ui.n443.className)" :style="ui.n443.style" class="original-button">
<template v-if="ui.n443.textContent !== undefined"><text>{{ ui.n443.textContent }}</text></template><template v-else>
<text>未隐藏</text>
</template>
</button>
<button v-if="!ui.n444.removed" :class="classes(ui.n444.className)" :style="ui.n444.style" class="original-button" @tap.stop="tap('n444', $event)">
<template v-if="ui.n444.textContent !== undefined"><text>{{ ui.n444.textContent }}</text></template><template v-else>
<text>已入库</text>
</template>
</button>
<button v-if="!ui.n445.removed" :class="classes(ui.n445.className)" :style="ui.n445.style" class="original-button" @tap.stop="tap('n445', $event)">
<template v-if="ui.n445.textContent !== undefined"><text>{{ ui.n445.textContent }}</text></template><template v-else>
<text>待审核</text>
</template>
</button>
<button v-if="!ui.n446.removed" :class="classes(ui.n446.className)" :style="ui.n446.style" class="original-button" @tap.stop="tap('n446', $event)">
<template v-if="ui.n446.textContent !== undefined"><text>{{ ui.n446.textContent }}</text></template><template v-else>
<text>草稿</text>
</template>
</button>
<button v-if="!ui.n447.removed" :class="classes(ui.n447.className)" :style="ui.n447.style" class="original-button" @tap.stop="tap('n447', $event)">
<template v-if="ui.n447.textContent !== undefined"><text>{{ ui.n447.textContent }}</text></template><template v-else>
<text>闲置</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n448.removed" :class="classes(ui.n448.className)" :style="ui.n448.style" class="original-div">
<template v-if="ui.n448.textContent !== undefined"><text>{{ ui.n448.textContent }}</text></template><template v-else>
<view v-if="!ui.n449.removed" :class="classes(ui.n449.className)" :style="ui.n449.style" class="original-p">
<template v-if="ui.n449.textContent !== undefined"><text>{{ ui.n449.textContent }}</text></template><template v-else>
<text>排序方式</text>
</template>
</view>
<view v-if="!ui.n450.removed" :class="classes(ui.n450.className)" :style="ui.n450.style" class="original-div">
<template v-if="ui.n450.textContent !== undefined"><text>{{ ui.n450.textContent }}</text></template><template v-else>
<button v-if="!ui.n451.removed" :class="classes(ui.n451.className)" :style="ui.n451.style" class="original-button">
<template v-if="ui.n451.textContent !== undefined"><text>{{ ui.n451.textContent }}</text></template><template v-else>
<text>最新上传</text>
</template>
</button>
<button v-if="!ui.n452.removed" :class="classes(ui.n452.className)" :style="ui.n452.style" class="original-button">
<template v-if="ui.n452.textContent !== undefined"><text>{{ ui.n452.textContent }}</text></template><template v-else>
<text>按阶段</text>
</template>
</button>
<button v-if="!ui.n453.removed" :class="classes(ui.n453.className)" :style="ui.n453.style" class="original-button">
<template v-if="ui.n453.textContent !== undefined"><text>{{ ui.n453.textContent }}</text></template><template v-else>
<text>按上传者</text>
</template>
</button>
<button v-if="!ui.n454.removed" :class="classes(ui.n454.className)" :style="ui.n454.style" class="original-button">
<template v-if="ui.n454.textContent !== undefined"><text>{{ ui.n454.textContent }}</text></template><template v-else>
<text>文件名</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n455.removed" :class="classes(ui.n455.className)" :style="ui.n455.style" class="original-div">
<template v-if="ui.n455.textContent !== undefined"><text>{{ ui.n455.textContent }}</text></template><template v-else>
<button v-if="!ui.n456.removed" :class="classes(ui.n456.className)" :style="ui.n456.style" class="original-button" @tap.stop="tap('n456', $event)">
<template v-if="ui.n456.textContent !== undefined"><text>{{ ui.n456.textContent }}</text></template><template v-else>
<text>重置</text>
</template>
</button>
<button v-if="!ui.n457.removed" :class="classes(ui.n457.className)" :style="ui.n457.style" class="original-button" @tap.stop="tap('n457', $event)">
<template v-if="ui.n457.textContent !== undefined"><text>{{ ui.n457.textContent }}</text></template><template v-else>
<text>应用筛选</text>
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n458.removed" :class="classes(ui.n458.className)" :style="ui.n458.style" class="original-div" @tap.stop="tap('n458', $event)">
<template v-if="ui.n458.textContent !== undefined"><text>{{ ui.n458.textContent }}</text></template><template v-else>
<view v-if="!ui.n459.removed" :class="classes(ui.n459.className)" :style="ui.n459.style" class="original-div" @tap.stop="tap('n459', $event)">
<template v-if="ui.n459.textContent !== undefined"><text>{{ ui.n459.textContent }}</text></template><template v-else>
<view v-if="!ui.n460.removed" :class="classes(ui.n460.className)" :style="ui.n460.style" class="original-div">
<template v-if="ui.n460.textContent !== undefined"><text>{{ ui.n460.textContent }}</text></template><template v-else>
<view v-if="!ui.n461.removed" :class="classes(ui.n461.className)" :style="ui.n461.style" class="original-div">
<template v-if="ui.n461.textContent !== undefined"><text>{{ ui.n461.textContent }}</text></template><template v-else>
<image v-if="!ui.n462.removed" :class="classes(ui.n462.className)" :style="ui.n462.style" class="original-icon" :src="iconSrc('n462')" style="width:17px;height:17px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n463.removed" :class="classes(ui.n463.className)" :style="ui.n463.style" class="original-div">
<template v-if="ui.n463.textContent !== undefined"><text>{{ ui.n463.textContent }}</text></template><template v-else>
<view v-if="!ui.n464.removed" :class="classes(ui.n464.className)" :style="ui.n464.style" class="original-p">
<template v-if="ui.n464.textContent !== undefined"><text>{{ ui.n464.textContent }}</text></template><template v-else>
<text>社区访谈整理.docx</text>
</template>
</view>
<view v-if="!ui.n465.removed" :class="classes(ui.n465.className)" :style="ui.n465.style" class="original-p">
<template v-if="ui.n465.textContent !== undefined"><text>{{ ui.n465.textContent }}</text></template><template v-else>
<text>许萌 · 18 KB</text>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n466.removed" :class="classes(ui.n466.className)" :style="ui.n466.style" class="original-div">
<template v-if="ui.n466.textContent !== undefined"><text>{{ ui.n466.textContent }}</text></template><template v-else>
<button v-if="!ui.n467.removed" :class="classes(ui.n467.className)" :style="ui.n467.style" class="original-button" @tap.stop="tap('n467', $event)">
<template v-if="ui.n467.textContent !== undefined"><text>{{ ui.n467.textContent }}</text></template><template v-else>
<image v-if="!ui.n468.removed" :class="classes(ui.n468.className)" :style="ui.n468.style" class="original-icon" :src="iconSrc('n468')" style="width:16px;height:16px" mode="aspectFit" />
<view v-if="!ui.n469.removed" :class="classes(ui.n469.className)" :style="ui.n469.style" class="original-div">
<template v-if="ui.n469.textContent !== undefined"><text>{{ ui.n469.textContent }}</text></template><template v-else>
<view v-if="!ui.n470.removed" :class="classes(ui.n470.className)" :style="ui.n470.style" class="original-p">
<template v-if="ui.n470.textContent !== undefined"><text>{{ ui.n470.textContent }}</text></template><template v-else>
<text>标记闲置</text>
</template>
</view>
<view v-if="!ui.n471.removed" :class="classes(ui.n471.className)" :style="ui.n471.style" class="original-p">
<template v-if="ui.n471.textContent !== undefined"><text>{{ ui.n471.textContent }}</text></template><template v-else>
<text>超过 30 天未引用自动提示清理</text>
</template>
</view>
</template>
</view>
</template>
</button>
<button v-if="!ui.n472.removed" :class="classes(ui.n472.className)" :style="ui.n472.style" class="original-button" @tap.stop="tap('n472', $event)">
<template v-if="ui.n472.textContent !== undefined"><text>{{ ui.n472.textContent }}</text></template><template v-else>
<image v-if="!ui.n473.removed" :class="classes(ui.n473.className)" :style="ui.n473.style" class="original-icon" :src="iconSrc('n473')" style="width:16px;height:16px" mode="aspectFit" />
<view v-if="!ui.n474.removed" :class="classes(ui.n474.className)" :style="ui.n474.style" class="original-div">
<template v-if="ui.n474.textContent !== undefined"><text>{{ ui.n474.textContent }}</text></template><template v-else>
<view v-if="!ui.n475.removed" :class="classes(ui.n475.className)" :style="ui.n475.style" class="original-p">
<template v-if="ui.n475.textContent !== undefined"><text>{{ ui.n475.textContent }}</text></template><template v-else>
<text>归档隐藏</text>
</template>
</view>
<view v-if="!ui.n476.removed" :class="classes(ui.n476.className)" :style="ui.n476.style" class="original-p">
<template v-if="ui.n476.textContent !== undefined"><text>{{ ui.n476.textContent }}</text></template><template v-else>
<text>从默认列表隐藏，归档区可恢复</text>
</template>
</view>
</template>
</view>
</template>
</button>
<button v-if="!ui.n477.removed" :class="classes(ui.n477.className)" :style="ui.n477.style" class="original-button" @tap.stop="tap('n477', $event)">
<template v-if="ui.n477.textContent !== undefined"><text>{{ ui.n477.textContent }}</text></template><template v-else>
<image v-if="!ui.n478.removed" :class="classes(ui.n478.className)" :style="ui.n478.style" class="original-icon" :src="iconSrc('n478')" style="width:16px;height:16px" mode="aspectFit" />
<view v-if="!ui.n479.removed" :class="classes(ui.n479.className)" :style="ui.n479.style" class="original-div">
<template v-if="ui.n479.textContent !== undefined"><text>{{ ui.n479.textContent }}</text></template><template v-else>
<view v-if="!ui.n480.removed" :class="classes(ui.n480.className)" :style="ui.n480.style" class="original-p">
<template v-if="ui.n480.textContent !== undefined"><text>{{ ui.n480.textContent }}</text></template><template v-else>
<text>标记为冗余</text>
</template>
</view>
<view v-if="!ui.n481.removed" :class="classes(ui.n481.className)" :style="ui.n481.style" class="original-p">
<template v-if="ui.n481.textContent !== undefined"><text>{{ ui.n481.textContent }}</text></template><template v-else>
<text>可在归档区或批量清理中处理</text>
</template>
</view>
</template>
</view>
</template>
</button>
</template>
</view>
<button v-if="!ui.n482.removed" :class="classes(ui.n482.className)" :style="ui.n482.style" class="original-button" @tap.stop="tap('n482', $event)">
<template v-if="ui.n482.textContent !== undefined"><text>{{ ui.n482.textContent }}</text></template><template v-else>
<text>取消</text>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n483.removed" :class="classes(ui.n483.className)" :style="ui.n483.style" class="original-div">
<template v-if="ui.n483.textContent !== undefined"><text>{{ ui.n483.textContent }}</text></template><template v-else>
<view v-if="!ui.n484.removed" :class="classes(ui.n484.className)" :style="ui.n484.style" class="original-div">
<template v-if="ui.n484.textContent !== undefined"><text>{{ ui.n484.textContent }}</text></template><template v-else>
<view v-if="!ui.n485.removed" :class="classes(ui.n485.className)" :style="ui.n485.style" class="original-h3">
<template v-if="ui.n485.textContent !== undefined"><text>{{ ui.n485.textContent }}</text></template><template v-else>
<text>批量清理 3 项？</text>
</template>
</view>
<view v-if="!ui.n486.removed" :class="classes(ui.n486.className)" :style="ui.n486.style" class="original-p">
<template v-if="ui.n486.textContent !== undefined"><text>{{ ui.n486.textContent }}</text></template><template v-else>
<text>清理后无法恢复，建议使用「归档隐藏」保留痕迹。如确认请输入「清理」二字。</text>
</template>
</view>
<input v-if="!ui.n487.removed" :class="classes(ui.n487.className)" :style="ui.n487.style" class="original-input" :value="ui.n487.value" @input="ui.n487.value = $event.detail.value" placeholder="输入：清理" :disabled="false" :maxlength="-1" />
<view v-if="!ui.n488.removed" :class="classes(ui.n488.className)" :style="ui.n488.style" class="original-div">
<template v-if="ui.n488.textContent !== undefined"><text>{{ ui.n488.textContent }}</text></template><template v-else>
<button v-if="!ui.n489.removed" :class="classes(ui.n489.className)" :style="ui.n489.style" class="original-button" @tap.stop="tap('n489', $event)">
<template v-if="ui.n489.textContent !== undefined"><text>{{ ui.n489.textContent }}</text></template><template v-else>
<text>取消</text>
</template>
</button>
<button v-if="!ui.n490.removed" :class="classes(ui.n490.className)" :style="ui.n490.style" class="original-button" @tap.stop="tap('n490', $event)">
<template v-if="ui.n490.textContent !== undefined"><text>{{ ui.n490.textContent }}</text></template><template v-else>
<text>确认清理</text>
</template>
</button>
</template>
</view>
</template>
</view>
</template>
</view>
<view v-if="!ui.n491.removed" :class="classes(ui.n491.className)" :style="ui.n491.style" class="original-div">
<template v-if="ui.n491.textContent !== undefined"><text>{{ ui.n491.textContent }}</text></template><template v-else>
<view v-if="!ui.n492.removed" :class="classes(ui.n492.className)" :style="ui.n492.style" class="original-div">
<template v-if="ui.n492.textContent !== undefined"><text>{{ ui.n492.textContent }}</text></template><template v-else>
<view v-if="!ui.n493.removed" :class="classes(ui.n493.className)" :style="ui.n493.style" class="original-div">
<template v-if="ui.n493.textContent !== undefined"><text>{{ ui.n493.textContent }}</text></template><template v-else>
<view v-if="!ui.n494.removed" :class="classes(ui.n494.className)" :style="ui.n494.style" class="original-p">
<template v-if="ui.n494.textContent !== undefined"><text>{{ ui.n494.textContent }}</text></template><template v-else>
<text>上传资料</text>
</template>
</view>
<button v-if="!ui.n495.removed" :class="classes(ui.n495.className)" :style="ui.n495.style" class="original-button" @tap.stop="tap('n495', $event)">
<template v-if="ui.n495.textContent !== undefined"><text>{{ ui.n495.textContent }}</text></template><template v-else>
<image v-if="!ui.n496.removed" :class="classes(ui.n496.className)" :style="ui.n496.style" class="original-icon" :src="iconSrc('n496')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<button v-if="!ui.n497.removed" :class="classes(ui.n497.className)" :style="ui.n497.style" class="original-button" @tap.stop="tap('n497', $event)">
<template v-if="ui.n497.textContent !== undefined"><text>{{ ui.n497.textContent }}</text></template><template v-else>
<view v-if="!ui.n498.removed" :class="classes(ui.n498.className)" :style="ui.n498.style" class="original-div">
<template v-if="ui.n498.textContent !== undefined"><text>{{ ui.n498.textContent }}</text></template><template v-else>
<image v-if="!ui.n499.removed" :class="classes(ui.n499.className)" :style="ui.n499.style" class="original-icon" :src="iconSrc('n499')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n500.removed" :class="classes(ui.n500.className)" :style="ui.n500.style" class="original-div">
<template v-if="ui.n500.textContent !== undefined"><text>{{ ui.n500.textContent }}</text></template><template v-else>
<view v-if="!ui.n501.removed" :class="classes(ui.n501.className)" :style="ui.n501.style" class="original-p">
<template v-if="ui.n501.textContent !== undefined"><text>{{ ui.n501.textContent }}</text></template><template v-else>
<text>本地文件</text>
</template>
</view>
<view v-if="!ui.n502.removed" :class="classes(ui.n502.className)" :style="ui.n502.style" class="original-p">
<template v-if="ui.n502.textContent !== undefined"><text>{{ ui.n502.textContent }}</text></template><template v-else>
<text>文档、表格、图片、代码</text>
</template>
</view>
</template>
</view>
</template>
</button>
<button v-if="!ui.n503.removed" :class="classes(ui.n503.className)" :style="ui.n503.style" class="original-button" @tap.stop="tap('n503', $event)">
<template v-if="ui.n503.textContent !== undefined"><text>{{ ui.n503.textContent }}</text></template><template v-else>
<view v-if="!ui.n504.removed" :class="classes(ui.n504.className)" :style="ui.n504.style" class="original-div">
<template v-if="ui.n504.textContent !== undefined"><text>{{ ui.n504.textContent }}</text></template><template v-else>
<image v-if="!ui.n505.removed" :class="classes(ui.n505.className)" :style="ui.n505.style" class="original-icon" :src="iconSrc('n505')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n506.removed" :class="classes(ui.n506.className)" :style="ui.n506.style" class="original-div">
<template v-if="ui.n506.textContent !== undefined"><text>{{ ui.n506.textContent }}</text></template><template v-else>
<view v-if="!ui.n507.removed" :class="classes(ui.n507.className)" :style="ui.n507.style" class="original-p">
<template v-if="ui.n507.textContent !== undefined"><text>{{ ui.n507.textContent }}</text></template><template v-else>
<text>微信聊天文件</text>
</template>
</view>
<view v-if="!ui.n508.removed" :class="classes(ui.n508.className)" :style="ui.n508.style" class="original-p">
<template v-if="ui.n508.textContent !== undefined"><text>{{ ui.n508.textContent }}</text></template><template v-else>
<text>导入聊天中的文件</text>
</template>
</view>
</template>
</view>
</template>
</button>
<button v-if="!ui.n509.removed" :class="classes(ui.n509.className)" :style="ui.n509.style" class="original-button" @tap.stop="tap('n509', $event)">
<template v-if="ui.n509.textContent !== undefined"><text>{{ ui.n509.textContent }}</text></template><template v-else>
<view v-if="!ui.n510.removed" :class="classes(ui.n510.className)" :style="ui.n510.style" class="original-div">
<template v-if="ui.n510.textContent !== undefined"><text>{{ ui.n510.textContent }}</text></template><template v-else>
<image v-if="!ui.n511.removed" :class="classes(ui.n511.className)" :style="ui.n511.style" class="original-icon" :src="iconSrc('n511')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</view>
<view v-if="!ui.n512.removed" :class="classes(ui.n512.className)" :style="ui.n512.style" class="original-div">
<template v-if="ui.n512.textContent !== undefined"><text>{{ ui.n512.textContent }}</text></template><template v-else>
<view v-if="!ui.n513.removed" :class="classes(ui.n513.className)" :style="ui.n513.style" class="original-p">
<template v-if="ui.n513.textContent !== undefined"><text>{{ ui.n513.textContent }}</text></template><template v-else>
<text>还原上次上传</text>
</template>
</view>
<view v-if="!ui.n514.removed" :class="classes(ui.n514.className)" :style="ui.n514.style" class="original-p">
<template v-if="ui.n514.textContent !== undefined"><text>{{ ui.n514.textContent }}</text></template><template v-else>
<text>从自动存档恢复</text>
</template>
</view>
</template>
</view>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n515.removed" :class="classes(ui.n515.className)" :style="ui.n515.style" class="original-div" @tap.stop="tap('n515', $event)">
<template v-if="ui.n515.textContent !== undefined"><text>{{ ui.n515.textContent }}</text></template><template v-else>
<view v-if="!ui.n516.removed" :class="classes(ui.n516.className)" :style="ui.n516.style" class="original-div" @tap.stop="tap('n516', $event)">
<template v-if="ui.n516.textContent !== undefined"><text>{{ ui.n516.textContent }}</text></template><template v-else>
<view v-if="!ui.n517.removed" :class="classes(ui.n517.className)" :style="ui.n517.style" class="original-div">
<template v-if="ui.n517.textContent !== undefined"><text>{{ ui.n517.textContent }}</text></template><template v-else>
<view v-if="!ui.n518.removed" :class="classes(ui.n518.className)" :style="ui.n518.style" class="original-p">
<template v-if="ui.n518.textContent !== undefined"><text>{{ ui.n518.textContent }}</text></template><template v-else>
<text>项目设置</text>
</template>
</view>
<button v-if="!ui.n519.removed" :class="classes(ui.n519.className)" :style="ui.n519.style" class="original-button" @tap.stop="tap('n519', $event)">
<template v-if="ui.n519.textContent !== undefined"><text>{{ ui.n519.textContent }}</text></template><template v-else>
<image v-if="!ui.n520.removed" :class="classes(ui.n520.className)" :style="ui.n520.style" class="original-icon" :src="iconSrc('n520')" style="width:20px;height:20px" mode="aspectFit" />
</template>
</button>
</template>
</view>
<button v-if="!ui.n521.removed" :class="classes(ui.n521.className)" :style="ui.n521.style" class="original-button" @tap.stop="tap('n521', $event)">
<template v-if="ui.n521.textContent !== undefined"><text>{{ ui.n521.textContent }}</text></template><template v-else>
<image v-if="!ui.n522.removed" :class="classes(ui.n522.className)" :style="ui.n522.style" class="original-icon" :src="iconSrc('n522')" style="width:17px;height:17px" mode="aspectFit" />
<view v-if="!ui.n523.removed" :class="classes(ui.n523.className)" :style="ui.n523.style" class="original-span">
<template v-if="ui.n523.textContent !== undefined"><text>{{ ui.n523.textContent }}</text></template><template v-else>
<text>重命名项目</text>
</template>
</view>
</template>
</button>
<button v-if="!ui.n524.removed" :class="classes(ui.n524.className)" :style="ui.n524.style" class="original-button" @tap.stop="tap('n524', $event)">
<template v-if="ui.n524.textContent !== undefined"><text>{{ ui.n524.textContent }}</text></template><template v-else>
<image v-if="!ui.n525.removed" :class="classes(ui.n525.className)" :style="ui.n525.style" class="original-icon" :src="iconSrc('n525')" style="width:17px;height:17px" mode="aspectFit" />
<view v-if="!ui.n526.removed" :class="classes(ui.n526.className)" :style="ui.n526.style" class="original-span">
<template v-if="ui.n526.textContent !== undefined"><text>{{ ui.n526.textContent }}</text></template><template v-else>
<text>提醒规则</text>
</template>
</view>
</template>
</button>
<button v-if="!ui.n527.removed" :class="classes(ui.n527.className)" :style="ui.n527.style" class="original-button" @tap.stop="tap('n527', $event)">
<template v-if="ui.n527.textContent !== undefined"><text>{{ ui.n527.textContent }}</text></template><template v-else>
<image v-if="!ui.n528.removed" :class="classes(ui.n528.className)" :style="ui.n528.style" class="original-icon" :src="iconSrc('n528')" style="width:17px;height:17px" mode="aspectFit" />
<view v-if="!ui.n529.removed" :class="classes(ui.n529.className)" :style="ui.n529.style" class="original-span">
<template v-if="ui.n529.textContent !== undefined"><text>{{ ui.n529.textContent }}</text></template><template v-else>
<text>结项归档</text>
</template>
</view>
</template>
</button>
</template>
</view>
</template>
</view>
<view v-if="!ui.n530.removed" :class="classes(ui.n530.className)" :style="ui.n530.style" class="original-div">
<template v-if="ui.n530.textContent !== undefined"><text>{{ ui.n530.textContent }}</text></template><template v-else>

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
import createController from '../../controllers/project.js'
const controller = createController()
const { ui, classes, tap, iconSrc } = controller
provide('pageController', controller)
onUnload(controller.dispose)
</script>
