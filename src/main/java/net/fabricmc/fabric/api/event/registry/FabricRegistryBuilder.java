package net.fabricmc.fabric.api.event.registry;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.mixin.registry.sync.RegistryAccessor;
import net.minecraft.util.registry.MutableRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class FabricRegistryBuilder<T, R extends MutableRegistry<T>> {
    @Contract(value = "_ -> new", pure = true)
    public static <T, R extends MutableRegistry<T>> @NotNull FabricRegistryBuilder<T, R> from(R registry) {
        return new FabricRegistryBuilder<>(registry);
    }

    private final R registry;

    private FabricRegistryBuilder(R registry) {
        this.registry = registry;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public R buildAndRegister() {
        RegistryAccessor.getRoot().add(((RegistryAccessor)registry).getRegistryKey(), registry, Lifecycle.stable());
        return registry;
    }
}
