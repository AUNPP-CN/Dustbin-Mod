package com.minciallo.dustbin.storage;

import com.minciallo.dustbin.MinCialloDustbin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 掉落物 → 垃圾桶种类的判定。
 *
 * <p><b>判定顺序固定为 工具 → 厨余 → 普通</b>，第一个匹配上的生效，
 * 因此一个物品永远不会同时进入两个桶。
 *
 * <p>两套规则都以<b>数据包标签</b>为准（而非写死在代码里），
 * 这样整合包作者或玩家可以直接改标签来调整分类，不需要重新编译模组：
 * <ul>
 *   <li>{@code #dustbin:tool_waste} —— 默认引用原生标签：
 *       镐/锹/斧/锄/剑、矛、四件套装备，以及通用刀具 {@code #c:tools/knife}</li>
 *   <li>{@code #dustbin:kitchen_waste} —— 手工登记的「不是食物但也算厨余」的物品</li>
 * </ul>
 */
public final class DustbinClassifier {
	/**
	 * 工具桶的判定标签。
	 *
	 * <p>默认值在 {@code data/dustbin/tags/item/tool_waste.json}，内容是原生的
	 * {@code #minecraft:pickaxes} / {@code #minecraft:shovels} / {@code #minecraft:axes}
	 * / {@code #minecraft:hoes} / {@code #minecraft:swords} / {@code #minecraft:spears}
	 * / {@code #minecraft:head_armor} / {@code #minecraft:chest_armor}
	 * / {@code #minecraft:leg_armor} / {@code #minecraft:foot_armor}
	 * 以及通用刀具标签 {@code #c:tools/knife}（农夫乐事的厨刀等模组刀具会随它进来）。
	 *
	 * <p>刻意<b>不</b>用 {@code minecraft:tool} 数据组件判定：那个组件还覆盖剪子、刷子、
	 * 打火石等物品，会把它们一并收进工具桶。用标签则行为完全可预测。
	 */
	public static final TagKey<Item> TOOL_WASTE =
			TagKey.create(Registries.ITEM, MinCialloDustbin.id("tool_waste"));

	/** 厨余桶的补充标签，用于「不是食物但同样算厨余」的物品（骨粉、种子等）。 */
	public static final TagKey<Item> KITCHEN_WASTE =
			TagKey.create(Registries.ITEM, MinCialloDustbin.id("kitchen_waste"));

	private DustbinClassifier() {
	}

	/** 判定该物品属于哪一类垃圾桶。空栈归为普通桶。 */
	public static DustbinKind classify(ItemStack stack) {
		if (stack.isEmpty()) {
			return DustbinKind.NORMAL;
		}
		if (isTool(stack)) {
			return DustbinKind.TOOL;
		}
		if (isKitchenWaste(stack)) {
			return DustbinKind.KITCHEN;
		}
		return DustbinKind.NORMAL;
	}

	private static boolean isTool(ItemStack stack) {
		return stack.is(TOOL_WASTE);
	}

	/**
	 * 厨余 = 所有食物 + 手工登记的厨余。
	 *
	 * <p>食物用 {@code minecraft:food} 数据组件判定，这样<b>模组加的食物也会自动被识别</b>，
	 * 不需要逐个列举物品 id。26.2 并没有 {@code minecraft:food} 这个物品标签，
	 * 只能用组件。
	 */
	private static boolean isKitchenWaste(ItemStack stack) {
		return stack.is(KITCHEN_WASTE) || stack.has(DataComponents.FOOD);
	}
}
