package net.fabricmc.fabric.papi.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

public class MixinUtil {
    public static int checkSide(WorldAccess worldAccess, Chunk chunk) {
        if (chunk instanceof WorldChunk) {
            if (worldAccess instanceof ServerWorld) return 1;
            if (worldAccess instanceof ClientWorld) return 2;
        }
        return 0;
    }
}
