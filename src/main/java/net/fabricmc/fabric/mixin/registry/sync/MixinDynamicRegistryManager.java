/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.registry.sync;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistrySync;

@Mixin(DynamicRegistryManager.class)
public class MixinDynamicRegistryManager {
	// SO FUNNY
	//@Inject(method = "createAndLoad", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/EntryLoader$Impl;<init>()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	//private static void onCreateImpl(CallbackInfoReturnable<DynamicRegistryManager.Mutable> cir, DynamicRegistryManager.Mutable registryManager) {
	//	DynamicRegistrySetupCallback.EVENT.invoker().onRegistrySetup(registryManager);
	//}

	/**
	 * Ensures that any registrations made into {@link net.minecraft.util.registry.BuiltinRegistries} after
	 * {@link DynamicRegistryManager} has been class-loaded are still propagated.
	 */
	//@Inject(method = "method_40327", at = @At(value = "RETURN"))
	//private static void setupBuiltInSync(CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir) {
	//	DynamicRegistrySync.setupSync(cir.getReturnValue());
	//}
}
