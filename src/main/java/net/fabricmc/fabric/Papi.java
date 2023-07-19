package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.event.interaction.InteractionEventsRouter;
import net.fabricmc.fabric.impl.event.interaction.InteractionEventsRouterClient;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.fabricmc.fabric.impl.structure.FabricStructureImpl;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Papi.MOD_ID)
public class Papi {

    public static final String MOD_ID = "papi";

    public Papi() {
        InteractionEventsRouter.onInitialize();
        FabricStructureImpl.onInitialize();

        if (FMLLoader.getDist().isClient()) {
            ClientNetworking.onInitializeClient();
            InteractionEventsRouterClient.onInitializeClient();
        }
    }
}