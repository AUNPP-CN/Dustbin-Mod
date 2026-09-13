# Dustbin 1.1.0 需求与实施清单

> 状态：**已全部实施**（分类规则几经追加，最终形态见 2.4 与第九节）  
> 版本：`1.1.0` ｜ tag 将使用 `26.2-Fabric-1.1.0` ｜ 直接在 `Fabric` 分支开发  
> 目标：新增「厨余垃圾桶」「工具垃圾桶」两类分类垃圾桶，未匹配的物品仍进原有的普通垃圾桶。
>
> ⚠️ **本文档是开发中途的设计快照，不要当作最终规格**：此后又追加了
> ①「工具垃圾桶」改名 **装备垃圾桶**、②第四类 **矿物垃圾桶**，以及把马食等动物饲料归入厨余。
> **最终形态以 `docs/release-1.1.0.md` 为准。**
>
> 已完成：框架 / 注册接线 / 资源与数据文件 / 18 张占位贴图 / 标签离线验证 / 四类入桶端到端验证 /
> 部署到 mods / README 与发行说明更新。
> 未完成：游戏内实测（客户端 UI）、推送。**本文档记录设计与决策，不再要求逐条确认。**



---

## 一、需求概述

| 垃圾桶       | 注册名                       | 收集内容                | 贴图             |
| --------- | ------------------------- | ------------------- | -------------- |
| 普通垃圾桶（现有） | `dustbin:dustbin`         | 兜底：所有未被前两类匹配的物品     | 已有，不动          |
| 厨余垃圾桶（新增） | `dustbin:kitchen_dustbin` | 食物、骨粉等你指定的「厨余」      | **你手绘**，我先出占位图 |
| 工具垃圾桶（新增） | `dustbin:tool_dustbin`    | 镐 / 锄 / 斧 / 剑（可能含锹） | **你手绘**，我先出占位图 |

三类各自独立拥有 **54 格**存储，且沿用「**每维度一份**」的现有语义  
（即：主世界三种桶各一份、下界三种桶各一份，互不相通）。

---

## 二、分类判定规则

判定按固定顺序进行，**第一个匹配上的分类生效**：

```
工具 → 厨余 → 普通（兜底）
```

### 2.1 工具类 —— 已实测确认可用的判定依据

**实测结论（读取游戏本体 `26.2-Fabric 0.19.3.jar` 的 `data/minecraft/tags/item/`）**：  
26.2 原生**存在**以下 5 个工具标签：

- `minecraft:pickaxes`
- `minecraft:axes`
- `minecraft:swords`
- `minecraft:hoes`
- `minecraft:shovels`

**但原生不存在 `minecraft:tools`** —— 它不覆盖全部工具，不能只引用它。

同样实测确认：游戏本体存在数据组件 **`minecraft:tool`** 与 **`minecraft:weapon`**  
（`DataComponents.TOOL` / `DataComponents.WEAPON`），可用于识别模组工具。

**最终采用方案 C（标签驱动）** —— 判定完全由 `#dustbin:tool_waste` 标签决定，
默认引用原生标签，需要扩充时改标签即可，不必重新编译：

| 引用 | 覆盖 |
|---|---|
| `#minecraft:pickaxes` / `shovels` / `axes` / `hoes` / `swords` | 各类工具，各 7 种材质 |
| `#minecraft:spears` | **矛**（26.2 新增，共 7 种） |
| `#minecraft:head_armor` / `chest_armor` / `leg_armor` / `foot_armor` | 四件套装备 |
| `#c:tools/knife`（**可选**） | 通用刀具标签 → 农夫乐事 6 把刀 + MrCrayfish 家具 1 把 |

刻意**不**用 `minecraft:tool` 组件：它还会覆盖剪子、刷子、打火石，会把它们一并收进来。
实测确认剪子 / 打火石 / 刷子 / 钓鱼竿 / 狼铠 / 马铠**均不在**上述标签内。

### 2.2 厨余类 —— 存在技术障碍，需你确认口径

**实测结论：26.2 原生没有 `minecraft:food` 物品标签**，无法用标签直接表达「所有食物」。

**但实测确认数据组件 `minecraft:food` 存在**（`DataComponents.FOOD` → `FoodProperties`），  
因此可以用组件判定「是不是食物」，**且模组食物会自动被覆盖**，不需要逐个列举。

