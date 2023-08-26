package net.fabricmc.fabric.api.resource;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.event.AddReloadListenerEvent;
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

        public void registerReloadListener(ResourceReloadListener resourceReloader) {
            if (this.resourceType.equals(ResourceType.CLIENT_RESOURCES)) {
                ResourceManager resourceManager =  MinecraftClient.getInstance().getResourceManager();
                if (resourceManager instanceof ReloadableResourceManager) ((ReloadableResourceManager)resourceManager).registerListener(resourceReloader);
            } else if (this.resourceType.equals(ResourceType.SERVER_DATA)) {
                FMLJavaModLoadingContext.get().getModEventBus().addListener((AddReloadListenerEvent event) -> event.addListener(resourceReloader));
            } else {
                throw new UnsupportedOperationException("Unsupported resource type registry!");
            }
        }
    }
}