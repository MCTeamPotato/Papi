package net.fabricmc.fabric.impl.item;

import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;

public class FabricItemImpl {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(FabricItemImpl::modifyItemAttributeModifiers);
    }

    private static void modifyItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ModifyItemAttributeModifiersCallback.EVENT.invoker().modifyAttributeModifiers(event.getItemStack(), event.getSlotType(), event.getModifiers());
    }
}