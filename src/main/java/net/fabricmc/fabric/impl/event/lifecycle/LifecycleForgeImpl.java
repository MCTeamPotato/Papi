package net.fabricmc.fabric.impl.event.lifecycle;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public final class LifecycleForgeImpl {
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.@NotNull Load event) {
        WorldAccess world = event.getLevel();
        Chunk chunk = event.getChunk();
        if (chunk instanceof WorldChunk worldChunk) {
            if (world instanceof ClientWorld clientWorld) {
                ClientChunkEvents.CHUNK_LOAD.invoker().onChunkLoad(clientWorld, worldChunk);
            } else if (world instanceof ServerWorld serverWorld) {
                ServerChunkEvents.CHUNK_LOAD.invoker().onChunkLoad(serverWorld, worldChunk);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.@NotNull Unload event) {
        WorldAccess world = event.getLevel();
        Chunk chunk = event.getChunk();
        if (chunk instanceof WorldChunk worldChunk) {
            if (world instanceof ClientWorld clientWorld) {
                ClientChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload(clientWorld, worldChunk);
            } else if (world instanceof ServerWorld serverWorld) {
                ServerChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload(serverWorld, worldChunk);
            }
        }
    }

    @SubscribeEvent
    public static void onTagsLoaded(@NotNull TagsUpdatedEvent event) {
        CommonLifecycleEvents.TAGS_LOADED.invoker().onTagsLoaded(event.getRegistryAccess(), event.getUpdateCause().equals(TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED));
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.@NotNull LevelTickEvent event) {
        World world = event.level;
        TickEvent.Phase phase = event.phase;
        if (phase == TickEvent.Phase.START) {
            if (world instanceof ClientWorld clientWorld) {
                ClientTickEvents.START_WORLD_TICK.invoker().onStartTick(clientWorld);
            } else if (world instanceof ServerWorld serverWorld) {
                ServerTickEvents.START_WORLD_TICK.invoker().onStartTick(serverWorld);
            }
        } else if (phase == TickEvent.Phase.END) {
            if (world instanceof ClientWorld clientWorld) {
                ClientTickEvents.END_WORLD_TICK.invoker().onEndTick(clientWorld);
            } else if (world instanceof ServerWorld serverWorld) {
                ServerTickEvents.END_WORLD_TICK.invoker().onEndTick(serverWorld);
            }
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(@NotNull LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.world.isClient) return;
        ServerEntityEvents.EQUIPMENT_CHANGE.invoker().onChange(entity, event.getSlot(), event.getFrom(), event.getTo());
    }

    @SubscribeEvent
    public static void onServerStarting(@NotNull ServerStartingEvent event) {
        ServerLifecycleEvents.SERVER_STARTING.invoker().onServerStarting(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStarted(@NotNull ServerStartedEvent event) {
        ServerLifecycleEvents.SERVER_STARTED.invoker().onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(@NotNull ServerStoppingEvent event) {
        ServerLifecycleEvents.SERVER_STOPPING.invoker().onServerStopping(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(@NotNull ServerStoppedEvent event) {
        ServerLifecycleEvents.SERVER_STOPPED.invoker().onServerStopped(event.getServer());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.@NotNull ServerTickEvent event) {
        MinecraftServer server = event.getServer();
        TickEvent.Phase phase = event.phase;
        if (phase == TickEvent.Phase.START) {
            ServerTickEvents.START_SERVER_TICK.invoker().onStartTick(server);
        } else if (phase == TickEvent.Phase.END) {
            ServerTickEvents.END_SERVER_TICK.invoker().onEndTick(server);
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.@NotNull Load event) {
        if (event.getLevel() instanceof ServerWorld serverWorld) ServerWorldEvents.LOAD.invoker().onWorldLoad(serverWorld.getServer(), serverWorld);
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.@NotNull Unload event) {
        if (event.getLevel() instanceof ServerWorld serverWorld) ServerWorldEvents.UNLOAD.invoker().onWorldUnload(serverWorld.getServer(), serverWorld);
    }

    @SubscribeEvent
    public static void onDataPackSync(@NotNull OnDatapackSyncEvent event) {
        ServerPlayerEntity player = event.getPlayer();
        if (player == null) {
            for (ServerPlayerEntity serverPlayerEntity : event.getPlayerList().getPlayerList()) {
                ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.invoker().onSyncDataPackContents(serverPlayerEntity, false);
            }
        } else {
            ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.invoker().onSyncDataPackContents(player, true);
        }
    }

    public static final class Client {
        @SubscribeEvent
        public static void onClientTick(TickEvent.@NotNull ClientTickEvent event) {
            TickEvent.Phase phase = event.phase;
            final MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (phase == TickEvent.Phase.START) {
                ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick(minecraftClient);
            } else if (phase == TickEvent.Phase.END) {
                ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick(minecraftClient);
            }
        }
    }
}
