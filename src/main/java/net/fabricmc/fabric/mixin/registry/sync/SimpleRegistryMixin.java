package net.fabricmc.fabric.mixin.registry.sync;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.Papi;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<T> extends Registry<T> {
    protected SimpleRegistryMixin(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
        super(key, lifecycle);
    }

    @Inject(method = "add", at = @At("RETURN"))
    private <V extends T> void add(RegistryKey<Registry<T>> registryKey, T entry, Lifecycle lifecycle, CallbackInfoReturnable<RegistryEntry<T>> cir) {
        papi$onChange(registryKey);
    }

    @Inject(method = "set(ILnet/minecraft/util/registry/RegistryKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;Z)Lnet/minecraft/util/registry/RegistryEntry;", at = @At("RETURN"))
    private <V extends T> void set(int rawId, RegistryKey<Registry<T>> registryKey, T value, Lifecycle lifecycle, boolean checkDuplicateKeys, CallbackInfoReturnable<RegistryEntry<T>> cir) {
        papi$onChange(registryKey);
    }

    @Unique
    private static final Set<String> VANILLA_NAMESPACES = Set.of("minecraft", "brigadier");

    @Unique
    private void papi$onChange(RegistryKey<Registry<T>> registryKey) {
        if (RegistrySyncManager.postBootstrap || !VANILLA_NAMESPACES.contains(registryKey.getValue().getNamespace())) {
            RegistryAttributeHolder holder = RegistryAttributeHolder.get(this);

            if (!holder.hasAttribute(RegistryAttribute.MODDED)) {
                Identifier id = getKey().getValue();
                Papi.LOGGER.debug("Registry {} has been marked as modded, registry entry {} was changed", id, registryKey.getValue());
                RegistryAttributeHolder.get(this).addAttribute(RegistryAttribute.MODDED);
            }
        }
    }
}