**推荐组合方案：**

```
是食物（含 minecraft:food 组件）  或  在 #dustbin:kitchen_waste 标签内
```

- 「是食物」→ 组件判定，零维护，覆盖全部原版与模组食物
- `#dustbin:kitchen_waste` 自建标签 → 用于补充**不是食物但你想收**的东西（骨粉就在这一类）

**需要你给的清单：** 除骨粉外，还想收哪些非食物物品？  
（候选：种子、小麦、腐烂的肉、蜘蛛眼、毒马铃薯、甜菜根、鸡蛋、海带、南瓜……）

### 2.3 兜底

未匹配前两类的物品 → 普通垃圾桶（现有行为不变）。

---

## 三、需要你决策的边界问题

### 3.1 分类桶满了怎么办？

普通桶目前的行为是：桶满 → 该物品退回原版 5 分钟消失逻辑。

分类桶满时，两种策略：

| 策略              | 行为      | 后果                        |
| --------------- | ------- | ------------------------- |
| **A. 不降级（我倾向）** | 直接走原版消失 | 分类保持纯净，但物品真的没了            |
| **B. 降级到普通桶**   | 塞进普通桶   | 不丢东西，但普通桶会被工具/厨余污染，违背分类初衷 |

### 3.2 进桶时间是否分桶设置？

现有 `/dustbin settime <分钟>` 是**全局一个值**（当前默认 1 分钟）。  
三种桶共用同一个时间？还是各自独立？

### 3.3 `/dustbin clear` 的作用范围

- `/dustbin clear` → 清空全部三类？还是只清当前所站维度的那一类？
- 建议：`/dustbin clear [normal|kitchen|tool]`，不带参数时清空全部。

### 3.4 工具类是否包含「锹（shovels）」？

你原话列了「镐子 锄头 斧头 剑等」，`等` 字未明确。  
原生 `minecraft:shovels` 标签是现成的，**要不要一起收**？

### 3.5 新桶的合成配方用什么材料？

现有普通桶配方：铁锭 ×8 围一圈 + 箱子 ×1。

新桶可以：

- 复用同一配方（只是产出不同）—— 简单
- 普通桶 + 一个标识物（如厨余桶 = 普通桶 + 骨头 / 工具桶 = 普通桶 + 铁镐）—— 更有层次
- 或者你直接给材料清单

---

## 四、存档兼容性（关键，不能出错）

| 项                      | 处理                                    |
| ---------------------- | ------------------------------------- |
| `mod id`               | **保持 `dustbin` 不变**（写进存档的注册名，改了老存档全丢） |
| 现有方块 `dustbin:dustbin` | **保持注册名不变**，它就是「普通垃圾桶」，老存档里的桶不受影响     |
| 存档数据 `dustbin_storage` | Codec **只新增可选字段**，老存档读入时新字段自动补空       |

具体做法：现有 `items` 字段**继续表示普通垃圾桶的内容**，新增 `kitchen_items`、  
`tool_items` 两个可选字段。这样升级 mod 后，老存档里普通桶的东西**一个都不会丢**。

---

## 五、代码改动清单

| 文件                                                  | 改动性质     | 说明                                                     |
| --------------------------------------------------- | -------- | ------------------------------------------------------ |
| `DustbinKind.java`                                  | **新增**   | 枚举 `NORMAL / KITCHEN / TOOL`，含显示名 key、存储字段名            |
| `DustbinClassifier.java`                            | **新增**   | 物品 → 分类的判定逻辑（标签 + 组件，见第二节）                             |
| `DustbinStorage.java`                               | 改造       | 由「单桶」改为「三类桶」，Codec 新增两个可选字段，`getInventory(kind)`       |
| `DustbinInventory.java`                             | 大概率不动    | 仍是 54 格容器，改成多实例复用                                      |
| `ModBlocks.java`                                    | 扩充       | 新增 2 个方块 + 2 个物品注册（沿用现有 `setId` 预置写法）                  |
| `ModBlockEntities.java`                             | 扩充       | 同一 BE 类型绑定 3 个方块                                       |
| `DustbinBlockEntity.java`                           | 改造       | `getStorageInventory()` 与 `getDisplayName()` 按 kind 取值 |
| `ItemEntityDespawnMixin.java`                       | 改造       | 先分类，再写入对应桶                                             |
| `ModCommands.java`                                  | 扩充       | `clear` 支持指定分类（见 3.3）                                  |
| `DustbinMenu` / `DustbinScreen` / `TakeOnlySlot`    | 预计不动     | 若菜单标题需按分类变化，则改 `getDisplayName` 即可                     |
| `DustbinBlockEntityRenderer` / `DustbinRenderState` | **预计不动** | 新桶沿用同一几何形状，贴图差异由模型 JSON 承担                             |

