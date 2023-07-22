package net.fabricmc.fabric.papi.api;

import net.minecraft.block.Block;

import java.util.Set;

public interface ExtendedBlockEntityType {
    Set<Block> getBlocks();
}
