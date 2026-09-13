package com.minciallo.dustbin.storage;

import com.minciallo.dustbin.MinCialloDustbin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 全局的、按维度共享的垃圾桶存储。
 *
 * <p>1.1.0 起改为<b>每种垃圾桶各持有一份 54 格库存</b>
 * （{@link DustbinKind#NORMAL} / {@link DustbinKind#KITCHEN} / {@link DustbinKind#TOOL}
 * / {@link DustbinKind#MINERAL}），进桶时间仍是一个全局值。
 *
 * <p><b>存档兼容性（关键）</b>：序列化字段 {@code items} 继续表示<b>普通垃圾桶</b>的内容，
 * 1.1.0 只是新增了 {@code kitchen_items} / {@code tool_items} / {@code mineral_items}
 * 三个<b>可选</b>字段。因此用 1.0.0 存的档升到 1.1.0 后，普通桶里的东西一个都不会丢 ——
 * 读取时缺失的新字段会补成空列表。
 */
public class DustbinStorage extends SavedData {
	public static final String NAME = "dustbin_storage";
	public static final int DEFAULT_COLLECTION_TICKS = 1 * 60 * 20; // 1 minute
	public static final int MIN_COLLECTION_TICKS = 1 * 60 * 20; // 1 minute
	public static final int MAX_COLLECTION_TICKS = 1440 * 60 * 20; // 1 day (1440 minutes)

	private final Map<DustbinKind, DustbinInventory> inventories = new EnumMap<>(DustbinKind.class);
	private int collectionTicks = DEFAULT_COLLECTION_TICKS;

	private static List<ItemStack> itemsOf(DustbinStorage storage, DustbinKind kind) {
		return storage.getInventory(kind).getItems();
	}

	private static final Codec<DustbinStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ItemStack.OPTIONAL_CODEC.listOf().optionalFieldOf("items", List.<ItemStack>of())
					.forGetter(storage -> itemsOf(storage, DustbinKind.NORMAL)),
			Codec.INT.optionalFieldOf("collection_ticks", DEFAULT_COLLECTION_TICKS)
					.forGetter(DustbinStorage::getCollectionTicks),
			ItemStack.OPTIONAL_CODEC.listOf().optionalFieldOf("kitchen_items", List.<ItemStack>of())
					.forGetter(storage -> itemsOf(storage, DustbinKind.KITCHEN)),
			ItemStack.OPTIONAL_CODEC.listOf().optionalFieldOf("tool_items", List.<ItemStack>of())
					.forGetter(storage -> itemsOf(storage, DustbinKind.TOOL)),
			ItemStack.OPTIONAL_CODEC.listOf().optionalFieldOf("mineral_items", List.<ItemStack>of())
					.forGetter(storage -> itemsOf(storage, DustbinKind.MINERAL))
	).apply(instance, DustbinStorage::fromData));

	public static final SavedDataType<DustbinStorage> TYPE = new SavedDataType<>(
			MinCialloDustbin.id(NAME),
			DustbinStorage::new,
			CODEC,
			DataFixTypes.SAVED_DATA_COMMAND_STORAGE
	);

	public DustbinStorage() {
	}

	private static DustbinStorage fromData(List<ItemStack> items, int collectionTicks,
			List<ItemStack> kitchenItems, List<ItemStack> toolItems, List<ItemStack> mineralItems) {
		DustbinStorage storage = new DustbinStorage();
		fill(storage.getInventory(DustbinKind.NORMAL), items);
		fill(storage.getInventory(DustbinKind.KITCHEN), kitchenItems);
		fill(storage.getInventory(DustbinKind.TOOL), toolItems);
		fill(storage.getInventory(DustbinKind.MINERAL), mineralItems);
		storage.collectionTicks = collectionTicks;
		return storage;
	}

	private static void fill(DustbinInventory inventory, List<ItemStack> items) {
		for (int i = 0; i < items.size() && i < DustbinInventory.SLOT_COUNT; i++) {
			inventory.setItem(i, items.get(i));
		}
	}

	public static DustbinStorage get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(TYPE);
	}

	/** 指定种类的库存。首次访问时按需创建。 */
	public DustbinInventory getInventory(DustbinKind kind) {
		return inventories.computeIfAbsent(kind, ignored -> new DustbinInventory());
	}

	public int getCollectionTicks() {
		return collectionTicks;
	}

	/**
	 * 进桶时间。<b>三种桶共用一个值</b>（1.1.0 的决定），
	 * 因此 {@code /dustbin settime} 的语义与 1.0.0 完全一致。
	 */
	public void setCollectionTicks(int ticks) {
		this.collectionTicks = ticks;
		setDirty();
	}

	/**
	 * 尝试把掉落物收进<b>它所属种类</b>的桶里。
	 *
	 * <p>收集规则：每种物品只占用一个格子，每格按物品自身堆叠上限；超出上限的多余
	 * 数量会被直接丢弃。仅当存在空位、或存在同种物品且未满的格子时才收集。
	 *
	 * <p>分类桶装满时<b>不会降级</b>塞进普通桶（1.1.0 的决定）—— 保持分类纯净，
	 * 该物品退回原版消失逻辑。
	 *
	 * @return true 表示已收集（多余部分被丢弃）；false 表示该种类的桶里没有该物品的容身之处，
	 *         调用方应让物品走原版消失。
	 */
	public boolean tryCollect(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		DustbinInventory inventory = getInventory(DustbinClassifier.classify(stack));
		if (!canCollect(inventory, stack)) {
			return false;
		}
		inventory.addItem(stack.copy());
		setDirty();
		return true;
	}

	/**
	 * 该桶是否能接纳这个物品。判定与 {@code DustbinInventory.addItem} 完全一致：
	 * 已存在同种物品的格子（无论满没满 —— 溢出的部分会被丢弃），或存在任意空位。
	 */
	private static boolean canCollect(DustbinInventory inventory, ItemStack stack) {
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack slot = inventory.getItem(i);
			if (slot.isEmpty()) {
				return true;
			}
			if (ItemStack.isSameItemSameComponents(slot, stack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 清空指定种类的桶，返回移除的组数。
	 *
	 * <p>「清空全部」不在这里：命令层需要<b>逐桶</b>拿到各自的数量好分别播报
	 * （见 {@code ModCommands#clearAll}），所以由调用方遍历 {@link DustbinKind}。
	 */
	public int clearAll(DustbinKind kind) {
		DustbinInventory inventory = getInventory(kind);
		int count = 0;
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (!inventory.getItem(i).isEmpty()) {
				count++;
			}
		}
		if (count > 0) {
			inventory.clearContent();
			setDirty();
		}
		return count;
	}
}
