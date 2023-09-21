package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleForgeImpl;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod("papi")
public class Papi {
    public Papi() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(LifecycleForgeImpl.class);
        NetworkingImpl.init();
        if (FMLLoader.getDist().isClient()) {
            bus.addListener(KeyBindingRegistryImpl::registerKeys);
            ClientNetworkingImpl.clientInit();
            MinecraftForge.EVENT_BUS.register(LifecycleForgeImpl.Client.class);
        }
    }
}
