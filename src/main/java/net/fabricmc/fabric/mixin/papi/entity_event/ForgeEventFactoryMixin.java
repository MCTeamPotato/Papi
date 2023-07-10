package net.fabricmc.fabric.mixin.papi.entity_event;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = ForgeEventFactory.class, remap = false)
public class ForgeEventFactoryMixin {
    //boolean is inverted at the return because Forge adds a "!" at the return place.
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Inject(method = "fireSleepingTimeCheck", at = @At("HEAD"), cancellable = true)
    private static void onFireSleepingTimeCheck(PlayerEntity player, Optional<BlockPos> sleepingLocation, CallbackInfoReturnable<Boolean> cir) {
        boolean day = player.world.isDay();
        if (player.getSleepingPosition().isPresent()) {
            BlockPos pos = player.getSleepingPosition().get();
            ActionResult result = EntitySleepEvents.ALLOW_SLEEP_TIME.invoker().allowSleepTime(player, pos, !day);
            if (result != ActionResult.PASS) {
                cir.setReturnValue(result.isAccepted()); // true from the event = night-like conditions, so we have to invert
                cir.cancel();
            }
        }
        cir.setReturnValue(!day);
        cir.cancel();
    }
}
