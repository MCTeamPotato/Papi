package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.client.event.lifecycle.ClientLifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleEventsImpl;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";
    public Papi() {
        LifecycleEventsImpl.init();
        NetworkingImpl.init();

        if (FMLLoader.getDist().isClient()) {
            ClientNetworkingImpl.clientInit();
            ClientLifecycleEventsImpl.clientInit();
            ClientNetworking.clientInit();
        }
    }
}
