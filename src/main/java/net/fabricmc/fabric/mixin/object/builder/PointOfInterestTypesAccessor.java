package net.fabricmc.fabric.mixin.object.builder;

import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(PointOfInterestTypes.class)
public interface PointOfInterestTypesAccessor {
    @Accessor("POI_STATES")
    static Set<BlockState> getPoiStates() {
        throw new UnsupportedOperationException();
    }

    @Invoker("register")
    static PointOfInterestType papi$register(Registry<PointOfInterestType> registry, RegistryKey<PointOfInterestType> key, Set<BlockState> states, int ticketCount, int searchDistance) {
        throw new UnsupportedOperationException();
    }
}
