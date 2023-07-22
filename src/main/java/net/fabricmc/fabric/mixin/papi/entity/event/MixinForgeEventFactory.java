package net.fabricmc.fabric.mixin.papi.entity.event;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeEventFactory.class, remap = false)
public class MixinForgeEventFactory {
    @Inject(method = "fireSleepingLocationCheck", at = @At("RETURN"), cancellable = true)
    private static void onSleep(LivingEntity player, BlockPos sleepingLocation, CallbackInfoReturnable<Boolean> cir) {
        boolean forgeResult = cir.getReturnValue();
        ActionResult result = EntitySleepEvents.ALLOW_BED.invoker().allowBed(player, sleepingLocation, player.world.getBlockState(sleepingLocation), forgeResult);
        if (result != ActionResult.PASS) {
            cir.setReturnValue(result.isAccepted());
        }
    }
}
