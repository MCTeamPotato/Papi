package net.fabricmc.fabric.impl.client.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RenderGuiEvent;

public final class ClientRenderingEventHooks {

    static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        ColorProviderRegistryImpl.BLOCK.initialize(event.getBlockColors());
    }

    static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        ColorProviderRegistryImpl.ITEM.initialize(event.getItemColors());
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        EntityRendererRegistryImpl.setup((type, provider) -> event.registerEntityRenderer((EntityType) type, provider));

        BlockEntityRendererRegistryImpl.setup((t, factory) -> event.registerBlockEntityRenderer((BlockEntityType) t, factory));
    }

    static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        EntityModelLayerImpl.PROVIDERS.forEach((name, provider) -> event.registerLayerDefinition(name, provider::createModelData));
    }

    static void onPostRenderHud(RenderGuiEvent.Post event) {
        HudRenderCallback.EVENT.invoker().onHudRender(event.getPoseStack(), event.getPartialTick());
    }
}