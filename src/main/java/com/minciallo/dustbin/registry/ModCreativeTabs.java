package com.minciallo.dustbin.registry;

import com.minciallo.dustbin.MinCialloDustbin;
import com.minciallo.dustbin.storage.DustbinKind;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * 本模组自己的创造模式物品栏「更多的垃圾桶」。
 *
 * <p>三种垃圾桶集中在这一栏里，不混进原版的「建筑方块」栏 —— 玩家在创造模式背包里
 * 一眼就能找到全部垃圾桶。
 *
 * <p>静态初始化即完成注册。Fabric 的创造栏 API 会按命名空间把模组标签页归到一处，
 * 因此不需要额外指定插入位置。
 */
public class ModCreativeTabs {
	public static final ResourceKey<CreativeModeTab> BINS_KEY =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, MinCialloDustbin.id("bins"));

	public static final CreativeModeTab BINS = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			BINS_KEY,
			FabricCreativeModeTab.builder()
					.title(Component.translatable("itemGroup.dustbin.bins"))
					// 用普通垃圾桶当标签页图标：它是这个模组的代表方块。
					.icon(() -> new ItemStack(ModBlocks.item(DustbinKind.NORMAL)))
					.displayItems((parameters, output) -> {
						for (DustbinKind kind : DustbinKind.values()) {
							output.accept(ModBlocks.item(kind));
						}
					})
					.build()
	);

	public static void register() {
		// The static initializer above performs the actual registration.
	}

	private ModCreativeTabs() {
	}
}
