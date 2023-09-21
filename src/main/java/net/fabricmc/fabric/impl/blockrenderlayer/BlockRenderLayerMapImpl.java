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

package net.fabricmc.fabric.impl.blockrenderlayer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.mixin.blockrenderlayer.RenderLayersAccessor;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;

public class BlockRenderLayerMapImpl implements BlockRenderLayerMap {
	public BlockRenderLayerMapImpl() { }

	@Override
	public void putBlock(Block block, RenderLayer renderLayer) {
		if (block == null) throw new IllegalArgumentException("Request to map null block to BlockRenderLayer");
		if (renderLayer == null) throw new IllegalArgumentException("Request to map block " + block + " to null BlockRenderLayer");

		blockHandler.accept(block, renderLayer);
	}

	@Override
	public void putBlocks(RenderLayer renderLayer, Block @NotNull ... blocks) {
		for (Block block : blocks) {
			putBlock(block, renderLayer);
		}
	}

	@Override
	public void putItem(Item item, RenderLayer renderLayer) {
		if (item == null) throw new IllegalArgumentException("Request to map null item to BlockRenderLayer");
		if (renderLayer == null) throw new IllegalArgumentException("Request to map item " + item + " to null BlockRenderLayer");

		itemHandler.accept(item, renderLayer);
	}

	@Override
	public void putItems(RenderLayer renderLayer, Item @NotNull ... items) {
		for (Item item : items) {
			putItem(item, renderLayer);
		}
	}

	@Override
	public void putFluid(Fluid fluid, RenderLayer renderLayer) {
		if (fluid == null) throw new IllegalArgumentException("Request to map null fluid to BlockRenderLayer");
		if (renderLayer == null) throw new IllegalArgumentException("Request to map fluid " + fluid + " to null BlockRenderLayer");

		fluidHandler.accept(fluid, renderLayer);
	}

	@Override
	public void putFluids(RenderLayer renderLayer, Fluid @NotNull ... fluids) {
		for (Fluid fluid : fluids) {
			putFluid(fluid, renderLayer);
		}
	}

	private static final Map<Block, RenderLayer> blockRenderLayerMap = new Object2ObjectOpenHashMap<>();
	private static final Map<Item, RenderLayer> itemRenderLayerMap = new Object2ObjectOpenHashMap<>();
	private static final Map<Fluid, RenderLayer> fluidRenderLayerMap = new Object2ObjectOpenHashMap<>();

	//This consumers initially add to the maps above, and then are later set (when initialize is called) to insert straight into the target map.
	private static BiConsumer<Block, RenderLayer> blockHandler = blockRenderLayerMap::put;
	private static BiConsumer<Item, RenderLayer> itemHandler = itemRenderLayerMap::put;
	private static BiConsumer<Fluid, RenderLayer> fluidHandler = fluidRenderLayerMap::put;

	public static void initialize(BiConsumer<Block, RenderLayer> blockHandlerIn, BiConsumer<Fluid, RenderLayer> fluidHandlerIn) {
		//Done to handle backwards compat, in previous snapshots Items had their own map for render layers, now the BlockItem is used.
		BiConsumer<Item, RenderLayer> itemHandlerIn = (item, renderLayer) -> blockHandlerIn.accept(Block.getBlockFromItem(item), renderLayer);

		//Add all the pre-existing render layers
		blockRenderLayerMap.forEach(blockHandlerIn);
		itemRenderLayerMap.forEach(itemHandlerIn);
		fluidRenderLayerMap.forEach(fluidHandlerIn);

		//Set the handlers to directly accept later additions
		blockHandler = blockHandlerIn;
		itemHandler = itemHandlerIn;
		fluidHandler = fluidHandlerIn;
	}

	public static void initRenderLayers(@NotNull FMLClientSetupEvent event) {
		event.enqueueWork(() -> initialize((block, renderLayer) -> {
			Map<RegistryEntry.Reference<Block>, ChunkRenderTypeSet> blockRenderTypes = RenderLayersAccessor.getBlockRenderTypes();
			synchronized (blockRenderTypes) {
				blockRenderTypes.put(ForgeRegistries.BLOCKS.getDelegateOrThrow(block), ChunkRenderTypeSet.of(renderLayer));
			}
		}, RenderLayers::setRenderLayer));
	}
}
