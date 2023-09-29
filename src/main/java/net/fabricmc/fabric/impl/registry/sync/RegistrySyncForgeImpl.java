package net.fabricmc.fabric.impl.registry.sync;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.mixin.registry.sync.AccessorRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.ArrayList;
import java.util.List;

public class RegistrySyncForgeImpl {
    private static final List<MutableRegistry<?>> REGISTRIES = new ArrayList<>();

    public static void addRegistry(MutableRegistry<?> registry) {
        REGISTRIES.add(registry);
    }

    public static void submitRegistries() {
        if (Registry.REGISTRIES instanceof SimpleRegistry<?> rootRegistry)
            rootRegistry.unfreeze();

        REGISTRIES.forEach(registry -> AccessorRegistry.getROOT().add((RegistryKey<MutableRegistry<?>>) registry.getKey(), registry, Lifecycle.stable()));

        if (Registry.REGISTRIES instanceof SimpleRegistry<?> rootRegistry)
            rootRegistry.freeze();
    }
}
