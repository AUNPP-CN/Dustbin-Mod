package com.minciallo.dustbin.storage;

import com.minciallo.dustbin.MinCialloDustbin;

import net.minecraft.resources.Identifier;

/**
 * 垃圾桶的种类。
 *
 * <p>每一种都拥有自己独立的 54 格存储，且沿用「每维度一份」的语义
 * （主世界各类桶各一份、下界各类桶各一份，互不相通）。
 *
 * <p>{@link #path} 同时充当方块/物品的注册名后缀与语言文件 key 的后缀。
 * {@link #NORMAL} 的 path 是 {@code dustbin}，与 1.0.0 的注册名
 * {@code dustbin:dustbin} 完全一致 —— <b>老存档里的桶和物品都靠这个对齐，不能改。</b>
 */
public enum DustbinKind {
	/** 普通垃圾桶，兜底收所有未被其它桶匹配的物品。 */
	NORMAL("dustbin"),
	/** 厨余垃圾桶：食物（按 {@code minecraft:food} 组件判定）与手工登记的厨余。 */
	KITCHEN("kitchen_dustbin"),
	/** 装备垃圾桶：镐 / 锹 / 斧 / 锄 / 剑 / 矛 / 四件套装备 / 厨刀等。 */
	TOOL("tool_dustbin"),
	/** 矿物垃圾桶：矿石、原矿、锭、宝石、粒与矿物块。 */
	MINERAL("mineral_dustbin");

	private final String path;

	DustbinKind(String path) {
		this.path = path;
	}

	/** 注册名后缀，如 {@code kitchen_dustbin}。 */
	public String path() {
		return path;
	}

	/** 完整的注册 id，如 {@code dustbin:kitchen_dustbin}。 */
	public Identifier id() {
		return MinCialloDustbin.id(path);
	}

	public String blockTranslationKey() {
		return "block.dustbin." + path;
	}

	public String itemTranslationKey() {
		return "item.dustbin." + path;
	}

	/** 打开界面时的标题 key。 */
	public String containerTranslationKey() {
		return "container.dustbin." + path;
	}
}
