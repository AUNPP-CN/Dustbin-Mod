package com.minciallo.dustbin;

import com.minciallo.dustbin.registry.ModBlockEntities;
import com.minciallo.dustbin.registry.ModBlocks;
import com.minciallo.dustbin.registry.ModCommands;
import com.minciallo.dustbin.registry.ModCreativeTabs;
import com.minciallo.dustbin.registry.ModMenuTypes;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinCialloDustbin implements ModInitializer {
	public static final String MOD_ID = "dustbin";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.register();
		ModBlockEntities.register();
		ModMenuTypes.register();
		ModCommands.register();

		// 三种垃圾桶统一放进本模组自己的「更多的垃圾桶」创造栏，
		// 而不是散落在原版「建筑方块」栏里。
		ModCreativeTabs.register();

		LOGGER.info("Dustbin loaded!");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
