package net.fabricmc.fabric;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";
    public Papi() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}