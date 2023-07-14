package net.fabricmc.fabric.mixin.papi.lifecycle_chunk_event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.papi.util.MixinUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.event.world.ChunkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class MixinChunkEvent {
    @Mixin(value = ChunkEvent.Load.class, remap = false)
    public static abstract class MixinLoad extends ChunkEvent {
        public MixinLoad(Chunk chunk) {
            super(chunk);
        }

        @Inject(method = "<init>", at = @At("RETURN"))
        private void onInit(CallbackInfo callbackInfo) {
            WorldAccess worldAccess = this.getWorld();
            Chunk chunk = this.getChunk();
            int side = MixinUtil.checkSide(worldAccess, chunk);
            if (side == 1) {
                ServerChunkEvents.CHUNK_LOAD.invoker().onChunkLoad((ServerWorld) worldAccess, (WorldChunk) chunk);
            } else if (side == 2) {
                ClientChunkEvents.CHUNK_LOAD.invoker().onChunkLoad((ClientWorld) worldAccess, (WorldChunk) chunk);
            }
        }
    }

    @Mixin(value = ChunkEvent.Unload.class, remap = false)
    public abstract static class MixinUnload extends ChunkEvent {
        public MixinUnload(Chunk chunk) {
            super(chunk);
        }

        @Inject(method = "<init>", at = @At("RETURN"))
        private void onInit(CallbackInfo callbackInfo) {
            WorldAccess worldAccess = this.getWorld();
            Chunk chunk = this.getChunk();
            int side = MixinUtil.checkSide(worldAccess, chunk);
            if (side == 1) {
                ServerChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ServerWorld) worldAccess, (WorldChunk) chunk);
            } else if (side == 2) {
                ClientChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ClientWorld) worldAccess, (WorldChunk) chunk);
            }
        }
    }
}
