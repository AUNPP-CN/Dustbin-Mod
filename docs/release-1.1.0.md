# Dustbin 1.1.0

**Minecraft 26.2 ・ Fabric ・ Java 25 ・ CC BY-NC-SA 4.0**

> 掉落物不再无声消失 —— 它们会按类别滑进垃圾桶。
> Dropped items no longer vanish silently — they sort themselves into a bin.

垃圾桶从一种变成**四种**：厨余、装备、矿物，以及兜底的其他。
The bin becomes **four bins**: kitchen waste, equipment, minerals, and a catch-all.

![垃圾桶的关闭与打开状态 / Closed and open states](https://raw.githubusercontent.com/AUNPP-CN/Dustbin-Mod/Fabric/docs/preview.png)

*左：关盖 · 右：开盖（95°）。由方块模型离线渲染。*
*Left: closed · Right: open at 95°. Rendered offline from the block model.*

---

## 概述 / Overview

1.0.0 把所有掉落物收进同一个桶；1.1.0 让它们**各归其类** —— 骨头和食物进厨余桶，镐斧剑与护甲进装备桶，矿石和锭进矿物桶，其余照旧进其他桶。

In 1.0.0 every dropped item went into the same bin; 1.1.0 **sorts them** — bones and food into the Kitchen Bin, tools and armour into the Equipment Bin, ores and ingots into the Mineral Bin, and everything else into the Other Bin.

分类规则**全部写在数据包标签里**，可以直接改标签、或用数据包覆盖，**不需要重新编译模组**。
All classification lives in **datapack tags**, so you can retune it by editing a tag or overriding it from a datapack — **no recompiling needed**.

四种桶依然是**兜底机制，不是无限仓库** —— 对应种类的桶满了以后，物品回落到原版行为正常消失，**不会串到别的桶里**。

Each bin is still a **safety net, not unlimited storage** — once the bin an item belongs to is full, the item falls back to vanilla despawn and is **never diverted into another bin**.

---

## 本版亮点 / What's in 1.1.0

- **四类垃圾桶，各收各的**：判定顺序 **装备 → 厨余 → 矿物 → 其他**，第一个匹配的生效。
  **Four bins, each with its own domain**: the order is **equipment → kitchen → minerals → everything else**, first match wins.
- **厨余垃圾桶**：骨头、骨粉、各类种子、腐肉、蜘蛛眼、毒马铃薯、甜菜根、蛋糕，**以及任何带食物组件的物品** —— 所以模组加的食物无需逐个适配就会自动进来。「放下才能吃」的食物方块（寿司拼盘、各类整块派）与动物饲料（**马食**、狗粮）也在其列。
  **Kitchen Bin**: bone, bone meal, seeds, rotten flesh, spider eye, poisonous potato, beetroot, cake, **plus anything carrying the food component** — so modded food is picked up with no per-mod work. Food blocks you must place before eating (sushi platters, whole pies) and animal feed (**horse feed**, dog food) are included too.
- **装备垃圾桶**（1.0.0 里叫「工具垃圾桶」）：镐、锹、斧、锄、剑、矛、**盾牌与三叉戟**，以及四件套护甲；通用刀具标签让各类厨刀一并归入。剪子、打火石、刷子、钓鱼竿**不算**装备，仍进其他桶。
  **Equipment Bin** (called the "Tool Bin" in 1.0.0): pickaxes, shovels, axes, hoes, swords, spears, **shields and tridents**, plus all four armour slots and knives from the shared tool tag. Shears, flint and steel, brushes and fishing rods are **not** equipment and stay in the Other Bin.
- **矿物垃圾桶**：八类矿石（煤/铜/铁/金/钻石/绿宝石/青金石/红石）、下界石英矿石、原矿与粗矿块、煤炭与木炭、金属粒、铜块（含氧化与涂蜡变种）、各类锭（铁/金/铜/下界合金）、下界合金碎片、钻石、绿宝石、青金石、红石、下界石英、紫水晶碎片、远古残骸，以及矿物块。**不收加工品** —— 铜门、铜台阶、红石灯、石英楼梯这类建材仍进其他桶。
  **Mineral Bin**: the eight ore types (coal / copper / iron / gold / diamond / emerald / lapis / redstone), nether quartz ore, raw ores and raw ore blocks, coal and charcoal, metal nuggets, copper blocks (including oxidised and waxed variants), every ingot (iron / gold / copper / netherite), netherite scrap, diamond, emerald, lapis lazuli, redstone, nether quartz, amethyst shards, ancient debris and the mineral blocks. **Crafted goods are excluded** — copper doors, copper stairs, redstone lamps and quartz stairs stay in the Other Bin.
- **专属创造栏「更多的垃圾桶 / More Bins」**：四种桶集中在一处，不再散落在原版「建筑方块」栏。
  **Dedicated creative tab, More Bins**: all four bins in one place instead of scattered through vanilla's Building Blocks tab.
- **成就「回收再利用！/ Recycle!」**：首次合成任意一种垃圾桶时解锁（`goal` 级，有弹窗与聊天播报）。
  **Advancement, Recycle!**: unlocked the first time you craft any bin (goal frame, with toast and chat announcement).
- **老存档平滑升级**：1.0.0 存下的世界直接可用，**原普通桶里的物品一件不丢**。
  **Smooth upgrade from old saves**: worlds saved by 1.0.0 keep working, and **nothing in the original bin is lost**.

---

## 四类垃圾桶 / The four bins

| 垃圾桶 Bin | 收什么 What it collects | 中心材料 Core item |
| --- | --- | --- |
| **其他垃圾桶 / Other Bin** | 兜底 —— 其它三类都不匹配的物品。Anything left over. | 箱子 / Chest |
| **厨余垃圾桶 / Kitchen Bin** | 食物与厨余。Food and kitchen scraps. | 骨头 / Bone |
| **装备垃圾桶 / Equipment Bin** | 工具、武器、护甲。Tools, weapons and armour. | 铁镐 / Iron Pickaxe |
| **矿物垃圾桶 / Mineral Bin** | 矿石、原矿、锭、宝石、矿物块。Ores, raw materials, ingots, gems and mineral blocks. | 铁块 / Iron Block |

跨模组引用（通用刀具标签、通用食物标签、农夫乐事的饲料等）全部标为**可选**，所以装或不装那些模组**都不会导致标签加载失败**。
Every cross-mod reference (shared tool tags, shared food tags, Farmer's Delight's feed, …) is marked **optional**, so they cannot break tag loading whether or not those mods are present.

---

## 合成 / Crafting

四种桶摆法相同 —— 8 个铁锭围住 1 个中心材料，只有中心材料不同。
All four bins share the same shape — 8 iron ingots around one core item; only the core differs.

```
I I I
I C I
I I I
```

`I` = 铁锭 / Iron Ingot

---

## 指令 / Commands

| 指令 Command | 作用 Description |
| --- | --- |
| `/dustbin clear` | 清空**全部四个桶**，并**逐个桶**报告各自清掉了几组。Empty every bin, reporting each bin's own count. |
| `/dustbin clear all` | 同上，显式写法。The same thing, spelled out. |
| `/dustbin clear <other\|kitchen\|equipment\|mineral>` | 只清空指定的**那一个**桶。<br>`normal`（= other）与 `tool`（= equipment）是等效别名，两种写法都认。Empty just that one bin; `normal` and `tool` are accepted aliases. |
| `/dustbin settime <minutes>` | 设置收集阈值，范围 1 ~ 1440 分钟；所有桶**共用一个值**。Set the collection threshold (1–1440 minutes), shared by all bins. |

单人世界的房主可直接使用；多人服务器需要管理员权限。
Singleplayer hosts can use them directly; multiplayer servers require admin permission.

---

## 升级说明 / Upgrading from 1.0.0

直接把 1.1.0 的 jar 换进 `mods/` 即可，**存档不需要任何处理**。
Just swap in the 1.1.0 jar — **the save needs no conversion**.

- 1.0.0 的「垃圾桶」在 1.1.0 里显示为「**其他垃圾桶**」，**注册名未变**，因此世界中已有的桶、桶里的物品、以及其它模组对它的引用全部照旧。
  The 1.0.0 bin now displays as **Other Bin**; its registry name is unchanged, so existing blocks, their contents and any references from other mods all keep working.
- 存储格式只**新增**了可选字段，旧字段含义不变；缺失的新字段读入时自动补空。
  The storage format only **gains** optional fields; existing fields keep their meaning, and missing ones default to empty on load.
- **注意**：`mod id` 相同，两个版本**不能同时**放进 `mods/` 目录。
  **Note**: the mod id is identical, so the two versions **must not** sit in `mods/` at the same time.

---

## 安装 / Installation

1. 安装 **Fabric Loader ≥ 0.19.3** 与 **Fabric API**。
   Install **Fabric Loader ≥ 0.19.3** and **Fabric API**.
2. 把 `dustbin-fabric-1.1.0.jar` 放进 `mods/` 目录。
   Put `dustbin-fabric-1.1.0.jar` into your `mods/` folder.
3. 需要 **Java 25** 运行时。
   Requires a **Java 25** runtime.

模组身份标识（mod id）为 `dustbin`，**不随模组端变化**；只有产物文件名带模组端后缀。这样将来从 Fabric 构建切换到其他模组端时，存档里的垃圾桶与桶内物品可以原样保留。
The mod id is `dustbin` and **stays the same across loaders**; only the artifact filename carries a loader suffix. That way, moving a world to another loader keeps every bin and its contents intact.

---

## 兼容性 / Compatibility

| 项目 Component | 要求 Requirement |
| --- | --- |
| Minecraft | 26.2 |
| Fabric Loader | ≥ 0.19.3 |
| Fabric API | 必需 / required |
| Java | ≥ 25 |

Fabric 构建通过 Mixin 注入 `ItemEntity#tick`。与其他同样改写掉落物消失逻辑的模组同时使用时**可能冲突**，建议实测后再大规模投入。
The Fabric build mixes into `ItemEntity#tick`. It **may conflict** with other mods that rewrite item despawn behaviour — test before shipping it in a pack.

---

## 已知限制 / Known limitations

- **物品年龄只在所在区块被加载时增长。** 长期未加载的区块里的掉落物不会"到点进桶"，直到有人靠近。
  **Item age only advances while the chunk is loaded.** Items in unloaded chunks are not collected until a player comes back.
- **开盖角度 95°，垂直占用超出方块本身。** 按模型几何实测：盖顶伸到方块顶面**上方约 0.37 格**，因此正上方紧贴方块时必然穿模；盖沿向后探出约 **0.035 格**，把角度压到 85° 以内即可消除。但即便压到 60°，盖顶仍有约 0.33 格高 —— 开盖的垂直占用无法靠调角度规避，只能靠留空解决。
  **The lid opens to 95°**, so it occupies space above the block. Measured from the geometry: the lid top reaches about **0.37 blocks above** the block's top face, so a block directly overhead is always clipped; the tab juts roughly **0.035 blocks** rearward, which disappears below 85°. Even at 60° the lid still stands about 0.33 blocks tall — the vertical footprint of an open lid cannot be designed away by lowering the angle, only accommodated by leaving headroom.
- **分类桶装满不降级。** 对应种类的桶没有空位时，物品**不会**被塞进其他桶，而是回落到原版消失逻辑 —— 这是为了保持分类干净。
  **A full bin never downgrades.** If the bin an item belongs to has no room, the item is **not** diverted into another bin; it falls back to vanilla despawn, keeping each bin's contents predictable.
- **存储按维度保存在世界数据里。** 移除本模组后，数据文件仍留在存档中但不再被读取。
  **Storage is saved per dimension** in the world data. If you remove the mod, the file stays in the save but is never read again.

---

## 产物校验 / Artifact verification

建议下载后比对校验值，确认文件完整。
Please verify the checksum after downloading.

```
文件 / File : dustbin-fabric-1.1.0.jar
大小 / Size : 320,265 bytes
MD5         : e2eab1528577f17a3ed0a0b02511b0a5
SHA-256     : 4b80402c2a49d891e43d0656e2234a9af40f3d38f228dce4161f8eb8c93f0aea
```

---

## 许可证 / License

[CC BY-NC-SA 4.0](https://github.com/AUNPP-CN/Dustbin-Mod/blob/Fabric/LICENSE) —— 允许使用、修改与再分发，但**必须署名**、**不得用于商业用途**，且衍生作品需以相同协议共享。

[CC BY-NC-SA 4.0](https://github.com/AUNPP-CN/Dustbin-Mod/blob/Fabric/LICENSE) — use, adaptation and redistribution are permitted, provided you **give attribution**, **do not use it commercially**, and **share derivatives under the same license**.

欢迎提交 Issue 与 Pull Request。 / Issues and pull requests are welcome.