---

## 六、资源文件清单（每个新桶 × 2）

### 6.1 需新增的文件（每桶 5 个 JSON + 1 个 blockstate）

| 文件                                            | 来源                           |
| --------------------------------------------- | ---------------------------- |
| `assets/dustbin/blockstates/<bin>.json`       | 复制现有，8 个变体（4 朝向 × 开关），仅改模型引用 |
| `assets/dustbin/models/block/<bin>_body.json` | 复制现有，改 4 个贴图引用（桶身，无盖）        |
| `assets/dustbin/models/block/<bin>.json`      | 复制现有，改 7 个贴图引用（含盖，物品用）       |
| `assets/dustbin/models/item/<bin>.json`       | `parent` 指向 `<bin>` 方块模型     |
| `assets/dustbin/items/<bin>.json`             | 物品模型定义，指向 `<bin>` 方块模型       |
| `data/dustbin/loot_table/blocks/<bin>.json`   | 复制现有，改掉落物 id                 |
| `data/dustbin/recipe/<bin>.json`              | 配方，材料待定（见 3.5）               |

### 6.2 需修改的共享文件

| 文件                                                | 改动                                            |
| ------------------------------------------------- | --------------------------------------------- |
| `data/minecraft/tags/block/mineable/pickaxe.json` | 加入两个新方块 id                                    |
| `data/minecraft/tags/block/needs_stone_tool.json` | 加入两个新方块 id                                    |
| `assets/dustbin/lang/zh_cn.json`                  | 新增 `block.*` / `item.*` / `container.*` 各 2 条 |
| `assets/dustbin/lang/en_us.json`                  | 同上（英文）                                        |
| 新增 `data/dustbin/tags/item/`（若采用方案 C / 厨余标签）      | 自建分类标签                                        |

---

## 七、贴图交付规格（**你手绘的接口约定**）

**规格：128 × 128 像素，PNG，RGBA（带透明通道）。**  
注意不是 16×16 —— 现有贴图全部是 128×128，你按 16 像素画会糊。

**每个垃圾桶需要 6 张**，共 **12 张**：

| # | 文件名                    | 用途   | 说明            |
| - | ---------------------- | ---- | ------------- |
| 1 | `<bin>_side.png`       | 桶身四面 | 主视觉，最显眼的一张    |
| 2 | `<bin>_bottom.png`     | 桶底   | 平时看不到，可简单处理   |
| 3 | `<bin>_interior.png`   | 桶内壁  | **开盖后可见**，值得画 |
| 4 | `<bin>_lid_top.png`    | 盖子顶面 | 俯视时最显眼        |
| 5 | `<bin>_lid_side.png`   | 盖子四周 | 盖子侧边          |
| 6 | `<bin>_lid_handle.png` | 盖子提手 | 顶部小凸起         |

其中 `<bin>` 为 `kitchen_dustbin` 与 `tool_dustbin`。

**你可以自由决定的东西：**

- 贴图文件名/前缀 —— 你按自己习惯命名也行，告诉我，我来对齐 JSON 引用
- 画成什么风格 —— 只需保证「一眼能区分三类」即可（区分度靠主色最有效）

**我会先做的：** 两张纯色/带文字标记的占位贴图，让功能可以立刻进游戏验证，  
你之后直接用同名文件覆盖即可，**不需要改任何代码**。

---

## 八、实施顺序建议

| 批次        | 内容                                   | 可验证性   |
| --------- | ------------------------------------ | ------ |
| **第 1 批** | 分类框架 + 两个新桶注册（含占位贴图）→ 能放置、能开盖、能收对应物品 | 可进游戏实测 |
| **第 2 批** | 指令适配、语言文件、配方、战利品表、挖掘标签               | 可进游戏实测 |
| **第 3 批** | README / 发行说明更新、版本号改 `1.1.0`、构建发布    | 发布流程   |

