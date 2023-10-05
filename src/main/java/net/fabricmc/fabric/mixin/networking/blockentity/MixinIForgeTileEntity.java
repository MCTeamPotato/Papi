package net.fabricmc.fabric.mixin.networking.blockentity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IForgeTileEntity.class)
public interface MixinIForgeTileEntity {
    @Shadow BlockEntity getTileEntity();

    /**
     * @author Kasualix
     * @reason impl api
     */
    @Overwrite
    default void handleUpdateTag(BlockState state, CompoundTag tag) {
        BlockEntity entity = getTileEntity();
        if (entity instanceof BlockEntityClientSerializable) {
            ((BlockEntityClientSerializable) entity).fromClientTag(tag);
        } else {
            entity.fromTag(state, tag);
        }
    }
}
