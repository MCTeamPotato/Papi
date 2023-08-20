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

package net.fabricmc.fabric.mixin.item;

import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;

import net.fabricmc.fabric.impl.item.RecipeRemainderHandler;

@Mixin(Recipe.class)
public interface RecipeMixin<C extends Inventory> {
	/**
	 * @author Kasualix, TexTrue
	 * @reason impl api
	 */
	@Overwrite
	default DefaultedList<ItemStack> getRemainder(C inventory) {
		DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
		for (int index = 0; index < itemStacks.size(); ++index) {
			ItemStack item = inventory.getStack(index);
			RecipeRemainderHandler.REMAINDER_STACK.set(((FabricItemStack)inventory.getStack(index)).getRecipeRemainder());
			if (!item.hasContainerItem() || RecipeRemainderHandler.REMAINDER_STACK.get().isEmpty()) continue;
			itemStacks.set(index, RecipeRemainderHandler.REMAINDER_STACK.get());
			RecipeRemainderHandler.REMAINDER_STACK.remove();
		}
		return itemStacks;
	}

	/*
	@Inject(method = "getRemainder", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD)
	default void captureStack(C inventory, CallbackInfoReturnable<DefaultedList<ItemStack>> cir, DefaultedList<ItemStack> defaultedList, int i) {
		RecipeRemainderHandler.REMAINDER_STACK.set(inventory.getStack(i).getRecipeRemainder());
	}

	@Redirect(method = "getRemainder", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;hasRecipeRemainder()Z"))
	private boolean hasStackRemainder(Item instance) {
		return !RecipeRemainderHandler.REMAINDER_STACK.get().isEmpty();
	}

	@Redirect(method = "getRemainder", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
	private Object getStackRemainder(DefaultedList<ItemStack> inventory, int index, Object element) {
		Object remainder = inventory.set(index, RecipeRemainderHandler.REMAINDER_STACK.get());
		RecipeRemainderHandler.REMAINDER_STACK.remove();
		return remainder;
	}
	 */
}
