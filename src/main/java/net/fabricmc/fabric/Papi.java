package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.fabric.impl.client.event.lifecycle.ClientLifecycleEventsImpl;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.fabric.impl.client.screen.ScreenEventHooks;
import net.fabricmc.fabric.impl.entity.event.EntityEventHooks;
import net.fabricmc.fabric.impl.event.interaction.InteractionEventsRouter;
import net.fabricmc.fabric.impl.event.lifecycle.LifecycleEventsImpl;
import net.fabricmc.fabric.impl.lookup.ApiLookupImpl;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientInit;
import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientSync;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Papi.MOD_ID)
public class Papi {
    public static final String MOD_ID = "papi";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public Papi() {
        NetworkingImpl.init();
        LifecycleEventsImpl.init();
        ApiLookupImpl.init();
        InteractionEventsRouter.init();
        CustomIngredientInit.init();
        CustomIngredientSync.init();
        MinecraftForge.EVENT_BUS.register(LifecycleEventsImpl.class);
        MinecraftForge.EVENT_BUS.register(EntityEventHooks.class);
        if (FMLLoader.getDist().isClient()) {
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(BlockRenderLayerMapImpl::onClientSetup);
            bus.addListener(KeyBindingRegistryImpl::onRegisterKeyMappings);
            MinecraftForge.EVENT_BUS.register(ScreenEventHooks.class);
            ClientNetworkingImpl.clientInit();
            ClientLifecycleEventsImpl.clientInit();
            ClientNetworking.clientInit();
        }
    }
}
