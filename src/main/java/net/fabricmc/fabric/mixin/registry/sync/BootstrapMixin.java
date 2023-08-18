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

import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Bootstrap.class, priority = 2000)
public class BootstrapMixin {
	@Inject(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/registries/GameData;vanillaSnapshot()V", remap = false, shift = At.Shift.AFTER))
	private static void afterVanillaSnapshot(CallbackInfo ci) {
		RegistrySyncManager.bootstrapRegistries();
	}
}