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

package net.fabricmc.fabric.api.event.lifecycle.v1;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public final class ServerWorldEvents {
	/**
	 * @deprecated Use {@link net.minecraftforge.event.level.LevelEvent.Load}
	 */
	@Deprecated(forRemoval = true)
	public static final Event<Load> LOAD = EventFactory.createArrayBacked(Load.class, callbacks -> (server, world) -> {
		for (Load callback : callbacks) {
			callback.onWorldLoad(server, world);
		}
	});

	/**
	 * @deprecated Use {@link net.minecraftforge.event.level.LevelEvent.Unload}
	 */
	@Deprecated(forRemoval = true)
	public static final Event<Unload> UNLOAD = EventFactory.createArrayBacked(Unload.class, callbacks -> (server, world) -> {
		for (Unload callback : callbacks) {
			callback.onWorldUnload(server, world);
		}
	});

	@FunctionalInterface
	public interface Load {
		void onWorldLoad(MinecraftServer server, ServerWorld world);
	}

	@FunctionalInterface
	public interface Unload {
		void onWorldUnload(MinecraftServer server, ServerWorld world);
	}

	private ServerWorldEvents() {
	}
}
