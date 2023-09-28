package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.fabric.impl.client.event.lifecycle.ClientLifecycleEventsImpl;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.fabric.impl.client.rendering.RenderingForgeImpl;
import net.fabricmc.fabric.impl.command.CommandApiForgeImpl;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.fabricmc.fabric.impl.entity.event.EntityEventForgeImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleEventsImpl;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleForgeImpl;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.fabricmc.fabric.impl.item.ItemForgeImpl;
import net.fabricmc.fabric.impl.lookup.ApiLookupImpl;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.object.builder.ObjectBuilderForgeImpl;
import net.fabricmc.fabric.impl.object.builder.TradeOfferInternals;
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
    public static final Logger LOGGER = LoggerFactory.getLogger(Papi.class);

    public Papi() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        LifecycleEventsImpl.init();
        NetworkingImpl.init();
        ApiLookupImpl.init();
        MinecraftForge.EVENT_BUS.register(EntityEventForgeImpl.class);
        MinecraftForge.EVENT_BUS.register(LifecycleForgeImpl.class);
        MinecraftForge.EVENT_BUS.addListener(CommandApiForgeImpl::registerCommands);
        MinecraftForge.EVENT_BUS.register(TradeOfferInternals.class);
        bus.addListener(CommandApiForgeImpl::registerArgumentTypes);
        bus.register(ObjectBuilderForgeImpl.class);
        if (FMLLoader.getDist().isClient()) {
            MinecraftForge.EVENT_BUS.register(ItemForgeImpl.class);
            MinecraftForge.EVENT_BUS.register(LifecycleForgeImpl.Client.class);
            MinecraftForge.EVENT_BUS.addListener(ClientCommandInternals::registerClientCommands);
            MinecraftForge.EVENT_BUS.addListener(FabricItemInternals::modifyItemAttributeModifiers);
            MinecraftForge.EVENT_BUS.addListener(RenderingForgeImpl::onPostRenderHud);
            bus.addListener(KeyBindingRegistryImpl::registerKeys);
            bus.addListener(BlockRenderLayerMapImpl::initRenderLayers);
            bus.addListener(RenderingForgeImpl::onRegisterBlockColors);
            bus.addListener(RenderingForgeImpl::onRegisterItemColors);
            bus.addListener(RenderingForgeImpl::registerEntityRenderers);
            bus.addListener(RenderingForgeImpl::registerLayerDefinitions);

            ClientLifecycleEventsImpl.clientInit();
        }
    }
}
