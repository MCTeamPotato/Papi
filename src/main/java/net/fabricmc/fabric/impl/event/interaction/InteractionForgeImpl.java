package net.fabricmc.fabric.impl.event.interaction;

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
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

public class InteractionForgeImpl {
    @SubscribeEvent
    public static void onEntityInteractAt(PlayerInteractEvent.@NotNull EntityInteractSpecific event) {
        Entity entity = event.getTarget();
        EntityHitResult hitResult = new EntityHitResult(entity, event.getLocalPos().add(entity.getPos()));
        ActionResult result = UseEntityCallback.EVENT.invoker().interact(event.getEntity(), event.getLevel(), event.getHand(), entity, hitResult);
        if (result != ActionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.@NotNull EntityInteract event) {
        ActionResult result = UseEntityCallback.EVENT.invoker().interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget(), null);
        if (result != ActionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(@NotNull AttackEntityEvent event) {
        PlayerEntity player = event.getEntity();
        ActionResult result = AttackEntityCallback.EVENT.invoker().interact(player, player.getWorld(), Hand.MAIN_HAND, event.getTarget(), null);
        if (result != ActionResult.PASS) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.@NotNull LeftClickBlock event) {
        if (event.getSide() == LogicalSide.CLIENT) {
            ActionResult result = AttackBlockCallback.EVENT.invoker().interact(event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), event.getFace());
            if (result != ActionResult.PASS) {
                event.setUseBlock(result == ActionResult.SUCCESS ? Event.Result.ALLOW : Event.Result.DENY);
                event.setUseItem(result == ActionResult.SUCCESS ? Event.Result.ALLOW : Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.@NotNull RightClickBlock event) {
        ActionResult result = UseBlockCallback.EVENT.invoker().interact(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
        if (result != ActionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.@NotNull RightClickItem event) {
        TypedActionResult<ItemStack> result = UseItemCallback.EVENT.invoker().interact(event.getEntity(), event.getLevel(), event.getHand());
        if (result.getResult() != ActionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result.getResult());
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.@NotNull BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        World level = player.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        BlockEntity be = level.getBlockEntity(pos);
        if (!PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, state, be)) {
            PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(level, player, pos, state, be);

            event.setCanceled(true);
        }
    }
}
