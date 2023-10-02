package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.fabric.impl.command.CommandForgeImpl;
import net.fabricmc.fabric.impl.lifecycle.LifecycleForgeImpl;
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
    public static final Logger LOGGER = LogManager.getLogger(Papi.class);
    public Papi() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus eventBus = MinecraftForge.EVENT_BUS;

        eventBus.register(LifecycleForgeImpl.class);
        eventBus.addListener(CommandForgeImpl::registerCommands);
        if (FMLLoader.getDist().isClient()) {
            modBus.addListener(BlockRenderLayerMapImpl::initRenderLayers);

            eventBus.register(LifecycleForgeImpl.Client.class);
        }
    }
}
