package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.client.indigo.Indigo;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.fabric.impl.event.lifecycle.ClientLifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LegacyEventInvokers;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.client.LegacyClientEventInvokers;
import net.fabricmc.fabric.impl.networking.OldClientNetworkingHooks;
import net.fabricmc.fabric.impl.networking.OldNetworkingHooks;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.fabricmc.papi.impl.event.lifecycle.ChunkEventsImpl;
import net.fabricmc.papi.impl.resource.loader.ResourceLoaderImpl;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public Papi() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        LegacyEventInvokers.onInitialize();
        OldNetworkingHooks.onInitialize();

        LifecycleEventsImpl.onInitialize();

        if (FMLLoader.getDist().isClient()) {
            LegacyClientEventInvokers.onInitializeClient();
            OldClientNetworkingHooks.onInitializeClient();

            Indigo.onInitializeClient();
            ClientLifecycleEventsImpl.onInitializeClient();
            ClientNetworkingImpl.clientInit();
            ClientNetworking.onInitializeClient();

            KeyBindingRegistryImpl.onRegisterKeyMappings();

            modEventBus.addListener(ResourceLoaderImpl::onClientResourcesReload);
        }

        modEventBus.addListener(ResourceLoaderImpl::addPackFinders);

        forgeEventBus.addListener(ResourceLoaderImpl::onServerDataReload);
        forgeEventBus.register(ChunkEventsImpl.class);
    }
}
