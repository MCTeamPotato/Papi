package net.fabricmc.fabric;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";

    public Papi() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static int checkSide(WorldAccess worldAccess, Chunk chunk) {
        if (chunk instanceof WorldChunk) {
            if (worldAccess instanceof ServerWorld) return 1;
            if (worldAccess instanceof ClientWorld) return 2;
        }
        return 114514;
    }
}