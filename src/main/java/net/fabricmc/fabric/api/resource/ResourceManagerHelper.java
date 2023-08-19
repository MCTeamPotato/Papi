package net.fabricmc.fabric.api.resource;

import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ResourceManagerHelper {
    public static ResourceManagerHelperInternal get(ResourceType resourceType) {
        return new ResourceManagerHelperInternal(resourceType);
    }

    public static class ResourceManagerHelperInternal {
        private final ResourceType resourceType;
        public ResourceManagerHelperInternal(ResourceType resourceType) {
            this.resourceType = resourceType;
        }

        public void registerReloadListener(ResourceReloader resourceReloader) {
            if (this.resourceType.equals(ResourceType.CLIENT_RESOURCES)) {
                FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterClientReloadListenersEvent event) -> event.registerReloadListener(resourceReloader));
            }
        }
    }
}
