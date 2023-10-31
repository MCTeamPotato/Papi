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

package net.fabricmc.fabric.impl.event.lifecycle;

import net.fabricmc.fabric.Papi;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Papi.MOD_ID, value = Dist.DEDICATED_SERVER)
public final class LifecycleEventsImpl {
	@SubscribeEvent
	public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
		if (event.getLevel() instanceof ServerWorld world) {
			ServerEntityEvents.ENTITY_UNLOAD.invoker().onUnload(event.getEntity(), world);
		}
	}

	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
		if (event.getLevel() instanceof ServerWorld world) {
			ServerEntityEvents.ENTITY_LOAD.invoker().onLoad(event.getEntity(), world);
		}
	}

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		if (event.getLevel() instanceof ServerWorld world && event.getChunk() instanceof WorldChunk chunk) {
			((LoadedChunksCache) world).fabric_markLoaded(chunk);
		}
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if (event.getLevel() instanceof ServerWorld world && event.getChunk() instanceof WorldChunk chunk) {
			((LoadedChunksCache) world).fabric_markUnloaded(chunk);
			for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
				ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(blockEntity, world);
			}
		}
	}

	@SubscribeEvent
	public static void onWorldUnload(LevelEvent.Unload event) {
		if (event.getLevel() instanceof ServerWorld world) {
			for (WorldChunk chunk : ((LoadedChunksCache) world).fabric_getLoadedChunks()) {
				for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
					ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(blockEntity, world);
				}
			}

			for (Entity entity : world.iterateEntities()) {
				ServerEntityEvents.ENTITY_UNLOAD.invoker().onUnload(entity, world);
			}
		}
	}
}
