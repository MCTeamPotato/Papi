package net.fabricmc.fabric.mixin.registry.sync;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@SuppressWarnings("unused")
@Mixin(DebugChunkGenerator.class)
public interface DebugChunkGeneratorAccessor {
    @Accessor
    @Mutable
    static void setBLOCK_STATES(List<BlockState> blockStates) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    @Mutable
    static void setX_SIDE_LENGTH(int length) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    @Mutable
    static void setZ_SIDE_LENGTH(int length) {
        throw new UnsupportedOperationException();
    }
}