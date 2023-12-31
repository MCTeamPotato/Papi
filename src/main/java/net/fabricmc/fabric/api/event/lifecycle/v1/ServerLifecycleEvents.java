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
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ServerLifecycleEvents {
	private ServerLifecycleEvents() {
	}

	/**
	 * @deprecated Use {@link net.minecraftforge.event.server.ServerStartingEvent}
	 */
	@Deprecated(forRemoval = true)
	public static final Event<ServerStarting> SERVER_STARTING = EventFactory.createArrayBacked(ServerStarting.class, callbacks -> server -> {
		for (ServerStarting callback : callbacks) {
			callback.onServerStarting(server);
		}
	});

	/**
	 * @deprecated Use {@link net.minecraftforge.event.server.ServerStartedEvent}
	 */
	@Deprecated(forRemoval = true)
	public static final Event<ServerStarted> SERVER_STARTED = EventFactory.createArrayBacked(ServerStarted.class, (callbacks) -> (server) -> {
		for (ServerStarted callback : callbacks) {
			callback.onServerStarted(server);
		}
	});

	/**
	 * @deprecated Use {@link net.minecraftforge.event.server.ServerStoppingEvent}
	 */
	@Deprecated(forRemoval = true)
	public static final Event<ServerStopping> SERVER_STOPPING = EventFactory.createArrayBacked(ServerStopping.class, (callbacks) -> (server) -> {
		for (ServerStopping callback : callbacks) {
			callback.onServerStopping(server);
		}
	});

	/**
	 * @deprecated Use {@link net.minecraftforge.event.server.ServerStoppedEvent}
	 */
	@Deprecated(forRemoval = true)
	public static final Event<ServerStopped> SERVER_STOPPED = EventFactory.createArrayBacked(ServerStopped.class, callbacks -> server -> {
		for (ServerStopped callback : callbacks) {
			callback.onServerStopped(server);
		}
	});

	/**
	 * @deprecated Use {@link net.minecraftforge.event.OnDatapackSyncEvent}
	 */
	@Deprecated(forRemoval = true)
	public static final Event<SyncDataPackContents> SYNC_DATA_PACK_CONTENTS = EventFactory.createArrayBacked(SyncDataPackContents.class, callbacks -> (player, joined) -> {
		for (SyncDataPackContents callback : callbacks) {
			callback.onSyncDataPackContents(player, joined);
		}
	});

	/**
	 * Called before a Minecraft server reloads data packs.
	 */
	public static final Event<StartDataPackReload> START_DATA_PACK_RELOAD = EventFactory.createArrayBacked(StartDataPackReload.class, callbacks -> (server, serverResourceManager) -> {
		for (StartDataPackReload callback : callbacks) {
			callback.startDataPackReload(server, serverResourceManager);
		}
	});

	/**
	 * Called after a Minecraft server has reloaded data packs.
	 *
	 * <p>If reloading data packs was unsuccessful, the current data packs will be kept.
	 */
	public static final Event<EndDataPackReload> END_DATA_PACK_RELOAD = EventFactory.createArrayBacked(EndDataPackReload.class, callbacks -> (server, serverResourceManager, success) -> {
		for (EndDataPackReload callback : callbacks) {
			callback.endDataPackReload(server, serverResourceManager, success);
		}
	});

	@FunctionalInterface
	public interface ServerStarting {
		void onServerStarting(MinecraftServer server);
	}

	@FunctionalInterface
	public interface ServerStarted {
		void onServerStarted(MinecraftServer server);
	}

	@FunctionalInterface
	public interface ServerStopping {
		void onServerStopping(MinecraftServer server);
	}

	@FunctionalInterface
	public interface ServerStopped {
		void onServerStopped(MinecraftServer server);
	}

	@FunctionalInterface
	public interface SyncDataPackContents {
		/**
		 * Called right before tags and recipes are sent to a player,
		 * either because the player joined, or because the server reloaded resources.
		 * The {@linkplain MinecraftServer#getResourceManager() server resource manager} is up-to-date when this is called.
		 *
		 * <p>For example, this event can be used to sync data loaded with custom resource reloaders.
		 *
		 * @param player Player to which the data is being sent.
		 * @param joined True if the player is joining the server, false if the server finished a successful resource reload.
		 */
		void onSyncDataPackContents(ServerPlayerEntity player, boolean joined);
	}

	@FunctionalInterface
	public interface StartDataPackReload {
		void startDataPackReload(MinecraftServer server, LifecycledResourceManager resourceManager);
	}

	@FunctionalInterface
	public interface EndDataPackReload {
		/**
		 * Called after data packs on a Minecraft server have been reloaded.
		 *
		 * <p>If the reload was not successful, the old data packs will be kept.
		 *
		 * @param server the server
		 * @param resourceManager the resource manager
		 * @param success if the reload was successful
		 */
		void endDataPackReload(MinecraftServer server, LifecycledResourceManager resourceManager, boolean success);
	}
}
