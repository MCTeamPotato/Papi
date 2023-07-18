package net.fabricmc.fabric.mixin.papi.interaction;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraftforge.event.world.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockEvent.BreakEvent.class, remap = false)
public class MixinBreakEvent extends BlockEvent {
    public MixinBreakEvent(WorldAccess world, BlockPos pos, BlockState state) {
        super(world, pos, state);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        BlockEntity entity = world.getBlockEntity(pos);
        boolean result = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, player, pos, state, entity);
        if (!result) {
            PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(world, player, pos, state, entity);
            this.setCanceled(true);
        }
    }
}