先做第 1 批就能看出分类对不对，避免后面返工。

---

## 九、已确认的决策（2026-09-13）

| #   | 决策项    | 结果                                                          |
| --- | ------ | ----------------------------------------------------------- |
| 1   | 工具类判定  | **方案 C**：数据包标签 `#dustbin:tool_waste`，默认引用原生 5 个工具标签           |
| 2   | 工具类范围  | 镐 / 锹 / 斧 / 锄 / 剑；**不含**剪子、钓鱼竿（避开 `minecraft:tool` 组件的连带影响） |
| 3   | 厨余的非食物 | 骨头、骨粉、各类种子、腐肉、蜘蛛眼、毒马铃薯、甜菜根                                  |
| 4   | 分类桶满了  | **不降级**，退回原版消失（保持分类纯净）                                      |
| 5   | 新桶注册名  | `dustbin:kitchen_dustbin` / `dustbin:tool_dustbin`          |
| 6   | 合成配方   | 与原桶同构（铁锭 ×8 + 中心特征物）；厨余中心放骨头，工具中心放铁镐                         |

另两项：

- **进桶时间**：三类**共用**一个全局值，`/dustbin settime` 语义与 1.0.0 完全一致
- **`/dustbin clear`**：支持可选分类参数 `normal` / `kitchen` / `tool`，不带参数时清空全部三类

### 实施记录

- 第 1、2 批已一并完成：分类框架 + 注册接线 + 全部资源与数据文件 + 12 张占位贴图。
- 分类规则全部落在**数据包标签**上，调整分类不需要重新编译模组。
- 食物判定用 `minecraft:food` 数据组件，因此**模组食物也会被自动识别**。
- 存档兼容：序列化字段 `items` 继续表示普通垃圾桶，仅新增 `kitchen_items` / `tool_items` 两个**可选**字段。
- 刻意不改的部分：渲染器、菜单、界面（三种桶共用几何与菜单，差异由模型 JSON 承担）。

---

## 十、追加功能（2026-09-13 傍晚，用户追加）

### 10.1 专属创造模式物品栏「更多的垃圾桶」

| 项 | 值 |
|---|---|
| 标签页标题 | `itemGroup.dustbin.bins` → **更多的垃圾桶** / **More Bins** |
| 注册 id | `dustbin:bins`（`Registries.CREATIVE_MODE_TAB`） |
| 图标 | 普通垃圾桶（`dustbin:dustbin`） |
| 内容 | `DustbinKind.values()` 顺序 —— 其他 / 厨余 / 工具 三个桶 |
| 实现 | `registry/ModCreativeTabs.java`，静态初始化即注册 |

- 三种桶**从原版「建筑方块」栏移出**，只在新标签页里出现（避免重复）
- 用的是 Fabric 的 `FabricCreativeModeTab.builder()`；它会按命名空间把模组标签页归组，
  不需要自己指定插入位置。若标签页超出一页，Fabric 会自动加分页箭头。
- 注册走原版的 `Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceKey, tab)`

### 10.2 成就「回收再利用！」

| 项 | 值 |
|---|---|
| 文件 | `data/dustbin/advancement/recycle.json`（**单数** `advancement` 目录） |
| id | `dustbin:recycle` |
| 标题 | `advancements.dustbin.recycle.title` → **回收再利用！** / **Recycle!** |
| 描述 | `...description` → 合成你的第一个垃圾桶 / Craft your first bin |
| 图标 | `dustbin:dustbin` ｜ `frame`: `goal` |
| 触发 | `minecraft:recipe_crafted` |
| 父进度 | `minecraft:story/root` |

**「合成任意一种即可」是怎么表达的**：三个 criteria（分别对应三个配方）放进
`requirements` 的**同一个组**里 —— 组内是「或」，组间是「与」：

```json
"requirements": [["craft_dustbin", "craft_kitchen_dustbin", "craft_tool_dustbin"]]
```

**关于父进度**：`minecraft:story/root` 的达成条件就是「拿到工作台」，而合成垃圾桶
本身就需要工作台，所以父进度必然先于本成就达成 —— 不会出现节点被遮挡（显示 `???`）的情况。

> 若更希望它独占一个标签页，可改为「自建根进度 + `display.background`」，
> 代价是进度界面会多出一个只有单个节点的标签页。
