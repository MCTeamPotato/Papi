package net.fabricmc.fabric.impl.item;

import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.fabricmc.fabric.impl.client.item.ItemApiClientEventHooks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.fml.loading.FMLLoader;

public class FabricItemImpl {

    public static void init() {
        if (FMLLoader.getDist() == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(ItemApiClientEventHooks.class);
        }
        MinecraftForge.EVENT_BUS.addListener(FabricItemImpl::modifyItemAttributeModifiers);
    }

    private static void modifyItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ModifyItemAttributeModifiersCallback.EVENT.invoker().modifyAttributeModifiers(event.getItemStack(), event.getSlotType(), event.getModifiers());
    }
}