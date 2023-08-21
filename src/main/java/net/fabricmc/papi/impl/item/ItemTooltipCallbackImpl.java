package net.fabricmc.papi.impl.item;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemTooltipCallbackImpl {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemTooltipCallback.EVENT.invoker().getTooltip(event.getItemStack(), event.getFlags(), event.getToolTip());
    }
}
