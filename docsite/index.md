---
layout: home

hero:
  name: Dustbin 垃圾桶
  text: 掉落物不再无声消失
  tagline: 掉落物在设定时间后自动分类进桶，随时可以捡回来。厨余 / 装备 / 矿物 / 其他，各收各的。
  actions:
    - theme: brand
      text: 快速开始
      link: /guide/getting-started
    - theme: alt
      text: 1.1.0 更新内容
      link: /releases/release-1.1.0
    - theme: alt
      text: GitHub 仓库
      link: https://github.com/AUNPP-CN/Dustbin-Mod

features:
  - icon: 🗑️
    title: 四类垃圾桶
    details: 厨余、装备、矿物、其他，判定顺序装备 → 厨余 → 矿物 → 其他，首匹配生效。
  - icon: ⏱️
    title: 可配置延迟
    details: 阈值默认 1 分钟，可调 1 ~ 1440 分钟；桶满后物品回落原版行为正常消失。
  - icon: 🏷️
    title: 数据包标签驱动
    details: 分类规则全部写在物品标签里，改标签或用数据包覆盖即可，无需重新编译。
  - icon: 💾
    title: 存档平滑升级
    details: 1.0.0 世界直接可用，原普通桶里的物品一件不丢。
---
