package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.event.interaction.InteractionEventsRouter;
import net.fabricmc.fabric.impl.event.interaction.InteractionEventsRouterClient;
import net.fabricmc.fabric.impl.structure.FabricStructureImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(Papi.MOD_ID)
public class Papi {

    public static final String MOD_ID = "papi";

    public Papi() {
        MinecraftForge.EVENT_BUS.register(this);
        InteractionEventsRouterClient.onInitializeClient();
        InteractionEventsRouter.onInitialize();
        FabricStructureImpl.onInitialize();
    }
}