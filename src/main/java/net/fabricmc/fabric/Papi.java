package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.event.lifecycle.LegacyEventInvokers;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleForgeImpl;
import net.fabricmc.fabric.impl.event.lifecycle.client.LegacyClientEventInvokers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";

    public Papi() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        LegacyEventInvokers.onInitialize();
        LifecycleEventsImpl.onInitialize();

        forgeEventBus.register(LifecycleEventsImpl.class);

        if (FMLLoader.getDist().isClient()) {
            LegacyClientEventInvokers.onInitializeClient();
            forgeEventBus.register(LifecycleForgeImpl.Client.class);
        }
    }
}
