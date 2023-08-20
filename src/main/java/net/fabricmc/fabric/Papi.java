package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.client.indigo.Indigo;
import net.fabricmc.fabric.impl.event.lifecycle.ClientLifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LegacyEventInvokers;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.client.LegacyClientEventInvokers;
import net.fabricmc.fabric.impl.networking.OldClientNetworkingHooks;
import net.fabricmc.fabric.impl.networking.OldNetworkingHooks;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.fabric.papi.event.ResLoaderImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public Papi() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        LegacyEventInvokers.onInitialize();
        LifecycleEventsImpl.onInitialize();
        OldNetworkingHooks.onInitialize();

        if (FMLLoader.getDist().isClient()) {
            LegacyClientEventInvokers.onInitializeClient();
            ClientLifecycleEventsImpl.onInitializeClient();
            OldClientNetworkingHooks.onInitializeClient();
            Indigo.onInitializeClient();

            modEventBus.addListener(ResLoaderImpl::onClientResourcesReload);
        }

        modEventBus.addListener(ResLoaderImpl::addPackFinders);
        forgeEventBus.addListener(ResLoaderImpl::onServerDataReload);
    }
}
