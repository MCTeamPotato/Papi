package net.fabricmc.fabric.papi.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

public class MixinUtil {
    public static final int SERVER = 1;
    public static final int CLIENT = 2;
    public static final int NONE = 0;

    public static int checkSide(WorldAccess worldAccess, Chunk chunk) {
        if (chunk instanceof WorldChunk) {
            if (worldAccess instanceof ServerWorld) return SERVER;
            if (worldAccess instanceof ClientWorld) return CLIENT;
        }
        return NONE;
    }
}