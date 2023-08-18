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
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bootstrap.class)
public class BootstrapMixin {
	@Inject(method = "setOutputStreams", at = @At("RETURN"))
	private static void initialize(CallbackInfo info) {
		// These seemingly pointless accesses are done to make sure each
		// static initializer is called, to register vanilla-provided blocks
		// and items from the respective classes - otherwise, they would
		// duplicate our calls from below.
		Object oBiome = BiomeKeys.THE_END;
		Object oBlock = Blocks.AIR;
		Object oFluid = Fluids.EMPTY;
		Object oItem = Items.AIR;

		RegistrySyncManager.bootstrapRegistries();
	}

	@Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/Registry;freezeRegistries()V"))
	private static void skipFreeze() {
		// Don't freeze
	}

	@Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/registries/GameData;vanillaSnapshot()V"))
	private static void skipVanillaSnapshot() {
		// NO OP
	}
}