package net.fabricmc.fabric.impl.lifecycle;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class LifecycleForgeImpl {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.@NotNull WorldTickEvent event) {
        TickEvent.Phase phase = event.phase;
        if (event.world instanceof ServerWorld) {
            if (phase == TickEvent.Phase.END) {
                ServerTickEvents.END_WORLD_TICK.invoker().onEndTick((ServerWorld) event.world);
            } else if (phase == TickEvent.Phase.START) {
                ServerTickEvents.START_WORLD_TICK.invoker().onStartTick((ServerWorld) event.world);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStarting(@NotNull FMLServerStartingEvent event) {
        ServerLifecycleEvents.SERVER_STARTING.invoker().onServerStarting(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStarted(@NotNull FMLServerStartingEvent event) {
        ServerLifecycleEvents.SERVER_STARTED.invoker().onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(@NotNull FMLServerStoppingEvent event) {
        ServerLifecycleEvents.SERVER_STOPPING.invoker().onServerStopping(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(@NotNull FMLServerStoppingEvent event) {
        ServerLifecycleEvents.SERVER_STOPPED.invoker().onServerStopped(event.getServer());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.@NotNull ServerTickEvent event) {
        TickEvent.Phase phase = event.phase;
        if (phase == TickEvent.Phase.START) {
            ServerTickEvents.START_SERVER_TICK.invoker().onStartTick(ServerLifecycleHooks.getCurrentServer());
        } else if (phase == TickEvent.Phase.END) {
            ServerTickEvents.END_SERVER_TICK.invoker().onEndTick(ServerLifecycleHooks.getCurrentServer());
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.@NotNull Load event) {
        if (event.getWorld() instanceof ServerWorld) {
            ServerWorldEvents.LOAD.invoker().onWorldLoad(((ServerWorld) event.getWorld()).getServer(), (ServerWorld) event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.@NotNull Unload event) {
        if (event.getWorld() instanceof ServerWorld) {
            ServerWorldEvents.UNLOAD.invoker().onWorldUnload(((ServerWorld) event.getWorld()).getServer(), (ServerWorld) event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.@NotNull Unload event) {
        if (event.getChunk() instanceof WorldChunk && event.getWorld() instanceof ServerWorld) {
            ServerChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ServerWorld) event.getWorld(), (WorldChunk) event.getChunk());
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.@NotNull Load event) {
        if (event.getChunk() instanceof WorldChunk && event.getWorld() instanceof ServerWorld) {
            ServerChunkEvents.CHUNK_LOAD.invoker().onChunkLoad((ServerWorld) event.getWorld(), (WorldChunk) event.getChunk());
        }
    }

    public static class Client {
        @SubscribeEvent
        public static void onChunkLoad(ChunkEvent.@NotNull Load event) {
            if (event.getChunk() instanceof WorldChunk && event.getWorld() instanceof ClientWorld) {
                ClientChunkEvents.CHUNK_LOAD.invoker().onChunkLoad((ClientWorld) event.getWorld(), (WorldChunk) event.getChunk());
            }
        }

        @SubscribeEvent
        public static void onChunkUnload(ChunkEvent.@NotNull Unload event) {
            if (event.getChunk() instanceof WorldChunk && event.getWorld() instanceof ClientWorld) {
                ClientChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ClientWorld) event.getWorld(), (WorldChunk) event.getChunk());
            }
        }

        @SubscribeEvent
        public static void onWorldTick(TickEvent.@NotNull WorldTickEvent event) {
            TickEvent.Phase phase = event.phase;
            if (event.world instanceof ClientWorld){
                if (phase == TickEvent.Phase.END) {
                    ClientTickEvents.END_WORLD_TICK.invoker().onEndTick((ClientWorld) event.world);
                } else if (phase == TickEvent.Phase.START) {
                    ClientTickEvents.START_WORLD_TICK.invoker().onStartTick((ClientWorld) event.world);
                }
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.@NotNull ClientTickEvent event) {
            TickEvent.Phase phase = event.phase;
            if (phase == TickEvent.Phase.START) {
                ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick(MinecraftClient.getInstance());
            } else if (phase == TickEvent.Phase.END) {
                ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick(MinecraftClient.getInstance());
            }
        }
    }
}
