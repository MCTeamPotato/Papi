package net.fabricmc.fabric.impl.client.rendering;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RenderingImpl {
    public static void clientInit() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ClientRenderingEventHooks::onRegisterBlockColors);
        bus.addListener(ClientRenderingEventHooks::onRegisterItemColors);
        bus.addListener(ClientRenderingEventHooks::registerEntityRenderers);
        bus.addListener(ClientRenderingEventHooks::registerLayerDefinitions);
        MinecraftForge.EVENT_BUS.addListener(ClientRenderingEventHooks::onPostRenderHud);
    }
}
