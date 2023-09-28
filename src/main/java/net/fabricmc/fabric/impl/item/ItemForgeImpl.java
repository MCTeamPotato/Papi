package net.fabricmc.fabric.impl.item;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class ItemForgeImpl {
    @SubscribeEvent
    public static void onItemTooltip(@NotNull ItemTooltipEvent event) {
        ItemTooltipCallback.EVENT.invoker().getTooltip(event.getItemStack(), event.getFlags(), event.getToolTip());
    }
}
