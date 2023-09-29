package net.fabricmc.fabric.mixin.registry.sync;

import net.fabricmc.fabric.impl.registry.sync.RegistrySyncForgeImpl;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(RegistryManager.class)
public class RegistryManagerMixin {
    @Inject(method = "postNewRegistryEvent", at = @At("HEAD"), remap = false)
    private static void beforePostNewRegistryEvent(Executor executor, IModStateTransition.EventGenerator<? extends NewRegistryEvent> eventGenerator, CallbackInfoReturnable<CompletableFuture<List<Throwable>>> cir) {
        RegistrySyncForgeImpl.submitRegistries();
    }
}
