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

package com.teampotato.papi.mixin.event.lifecycle.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientChunkManager.class)
public abstract class ClientChunkManagerMixin {
	@Final @Shadow private ClientWorld world;

	@Redirect(method = "loadChunkFromPacket", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z", remap = false))
	private boolean onChunkLoad(IEventBus iEventBus, Event event) {
		if (!(event instanceof ChunkEvent.Load)) throw new RuntimeException();//This should not happen

		if (((ChunkEvent.Load) event).getChunk() instanceof WorldChunk) {
			ClientChunkEvents.CHUNK_LOAD.invoker().onChunkLoad(this.world, (WorldChunk) ((ChunkEvent.Load) event).getChunk());
		}

		return MinecraftForge.EVENT_BUS.post(event);
	}

	@Redirect(method = "unload", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z", remap = false))
	private boolean onChunkUnload(IEventBus iEventBus, Event event) {
		if (!(event instanceof ChunkEvent.Unload)) throw new RuntimeException();//This should not happen

		if (((ChunkEvent.Unload) event).getChunk() instanceof WorldChunk) {
			ClientChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload(this.world, (WorldChunk) ((ChunkEvent.Unload) event).getChunk());
		}

		return MinecraftForge.EVENT_BUS.post(event);
	}
}
