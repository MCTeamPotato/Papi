package net.fabricmc.fabric.impl.event.lifecycle;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public final class LifecycleForgeImpl {
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.@NotNull Load event) {
        Chunk chunk = event.getChunk();
        WorldAccess world = event.getWorld();
        if (chunk instanceof WorldChunk) {
            if (world instanceof ServerWorld) {
                ServerChunkEvents.CHUNK_LOAD.invoker().onChunkLoad((ServerWorld) world, (WorldChunk) chunk);
            } else if (world instanceof ClientWorld) {
                ClientChunkEvents.CHUNK_LOAD.invoker().onChunkLoad((ClientWorld) world, (WorldChunk) chunk);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.@NotNull Unload event) {
        Chunk chunk = event.getChunk();
        WorldAccess world = event.getWorld();
        if (chunk instanceof WorldChunk) {
            if (world instanceof ServerWorld) {
                ServerChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ServerWorld) world, (WorldChunk) chunk);
            } else if (world instanceof ClientWorld) {
                ClientChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ClientWorld) world, (WorldChunk) chunk);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStarting(@NotNull FMLServerStartingEvent event) {
        ServerLifecycleEvents.SERVER_STARTING.invoker().onServerStarting(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStarted(@NotNull FMLServerStartedEvent event) {
        ServerLifecycleEvents.SERVER_STARTED.invoker().onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(@NotNull FMLServerStoppingEvent event) {
        ServerLifecycleEvents.SERVER_STOPPING.invoker().onServerStopping(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(@NotNull FMLServerStoppedEvent event) {
        ServerLifecycleEvents.SERVER_STOPPED.invoker().onServerStopped(event.getServer());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.@NotNull ServerTickEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        TickEvent.Phase phase = event.phase;
        if (phase == TickEvent.Phase.START) {
            ServerTickEvents.START_SERVER_TICK.invoker().onStartTick(server);
        } else if (phase == TickEvent.Phase.END) {
            ServerTickEvents.END_SERVER_TICK.invoker().onEndTick(server);
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(WorldEvent.@NotNull Load event) {
        WorldAccess world = event.getWorld();
        if (world instanceof ServerWorld) {
            ServerWorldEvents.LOAD.invoker().onWorldLoad(((ServerWorld) world).getServer(), (ServerWorld)world);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(WorldEvent.@NotNull Unload event) {
        WorldAccess world = event.getWorld();
        if (world instanceof ServerWorld) {
            ServerWorldEvents.UNLOAD.invoker().onWorldUnload(((ServerWorld) world).getServer(), (ServerWorld)world);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.@NotNull WorldTickEvent event) {
        World world = event.world;
        TickEvent.Phase phase = event.phase;
        if (phase == TickEvent.Phase.START) {
            if (world instanceof ClientWorld) {
                ClientTickEvents.END_WORLD_TICK.invoker().onEndTick((ClientWorld)world);
            } else if (world instanceof ServerWorld) {
                ServerTickEvents.END_WORLD_TICK.invoker().onEndTick((ServerWorld)world);
            }
        } else if (phase == TickEvent.Phase.END) {
            if (world instanceof ClientWorld) {
                ClientTickEvents.START_WORLD_TICK.invoker().onStartTick((ClientWorld)world);
            } else if (world instanceof ServerWorld) {
                ServerTickEvents.START_WORLD_TICK.invoker().onStartTick((ServerWorld)world);
            }
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
