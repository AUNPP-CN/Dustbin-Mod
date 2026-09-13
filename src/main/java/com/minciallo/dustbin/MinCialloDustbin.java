package com.minciallo.dustbin;

import com.minciallo.dustbin.registry.ModBlockEntities;
import com.minciallo.dustbin.registry.ModBlocks;
import com.minciallo.dustbin.registry.ModCommands;
import com.minciallo.dustbin.registry.ModMenuTypes;
import com.minciallo.dustbin.storage.DustbinKind;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

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

		// Add all three trash bins to the vanilla "Building Blocks" creative tab.
		CreativeModeTabEvents.modifyOutputEvent(
				ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("building_blocks")))
				.register(output -> {
					for (DustbinKind kind : DustbinKind.values()) {
						output.accept(ModBlocks.item(kind));
					}
				});

		LOGGER.info("Dustbin loaded!");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
