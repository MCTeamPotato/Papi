package net.fabricmc.fabric.mixin.papi.interaction;

import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeHooks.class, remap = false)
public abstract class MixinForgeHooks {
    private static boolean fabric_itemPickCancelled;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getSlotWithStack(Lnet/minecraft/item/ItemStack;)I"), method = "onPickBlock")
    private static int modifyItemPick(PlayerInventory inventory, ItemStack stack) {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack result = ClientPickBlockApplyCallback.EVENT.invoker().pick(client.player, client.crosshairTarget, stack);
        fabric_itemPickCancelled = result.isEmpty();
        return inventory.getSlotWithStack(result);
    }


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getSlotWithStack(Lnet/minecraft/item/ItemStack;)I", shift = At.Shift.BEFORE), method = "onPickBlock", cancellable = true)
    private static void cancelItemPick(CallbackInfo info) {
        if (fabric_itemPickCancelled) {
            info.cancel();
        }
    }
}
