package com.minciallo.dustbin.registry;

import com.minciallo.dustbin.MinCialloDustbin;
import com.minciallo.dustbin.block.DustbinBlock;
import com.minciallo.dustbin.storage.DustbinKind;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class ModBlocks {
	private static final Map<DustbinKind, Block> BLOCKS = new EnumMap<>(DustbinKind.class);
	private static final Map<DustbinKind, Item> ITEMS = new EnumMap<>(DustbinKind.class);

	// 26.2 中 Block 构造链会强制要求 id 已设置（effectiveDrops 内部 requireNonNull(blockId)），
	// 因此必须在构造方块前先用 setId 预置 ResourceKey，否则会抛 "Block id not set"。
	// Item 同理（effectiveDescriptionId 内部 itemIdOrThrow）。
	//
	// 硬度 2.0（与箱子同档），requiresCorrectToolForDrops 要求用对的工具才掉落。
	// "石镐以上"这一档不是靠这里设的，而是由数据包标签决定：
	//   data/minecraft/tags/block/mineable/pickaxe.json  -> 镐类工具才有速度加成
	//   data/minecraft/tags/block/needs_stone_tool.json  -> 木镐挖了不掉落
	// 两者缺一，方块要么没有挖掘加速（裸手速度，2.0 硬度要挖 10 秒），要么被木镐挖走。
	// 三种垃圾桶共用一套属性与贴图以外的全部行为。
	static {
		for (DustbinKind kind : DustbinKind.values()) {
			ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, kind.id());
			Block block = Registry.register(
					BuiltInRegistries.BLOCK,
					blockKey,
					new DustbinBlock(kind, BlockBehaviour.Properties.of()
							.setId(blockKey)
							.strength(2.0f, 6.0f)
							.requiresCorrectToolForDrops()
							.sound(SoundType.METAL))
			);
			BLOCKS.put(kind, block);

			ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, kind.id());
			ITEMS.put(kind, Registry.register(
					BuiltInRegistries.ITEM,
					itemKey,
					new BlockItem(block, new Item.Properties().setId(itemKey))
			));
		}
	}

	/** 普通垃圾桶的方块（1.0.0 起就存在的那个，注册名保持不变）。 */
	public static final Block DUSTBIN = BLOCKS.get(DustbinKind.NORMAL);
	/** 普通垃圾桶的物品。 */
	public static final Item DUSTBIN_ITEM = ITEMS.get(DustbinKind.NORMAL);

	public static Block block(DustbinKind kind) {
		return BLOCKS.get(kind);
	}

	public static Item item(DustbinKind kind) {
		return ITEMS.get(kind);
	}

	/** 全部垃圾桶方块，供方块实体类型绑定。 */
	public static Set<Block> allBlocks() {
		return Set.copyOf(BLOCKS.values());
	}

	public static void register() {
		// Static initializers above perform the actual registration.
	}
}
