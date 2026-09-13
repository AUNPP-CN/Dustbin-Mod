package com.minciallo.dustbin.registry;

import com.minciallo.dustbin.MinCialloDustbin;
import com.minciallo.dustbin.block.DustbinBlockEntity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
	/**
	 * 三种垃圾桶共用同一个方块实体类型 —— 渲染器与菜单界面都按类型注册，
	 * 因此新增种类时客户端无需任何改动（贴图差异由模型 JSON 承担）。
	 */
	public static final BlockEntityType<DustbinBlockEntity> DUSTBIN = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			MinCialloDustbin.id("dustbin"),
			new BlockEntityType<>(DustbinBlockEntity::new, ModBlocks.allBlocks())
	);

	public static void register() {
		// Static initializers above perform the actual registration.
	}
}
