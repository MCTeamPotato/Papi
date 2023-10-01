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

package net.fabricmc.fabric.mixin.mininglevel;

import net.fabricmc.fabric.impl.mininglevel.MiningLevelManagerImpl;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("deprecation")
@Mixin(MiningToolItem.class)
public abstract class MiningToolItemMixin extends ToolItem {
	public MiningToolItemMixin(ToolMaterial material, Settings settings) {
		super(material, settings);
	}

	@Inject(method = "isSuitableFor(Lnet/minecraft/block/BlockState;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/MiningToolItem;getMaterial()Lnet/minecraft/item/ToolMaterial;", ordinal = 0), cancellable = true)
	private void fabric$onIsSuitableFor(BlockState state, CallbackInfoReturnable<Boolean> info) {
		if (this.getMaterial().getMiningLevel() < MiningLevelManagerImpl.getRequiredFabricMiningLevel(state)) {
			info.setReturnValue(false);
		}
	}

	@Inject(method = "isCorrectToolForDrops(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/MiningToolItem;getMaterial()Lnet/minecraft/item/ToolMaterial;"), cancellable = true)
	private void fabric$onForgeIsSuitableFor(ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> info) {
		if (this.getMaterial().getMiningLevel() < MiningLevelManagerImpl.getRequiredFabricMiningLevel(state)) {
			info.setReturnValue(false);
		}
	}
}