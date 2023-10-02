package net.fabricmc.fabric.mixin.object.builder;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(VillagerType.class)
public interface VillagerTypeAccessor {
    @Accessor("BIOME_TO_TYPE")
    static Map<RegistryKey<Biome>, VillagerType> getBiomeToType() {
        throw new UnsupportedOperationException();
    }
}
