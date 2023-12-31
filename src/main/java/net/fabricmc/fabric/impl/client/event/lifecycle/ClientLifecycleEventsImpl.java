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

package net.fabricmc.fabric.impl.client.event.lifecycle;

import net.fabricmc.fabric.Papi;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.impl.event.lifecycle.LoadedChunksCache;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Papi.MOD_ID, value = Dist.CLIENT)
public final class ClientLifecycleEventsImpl {
	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		if (event.getLevel() instanceof ClientWorld world && event.getChunk() instanceof WorldChunk chunk) {
			((LoadedChunksCache) world).fabric_markLoaded(chunk);
		}
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if (event.getLevel() instanceof ClientWorld world && event.getChunk() instanceof WorldChunk chunk) {
			((LoadedChunksCache) world).fabric_markUnloaded(chunk);
			for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
				ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(blockEntity, world);
			}
		}
	}
}
