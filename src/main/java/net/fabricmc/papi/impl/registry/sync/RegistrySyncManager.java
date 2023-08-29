package net.fabricmc.papi.impl.registry.sync;

public class RegistrySyncManager {
    //Set to true after vanilla's bootstrap has completed
    public static boolean postBootstrap = false;
    public static void bootstrapRegistries() {
        postBootstrap = true;
    }
}