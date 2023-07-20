package net.fabricmc.fabric.papi.event;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.List;

public class ResLoaderImpl {
    public static void onClientResourcesReload(RegisterClientReloadListenersEvent event) {
        List<ResourceReloader> existingListeners = ((ReloadableResourceManagerImpl) MinecraftClient.getInstance().getResourceManager()).reloaders;
        List<ResourceReloader> listeners = ResourceManagerHelperImpl.sort(ResourceType.CLIENT_RESOURCES, existingListeners);
        listeners.forEach(event::registerReloadListener);
    }

    public static void addPackFinders(AddPackFindersEvent event) {
        event.addRepositorySource(new ModResourcePackCreator(event.getPackType()));
    }

    public static void onServerDataReload(AddReloadListenerEvent event) {
        List<ResourceReloader> listeners = ResourceManagerHelperImpl.sort(ResourceType.SERVER_DATA, event.getListeners());
        listeners.forEach(event::addListener);
    }
}
