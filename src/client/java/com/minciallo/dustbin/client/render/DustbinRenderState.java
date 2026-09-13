package com.minciallo.dustbin.client.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

/**
 * Per-frame snapshot handed to {@link DustbinBlockEntityRenderer}.
 */
public class DustbinRenderState extends BlockEntityRenderState {
	/** Lid opening progress: 0 = fully closed, 1 = fully open. */
	public float openness;

	/**
	 * Horizontal facing of the block, copied from {@code DustbinBlock.FACING}.
	 * The lid hinge sits on the opposite side, so the bin always opens away from
	 * whoever placed it.
	 */
	public Direction facing = Direction.SOUTH;

	/**
	 * Lid textures for the bin being rendered.
	 *
	 * <p>Resolved per block entity, because every kind of bin has its own lid art
	 * ({@code dustbin_lid_top} vs {@code kitchen_dustbin_lid_top} …). These are
	 * <b>full</b> identifiers — with the {@code textures/} prefix and the {@code .png}
	 * suffix — see the note on the constants in {@link DustbinBlockEntityRenderer}.
	 */
	public Identifier lidTop;
	public Identifier lidSide;
	public Identifier lidHandle;
}
