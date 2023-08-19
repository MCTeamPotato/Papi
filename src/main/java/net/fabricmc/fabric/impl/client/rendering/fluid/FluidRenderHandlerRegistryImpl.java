/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.client.rendering.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

public class FluidRenderHandlerRegistryImpl implements FluidRenderHandlerRegistry {
	/**
	 * The water color of {@link BiomeKeys#OCEAN}.
	 */
	private static final int DEFAULT_WATER_COLOR = 0x3f76e4;
	private final Map<Fluid, FluidRenderHandler> handlers = new IdentityHashMap<>();
	private final Map<Fluid, FluidRenderHandler> modHandlers = new IdentityHashMap<>();
	private final Map<Block, Boolean> overlayBlocks = new IdentityHashMap<>();

	private FluidRenderer fluidRenderer;

	public FluidRenderHandlerRegistryImpl() {
	}

	@Override
	public FluidRenderHandler get(Fluid fluid) {
		return handlers.get(fluid);
	}

	public FluidRenderHandler getOverride(Fluid fluid) {
		return modHandlers.get(fluid);
	}

	@Override
	public void register(Fluid fluid, FluidRenderHandler renderer) {
		handlers.put(fluid, renderer);
		modHandlers.put(fluid, renderer);
	}

	@Override
	public void setBlockTransparency(Block block, boolean transparent) {
		overlayBlocks.put(block, transparent);
	}

	@Override
	public boolean isBlockTransparent(Block block) {
		if (overlayBlocks.containsKey(block)) {
			return overlayBlocks.get(block);
		}
		return block instanceof TransparentBlock || block instanceof LeavesBlock;
	}

	@Override
	public boolean isBlockTransparent(BlockState state, BlockRenderView level, BlockPos pos, FluidState fluidState) {
		return overlayBlocks.computeIfAbsent(state.getBlock(), block -> block.shouldDisplayFluidOverlay(state, level, pos, fluidState));
	}

	public void onFluidRendererReload(FluidRenderer renderer, Sprite[] waterSprites, Sprite[] lavaSprites, Sprite waterOverlay) {
		fluidRenderer = renderer;

		FluidRenderHandler waterHandler = getFluidRenderHandler(waterSprites, waterOverlay);

		//noinspection Convert2Lambda
		FluidRenderHandler lavaHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return lavaSprites;
			}
		};

		register(Fluids.WATER, waterHandler);
		register(Fluids.FLOWING_WATER, waterHandler);
		register(Fluids.LAVA, lavaHandler);
		register(Fluids.FLOWING_LAVA, lavaHandler);
		handlers.putAll(modHandlers);

		SpriteAtlasTexture texture = MinecraftClient.getInstance()
				.getBakedModelManager()
				.getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

		for (FluidRenderHandler handler : handlers.values()) {
			handler.reloadTextures(texture);
		}
	}

	@NotNull
	private static FluidRenderHandler getFluidRenderHandler(Sprite[] waterSprites, Sprite waterOverlay) {
		Sprite[] waterSpritesFull = {waterSprites[0], waterSprites[1], waterOverlay};
		return new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return waterSpritesFull;
			}

			@Override
			public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
				if (view != null && pos != null) {
					return BiomeColors.getWaterColor(view, pos);
				} else {
					return DEFAULT_WATER_COLOR;
				}
			}
		};
	}

	public void renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
		fluidRenderer.render(world, pos, vertexConsumer, blockState, fluidState);
	}
}
