package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.fabric.impl.client.event.lifecycle.ClientLifecycleEventsImpl;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.fabric.impl.command.CommandApiForgeImpl;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleForgeImpl;
import net.fabricmc.fabric.impl.lookup.ApiLookupImpl;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
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
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        LifecycleEventsImpl.init();
        NetworkingImpl.init();
        ApiLookupImpl.init();
        MinecraftForge.EVENT_BUS.register(LifecycleForgeImpl.class);
        MinecraftForge.EVENT_BUS.addListener(CommandApiForgeImpl::registerCommands);
        bus.addListener(CommandApiForgeImpl::registerArgumentTypes);
        if (FMLLoader.getDist().isClient()) {
            MinecraftForge.EVENT_BUS.register(LifecycleForgeImpl.Client.class);
            MinecraftForge.EVENT_BUS.addListener(ClientCommandInternals::registerClientCommands);
            bus.addListener(KeyBindingRegistryImpl::registerKeys);
            bus.addListener(BlockRenderLayerMapImpl::initRenderLayers);

            ClientNetworkingImpl.clientInit();
            ClientLifecycleEventsImpl.clientInit();
        }
    }
}
