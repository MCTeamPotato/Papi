package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LegacyEventInvokers;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleForgeImpl;
import net.fabricmc.fabric.impl.event.lifecycle.client.LegacyClientEventInvokers;
import net.fabricmc.fabric.impl.lookup.ApiLookupImpl;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.OldClientNetworkingHooks;
import net.fabricmc.fabric.impl.networking.OldNetworkingHooks;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
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
        OldNetworkingHooks.onInitialize();
        LifecycleEventsImpl.onInitialize();
        ApiLookupImpl.onInitialize();
        NetworkingImpl.init();

        forgeEventBus.register(LifecycleEventsImpl.class);

        if (FMLLoader.getDist().isClient()) {
            LegacyClientEventInvokers.onInitializeClient();
            OldClientNetworkingHooks.onInitializeClient();
            KeyBindingRegistryImpl.registerKeys();
            ClientNetworkingImpl.clientInit();
            forgeEventBus.register(LifecycleForgeImpl.Client.class);
        }
    }
}
