package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public Papi() {
        NetworkingImpl.init();
        if (FMLLoader.getDist().isClient()) {
            ClientNetworkingImpl.clientInit();
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        }
    }
}