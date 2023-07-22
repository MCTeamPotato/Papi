package net.fabricmc.fabric;

import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.fabricmc.fabric.impl.structure.FabricStructureImpl;
import net.fabricmc.fabric.impl.tool.attribute.ToolHandlers;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Papi.MOD_ID)
public class Papi {

    public static final String MOD_ID = "papi";
    public Papi() {
        ToolHandlers.init();
        NetworkingImpl.init();
        FabricStructureImpl.init();
        if (FMLLoader.getDist().isClient()) {
            ClientNetworkingImpl.clientInit();
        }
    }
}