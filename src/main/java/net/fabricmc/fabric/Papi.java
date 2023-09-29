package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.fabric.impl.client.event.lifecycle.ClientLifecycleEventsImpl;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.fabric.impl.client.rendering.RenderingForgeImpl;
import net.fabricmc.fabric.impl.client.rendering.fluid.RenderingFluidForgeImpl;
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
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus eventBus = MinecraftForge.EVENT_BUS;
        LifecycleEventsImpl.init();
        NetworkingImpl.init();
        ApiLookupImpl.init();
        eventBus.register(EntityEventForgeImpl.class);
        eventBus.register(LifecycleForgeImpl.class);
        eventBus.addListener(CommandApiForgeImpl::registerCommands);
        eventBus.register(TradeOfferInternals.class);
        modBus.addListener(CommandApiForgeImpl::registerArgumentTypes);
        modBus.register(ObjectBuilderForgeImpl.class);
        if (FMLLoader.getDist().isClient()) {
            eventBus.register(ItemForgeImpl.class);
            eventBus.register(LifecycleForgeImpl.Client.class);
            eventBus.addListener(ClientCommandInternals::registerClientCommands);
            eventBus.addListener(FabricItemInternals::modifyItemAttributeModifiers);
            eventBus.addListener(RenderingForgeImpl::onPostRenderHud);
            modBus.addListener(KeyBindingRegistryImpl::registerKeys);
            modBus.addListener(BlockRenderLayerMapImpl::initRenderLayers);
            modBus.addListener(RenderingForgeImpl::onRegisterBlockColors);
            modBus.addListener(RenderingForgeImpl::onRegisterItemColors);
            modBus.addListener(RenderingForgeImpl::registerEntityRenderers);
            modBus.addListener(RenderingForgeImpl::registerLayerDefinitions);
            modBus.addListener(RenderingFluidForgeImpl::onClientSetup);

            ClientLifecycleEventsImpl.clientInit();
        }
    }
}
