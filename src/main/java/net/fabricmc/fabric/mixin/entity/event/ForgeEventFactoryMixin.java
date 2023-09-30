package net.fabricmc.fabric.mixin.entity.event;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeEventFactory.class, remap = false)
public class ForgeEventFactoryMixin {
    @Inject(method = "fireSleepingLocationCheck", at = @At("RETURN"), cancellable = true)
    private static void onIsSleepingInBed(LivingEntity player, BlockPos sleepingPos, CallbackInfoReturnable<Boolean> cir) {
        BlockState bedState = player.world.getBlockState(sleepingPos);
        ActionResult result = EntitySleepEvents.ALLOW_BED.invoker().allowBed(player, sleepingPos, bedState, cir.getReturnValueZ());

        if (result != ActionResult.PASS) {
            cir.setReturnValue(result.isAccepted());
        }
    }
}
