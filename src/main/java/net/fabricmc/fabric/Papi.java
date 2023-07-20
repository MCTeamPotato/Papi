package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.event.lifecycle.ClientLifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LegacyEventInvokers;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.client.LegacyClientEventInvokers;
import net.fabricmc.fabric.impl.networking.OldClientNetworkingHooks;
import net.fabricmc.fabric.impl.networking.OldNetworkingHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";
    public Papi() {
        LegacyEventInvokers.onInitialize();
        LifecycleEventsImpl.onInitialize();
        OldNetworkingHooks.onInitialize();
        if (FMLLoader.getDist().isClient()) {
            LegacyClientEventInvokers.onInitializeClient();
            ClientLifecycleEventsImpl.onInitializeClient();
            OldClientNetworkingHooks.onInitializeClient();
        }
    }
}
