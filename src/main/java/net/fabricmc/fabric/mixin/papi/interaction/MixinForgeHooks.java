package net.fabricmc.fabric.mixin.papi.interaction;

import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;

@Mixin(value = ForgeHooks.class, remap = false, priority = -1000)
public abstract class MixinForgeHooks {
    @Shadow @Final private static Logger LOGGER;
    @Unique
    private static boolean fabric_itemPickCancelled;

    @Unique
    private static int papi$modifyItemPick(PlayerInventory inventory, ItemStack stack) {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack result = ClientPickBlockApplyCallback.EVENT.invoker().pick(client.player, client.crosshairTarget, stack);
        fabric_itemPickCancelled = result.isEmpty();
        return inventory.getSlotWithStack(result);
    }

    /**
     * @author Kasualix
     * @reason impl
     */
    @Overwrite
    public static boolean onPickBlock(HitResult target, PlayerEntity player, World world) {
        ItemStack result = ItemStack.EMPTY;
        boolean isCreative = player.abilities.creativeMode;
        BlockEntity te = null;
        if (target.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult)target).getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (state.isAir(world, pos)) {
                return false;
            }

            if (isCreative && Screen.hasControlDown() && state.hasTileEntity()) {
                te = world.getBlockEntity(pos);
            }

            result = state.getPickBlock(target, world, pos, player);
            if (result.isEmpty()) {
                LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), state.getBlock().getRegistryName());
            }
        } else if (target.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult)target).getEntity();
            result = entity.getPickedResult(target);
            if (result.isEmpty()) {
                LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), entity.getType().getRegistryName());
            }
        }

        if (result.isEmpty()) {
            return false;
        } else {
            if (te != null) {
                MinecraftClient.getInstance().addBlockEntityNbt(result, te);
            }

            if (isCreative) {
                player.inventory.addPickBlock(result);
                if (MinecraftClient.getInstance().interactionManager != null) {
                    MinecraftClient.getInstance().interactionManager.clickCreativeStack(player.getStackInHand(Hand.MAIN_HAND), 36 + player.inventory.selectedSlot);
                }
                return true;
            } else {
                int slot = papi$modifyItemPick(player.inventory, result);
                if (fabric_itemPickCancelled) return false;
                if (slot != -1) {
                    if (PlayerInventory.isValidHotbarIndex(slot)) {
                        player.inventory.selectedSlot = slot;
                    } else {
                        if (MinecraftClient.getInstance().interactionManager != null) {
                            MinecraftClient.getInstance().interactionManager.pickFromInventory(slot);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
