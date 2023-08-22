package net.fabricmc.papi.impl.event.interaction;

import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public final class InteractionEventImpl {
    @SubscribeEvent
    public static void onEntityInteractAt(PlayerInteractEvent.EntityInteractSpecific event) {
        Entity entity = event.getTarget();
        EntityHitResult hitResult = new EntityHitResult(entity, event.getLocalPos().add(entity.getPos()));
        ActionResult result = UseEntityCallback.EVENT.invoker().interact(event.getPlayer(), event.getWorld(), event.getHand(), entity, hitResult);
        if (result != ActionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ActionResult result = UseEntityCallback.EVENT.invoker().interact(event.getPlayer(), event.getWorld(), event.getHand(), event.getTarget(), null);
        if (result != ActionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        PlayerEntity player = event.getPlayer();
        ActionResult result = AttackEntityCallback.EVENT.invoker().interact(player, player.getWorld(), Hand.MAIN_HAND, event.getTarget(), null);
        if (result != ActionResult.PASS) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getSide() == LogicalSide.CLIENT) {
            ActionResult result = AttackBlockCallback.EVENT.invoker().interact(event.getPlayer(), event.getWorld(), event.getHand(), event.getPos(), event.getFace());
            if (result != ActionResult.PASS) {
                // Returning true will spawn particles and trigger the animation of the hand -> only for SUCCESS.
                // TODO TEST
                event.setUseBlock(result == ActionResult.SUCCESS ? Event.Result.ALLOW : Event.Result.DENY);
                event.setUseItem(result == ActionResult.SUCCESS ? Event.Result.ALLOW : Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ActionResult result = UseBlockCallback.EVENT.invoker().interact(event.getPlayer(), event.getWorld(), event.getHand(), event.getHitVec());
        if (result != ActionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        TypedActionResult<ItemStack> result = UseItemCallback.EVENT.invoker().interact(event.getPlayer(), event.getWorld(), event.getHand());
        if (result.getResult() != ActionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result.getResult());
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        World level = player.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        BlockEntity be = level.getBlockEntity(pos);
        boolean result = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, state, be);

        if (!result) {
            PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(level, player, pos, state, be);

            event.setCanceled(true);
        }
    }

    private InteractionEventImpl() {}
}
