package net.fabricmc.fabric.mixin.papi.api;

import net.fabricmc.fabric.papi.api.ExtendedBlockEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin implements ExtendedBlockEntityType {
    @Shadow @Final private Set<Block> blocks;

    @Override
    public Set<Block> getBlocks() {
        return this.blocks;
    }
}
