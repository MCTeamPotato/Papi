package net.fabricmc.fabric.mixin.registry.sync;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<T> extends Registry<T> {
    @Unique
    private static final Logger FARBIC_LOGGER = LogManager.getLogger("FabricRegistrySync");

    protected SimpleRegistryMixin(RegistryKey<Registry<T>> arg, Lifecycle lifecycle) {
        super(arg, lifecycle);
    }

    @Inject(method = "add", at = @At("RETURN"))
    private <V extends T> void add(RegistryKey<Registry<T>> registryKey, V entry, Lifecycle lifecycle, CallbackInfoReturnable<V> info) {
        papi$onChange(registryKey);
    }

    @Inject(method = "set(ILnet/minecraft/util/registry/RegistryKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Ljava/lang/Object;", at = @At("RETURN"))
    private <V extends T> void set(int rawId, RegistryKey<Registry<T>> registryKey, V entry, Lifecycle lifecycle, CallbackInfoReturnable<V> info) {
        papi$onChange(registryKey);
    }

    @Unique
    private void papi$onChange(RegistryKey<Registry<T>> registryKey) {
        if (RegistrySyncManager.postBootstrap || !registryKey.getValue().getNamespace().equals("minecraft")) {
            RegistryAttributeHolder holder = RegistryAttributeHolder.get(this);

            if (!holder.hasAttribute(RegistryAttribute.MODDED)) {
                Identifier id = getKey().getValue();
                FARBIC_LOGGER.debug("Registry {} has been marked as modded, registry entry {} was changed", id, registryKey.getValue());
                RegistryAttributeHolder.get(this).addAttribute(RegistryAttribute.MODDED);
            }
        }
    }
}