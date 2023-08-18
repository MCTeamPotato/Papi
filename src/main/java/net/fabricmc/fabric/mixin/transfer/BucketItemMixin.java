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

package net.fabricmc.fabric.mixin.transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Automatically uses the correct bucket emptying sound for
 * fluid attributes handlers overriding {@link FluidVariantAttributeHandler#getEmptySound}.
 */
@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

	@Shadow(remap = false) public abstract Fluid getFluid();

	@ModifyVariable(
			method = "playEmptyingSound",
			at = @At("STORE"),
			index = 4
	)
	private SoundEvent hookEmptyingSound(SoundEvent previous) {
		Fluid fluid = getFluid();
		return FluidVariantAttributes.getHandlerOrDefault(getFluid()).getEmptySound(FluidVariant.of(fluid)).orElse(previous);
	}
}
