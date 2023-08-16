package net.fabricmc.fabric.mixin.papi.blockrenderlayer;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraftforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RenderLayers.class)
public interface RenderLayersAccessor {
    @Accessor("BLOCK_RENDER_TYPES")
    static Map<RegistryEntry.Reference<Block>, ChunkRenderTypeSet> getBlockRenderTypes() {
        throw new AssertionError();
    }
}
