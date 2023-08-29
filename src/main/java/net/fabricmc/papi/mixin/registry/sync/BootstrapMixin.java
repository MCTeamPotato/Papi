package net.fabricmc.papi.mixin.registry.sync;

import net.fabricmc.papi.impl.registry.sync.RegistrySyncManager;
import net.minecraft.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bootstrap.class)
public abstract class BootstrapMixin {
    @Inject(method = "setOutputStreams", at = @At("RETURN"))
    private static void initialize(CallbackInfo info) {
        RegistrySyncManager.bootstrapRegistries();
    }
}