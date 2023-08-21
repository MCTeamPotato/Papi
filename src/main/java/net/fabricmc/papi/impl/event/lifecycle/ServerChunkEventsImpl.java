package net.fabricmc.papi.impl.event.lifecycle;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerChunkEventsImpl {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onChunkUnload(ChunkEvent.Unload event) {
        Chunk chunk = event.getChunk();
        if (chunk instanceof WorldChunk) {
            WorldAccess world = event.getWorld();
            if (world instanceof ServerWorld) {
                ServerChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ServerWorld) world, (WorldChunk) chunk);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onChunkLoad(ChunkEvent.Load event) {
        Chunk chunk = event.getChunk();
        if (chunk instanceof WorldChunk) {
            WorldAccess world = event.getWorld();
            if (world instanceof ServerWorld) {
                ServerChunkEvents.CHUNK_LOAD.invoker().onChunkLoad((ServerWorld) world, (WorldChunk) chunk);
            }
        }
    }
}
