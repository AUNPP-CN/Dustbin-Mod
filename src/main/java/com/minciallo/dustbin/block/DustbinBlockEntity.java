package com.minciallo.dustbin.block;

import com.minciallo.dustbin.menu.DustbinMenu;
import com.minciallo.dustbin.registry.ModBlockEntities;
import com.minciallo.dustbin.storage.DustbinInventory;
import com.minciallo.dustbin.storage.DustbinKind;
import com.minciallo.dustbin.storage.DustbinStorage;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity for the trash bin. It does not store items itself; it acts as a
 * portal to the global shared storage ({@link DustbinStorage}).
 *
 * <p>箱子属于哪一种由所在方块决定（{@link DustbinBlock#getKind()}），
 * 因此同一个方块实体类型可以为三种垃圾桶服务。
 */
public class DustbinBlockEntity extends BlockEntity implements ExtendedMenuProvider<BlockPos> {
	public DustbinBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DUSTBIN, pos, state);
	}

	/** 所在方块的垃圾桶种类。 */
	public DustbinKind getKind() {
		return getBlockState().getBlock() instanceof DustbinBlock dustbin
				? dustbin.getKind()
				: DustbinKind.NORMAL;
	}

	/**
	 * Sets the lid open/closed state. Server-side only: the resulting block state
	 * change is what gets synced to clients, where the renderer drives the animation.
	 */
	public void setOpen(boolean open) {
		if (level == null || level.isClientSide()) {
			return;
		}
		BlockState state = getBlockState();
		if (!state.hasProperty(DustbinBlock.OPEN) || state.getValue(DustbinBlock.OPEN) == open) {
			return;
		}
		level.setBlock(worldPosition, state.setValue(DustbinBlock.OPEN, open), Block.UPDATE_ALL);
	}

	@Override
	public Component getDisplayName() {
		// 界面标题即方块名：三种桶各自返回自己的 key，菜单与界面无需感知种类。
		return Component.translatable(getKind().containerTranslationKey());
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayer player) {
		return this.worldPosition;
	}

	@Override
	public DustbinMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return new DustbinMenu(containerId, playerInventory, getStorageInventory(), this.worldPosition);
	}

	/**
	 * The shared storage inventory on the server; a fresh inventory on the client
	 * (used only for the GUI layout, slot contents are synced from the server).
	 */
	private Container getStorageInventory() {
		if (level instanceof ServerLevel serverLevel) {
			return DustbinStorage.get(serverLevel).getInventory(getKind());
		}
		return new DustbinInventory();
	}
}
