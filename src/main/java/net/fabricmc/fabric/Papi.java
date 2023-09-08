package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.client.event.lifecycle.ClientLifecycleEventsImpl;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleEventsImpl;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";

    public Papi() {
        LifecycleEventsImpl.init();
        NetworkingImpl.init();
        if (FMLLoader.getDist().isClient()) {
            final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(KeyBindingRegistryImpl::registerKeys);

            ClientNetworkingImpl.clientInit();
            ClientLifecycleEventsImpl.clientInit();
        }
    }
}
