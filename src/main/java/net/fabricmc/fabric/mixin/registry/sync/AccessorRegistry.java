package net.fabricmc.fabric.mixin.registry.sync;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Registry.class)
public interface AccessorRegistry<T> {
    @Accessor()
    static MutableRegistry<MutableRegistry<?>> getROOT() {
        throw new UnsupportedOperationException();
    }

    @Accessor()
    RegistryKey<Registry<T>> getRegistryKey();

    @Invoker
    Lifecycle callGetEntryLifecycle(T object);
}
