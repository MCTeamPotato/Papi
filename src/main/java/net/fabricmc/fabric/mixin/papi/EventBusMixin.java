package net.fabricmc.fabric.mixin.papi;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EventBus.class, remap = false)
public class EventBusMixin {
    @Inject(method = "post(Lnet/minecraftforge/eventbus/api/Event;)Z", at = @At("HEAD"))
    private void onPost(Event event, CallbackInfoReturnable<Boolean> cir) {
        if (event instanceof ChunkEvent.Unload) {
            WorldAccess worldAccess = ((ChunkEvent.Unload) event).getWorld();
            Chunk chunk = ((ChunkEvent.Unload) event).getChunk();
            if (canPost(worldAccess, chunk)) {
                ServerChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ServerWorld) worldAccess, (WorldChunk) chunk);
            }
        } else if (event instanceof ChunkEvent.Load) {
            WorldAccess worldAccess = ((ChunkEvent.Load) event).getWorld();
            Chunk chunk = ((ChunkEvent.Load) event).getChunk();
            if (canPost(worldAccess, chunk)) {
                ServerChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ServerWorld) worldAccess, (WorldChunk) chunk);
            }
        }
    }

    @Unique
    private boolean canPost(WorldAccess worldAccess, Chunk chunk) {
        return worldAccess instanceof ServerWorld && chunk instanceof WorldChunk;
    }
}
