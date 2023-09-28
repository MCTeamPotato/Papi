package net.fabricmc.fabric.api.resource;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.*;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ResourceManagerHelper {

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Register get(ResourceType resourceType) {
        return new Register(resourceType);
    }

    public static final class Register {
        private final ResourceType resourceType;

        private Register(ResourceType resourceType) {
            this.resourceType = resourceType;
        }

        public void registerReloadListener(ResourceReloadListener resourceReloadListener) {
            if (this.resourceType.equals(ResourceType.CLIENT_RESOURCES)) {
                ResourceManager resourceManage =  MinecraftClient.getInstance().getResourceManager();
                if (resourceManage instanceof ReloadableResourceManager) ((ReloadableResourceManager)resourceManage).registerListener(resourceReloadListener);
            } else if (this.resourceType.equals(ResourceType.SERVER_DATA)) {
                FMLJavaModLoadingContext.get().getModEventBus().addListener((AddReloadListenerEvent event) -> event.addListener(resourceReloadListener));
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
}