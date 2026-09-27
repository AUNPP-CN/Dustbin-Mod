# 数据包扩展（第三方接入）

这个模组**没有对外的代码 API**——它不暴露给其他模组调用的类或接口。它能被定制和扩展的方式只有一种：**数据包物品标签**。这也是唯一需要了解的「接口」。

## 三个分类标签

| 标签 | 路径（数据包内） | 作用 |
| --- | --- | --- |
| `dustbin:tool_waste` | `data/dustbin/tags/item/tool_waste.json` | 进装备桶 |
| `dustbin:kitchen_waste` | `data/dustbin/tags/item/kitchen_waste.json` | 进厨余桶 |
| `dustbin:mineral_waste` | `data/dustbin/tags/item/mineral_waste.json` | 进矿物桶 |

三个标签都不匹配的物品进其他桶。判定顺序 **装备 → 厨余 → 矿物 → 其他**，首匹配生效。

模组自带的定义在模组 jar 内的同名路径下，数据包里写同名标签即可**整体覆盖**。

## 模组自带定义（摘要）

**tool_waste**：`#minecraft:pickaxes` `#minecraft:shovels` `#minecraft:axes` `#minecraft:hoes` `#minecraft:swords` `#minecraft:spears` `#minecraft:head_armor` `#minecraft:chest_armor` `#minecraft:leg_armor` `#minecraft:foot_armor`，加上 `minecraft:shield` `minecraft:trident`，以及可选的 `#c:tools/knife`。

**kitchen_waste**：骨头、骨粉、各类种子、腐肉、蜘蛛眼、毒马铃薯、甜菜根、蛋糕、`#minecraft:horse_food`，加上可选的 `#c:foods/edible_when_placed`（放下才能吃的食物方块）与农夫乐事动物饲料。**另有代码级兜底：任何带食物组件（food component）的物品都进厨余桶**，所以模组食物无需登记。

**mineral_waste**：八类矿石物品标签、原矿与粗矿块、煤炭与木炭、金属粒、铜块系列、各类锭、下界合金碎片、钻石、绿宝石、青金石、红石、下界石英、紫水晶碎片、远古残骸及矿物块。**不含加工建材**。

## 示例：用数据包把物品划进某个桶

在数据包里放 `data/dustbin/tags/item/kitchen_waste.json`：

```json
{
  "values": [
    "minecraft:bone",
    "minecraft:rotten_flesh",
    "mycustommod:custom_food"
  ]
}
```

::: warning 覆盖是整体替换
同名标签会**整体替换**模组自带定义，不是追加。所以上例必须把想保留的原有条目一起写上（骨头、腐肉……），漏写的就不再进厨余桶。
:::

## 铁律：跨模组条目必须标 `required: false`

引用**其他模组**的物品或标签时，必须写成对象形式：

```json
{
  "values": [
    {
      "id": "#c:tools/knife",
      "required": false
    }
  ]
}
```

标签条目的 `required` 默认是 `true`——一旦那个模组不存在，解析失败会导致**整条标签被丢弃**（不是跳过这一条），该桶分类直接失效。模组自带定义里的所有跨模组引用都标了 `required: false`，所以装不装对应模组都不影响标签加载。

::: tip 其他模组想适配这个垃圾桶？
同样走标签：把你模组的物品 id 加进上述任一标签即可（或让用户用数据包加）。厨余桶例外——只要你模组的食物带标准 food 组件，无需任何登记自动进桶。
:::
