package net.fabricmc.fabric.impl.registry.sync;

import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.mixin.registry.sync.AccessorRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.List;

public class FabricRegistryInit {
    private static final List<MutableRegistry<?>> REGISTRIES = new ObjectArrayList<>();
    public static void addRegistry(MutableRegistry<?> registry) {
        REGISTRIES.add(registry);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public static void submitRegistries() {
        if (Registry.REGISTRIES instanceof SimpleRegistry<?> rootRegistry) rootRegistry.unfreeze();
        for (MutableRegistry<?> registry : REGISTRIES) AccessorRegistry.getRoot().add((RegistryKey<MutableRegistry<?>>) registry.getKey(), registry, Lifecycle.stable());
        if (Registry.REGISTRIES instanceof SimpleRegistry<?> rootRegistry) rootRegistry.freeze();
    }
}
