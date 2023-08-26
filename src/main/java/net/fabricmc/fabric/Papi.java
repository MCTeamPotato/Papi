package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.lifecycle.LifecycleEventHooks;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public Papi() {
        NetworkingImpl.init();
        MinecraftForge.EVENT_BUS.register(LifecycleEventHooks.class);
        if (FMLLoader.getDist().isClient()) {
            ClientNetworkingImpl.clientInit();
        }
    }
}