package net.fabricmc.papi.impl.lifecycle;

import net.fabricmc.papi.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.papi.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LifecycleEventHooks {
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        Chunk chunk = event.getChunk();
        WorldAccess worldAccess = event.getWorld();
        if (chunk instanceof WorldChunk) {
            if (worldAccess instanceof ServerWorld) {
                ServerChunkEvents.CHUNK_LOAD.invoker().onChunkLoad((ServerWorld) worldAccess, (WorldChunk)chunk);
            } else if (worldAccess instanceof ClientWorld) {
                ClientChunkEvents.CHUNK_LOAD.invoker().onChunkLoad((ClientWorld)worldAccess, (WorldChunk)chunk);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        Chunk chunk = event.getChunk();
        WorldAccess worldAccess = event.getWorld();
        if (chunk instanceof WorldChunk) {
            if (worldAccess instanceof ServerWorld) {
                ServerChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ServerWorld) worldAccess, (WorldChunk)chunk);
            } else if (worldAccess instanceof ClientWorld) {
                ClientChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ClientWorld)worldAccess, (WorldChunk)chunk);
            }
        }
    }
}
