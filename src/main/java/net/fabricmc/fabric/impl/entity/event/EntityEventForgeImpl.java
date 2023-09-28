package net.fabricmc.fabric.impl.entity.event;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityEventForgeImpl {
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.getWorld().isClient && !ServerLivingEntityEvents.ALLOW_DAMAGE.invoker().allowDamage(entity, event.getSource(), event.getAmount())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onSleepingLocationCheck(SleepingLocationCheckEvent event) {
        LivingEntity entity = event.getEntity();
        BlockPos sleepingPos = event.getSleepingLocation();

        BlockState bedState = entity.getWorld().getBlockState(sleepingPos);
        boolean vanillaResult = bedState.getBlock().isBed(bedState, entity.getWorld(), sleepingPos, entity);
        ActionResult result = EntitySleepEvents.ALLOW_BED.invoker().allowBed(entity, sleepingPos, bedState, vanillaResult);

        if (result != ActionResult.PASS) {
            event.setResult(result.isAccepted() ? Event.Result.ALLOW : Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        PlayerEntity.SleepFailureReason failureReason = EntitySleepEvents.ALLOW_SLEEPING.invoker().allowSleep(event.getEntity(), event.getPos());

        if (failureReason != null) {
            event.setResult(failureReason);
        }
    }

    @SubscribeEvent
    public static void onPlayerSleepingTimeCheck(SleepingTimeCheckEvent event) {
        event.getSleepingLocation().ifPresent(sleepingPos -> {
            PlayerEntity player = event.getEntity();
            ActionResult result = EntitySleepEvents.ALLOW_SLEEP_TIME.invoker().allowSleepTime(player, sleepingPos, !player.getWorld().isDay());
            if (result != ActionResult.PASS) {
                event.setResult(result.isAccepted() ? Event.Result.ALLOW : Event.Result.DENY);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        ServerPlayerEvents.COPY_FROM.invoker().copyFromPlayer((ServerPlayerEntity) event.getOriginal(), (ServerPlayerEntity) event.getEntity(), !event.isWasDeath());
    }
}
