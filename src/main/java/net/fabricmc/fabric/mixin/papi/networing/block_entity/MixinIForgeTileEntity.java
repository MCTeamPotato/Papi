package net.fabricmc.fabric.mixin.papi.networing.block_entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = IForgeTileEntity.class, priority = 0, remap = false)
public interface MixinIForgeTileEntity {
    @Shadow
    default BlockEntity getTileEntity() {
        return (BlockEntity)this;
    }

    /**
     * @author Kasualix
     * @reason impl fabric block entity networking api
     **/
    @Overwrite
    default void handleUpdateTag(BlockState state, CompoundTag tag) {
        BlockEntity blockEntity = this.getTileEntity();
        if (blockEntity instanceof BlockEntityClientSerializable) {
            ((BlockEntityClientSerializable) blockEntity).fromClientTag(tag);
        } else {
            blockEntity.fromTag(state, tag);
        }
    }
}
