package net.fabricmc.fabric.impl.object.builder;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ObjectBuilderImpl {
    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(FabricDefaultAttributeRegistryImpl.class);

        MinecraftForge.EVENT_BUS.register(TradeOfferInternals.class);
    }
}
