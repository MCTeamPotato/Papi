package net.fabricmc.fabric.api.resource;

import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ResourceManagerHelper {

    public static Register get(ResourceType resourceType) {
        return new Register(resourceType);
    }

    public static class Register {
        private final ResourceType resourceType;

        private Register(ResourceType resourceType) {
            this.resourceType = resourceType;
        }

        public void registerReloadListener(ResourceReloader resourceReloader) {
            if (this.resourceType.equals(ResourceType.CLIENT_RESOURCES)) {
                FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterClientReloadListenersEvent event) -> event.registerReloadListener(resourceReloader));
            } else if (this.resourceType.equals(ResourceType.SERVER_DATA)) {
                FMLJavaModLoadingContext.get().getModEventBus().addListener((AddReloadListenerEvent event) -> event.addListener(resourceReloader));
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
}
