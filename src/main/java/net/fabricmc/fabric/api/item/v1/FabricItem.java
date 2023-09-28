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

package net.fabricmc.fabric.api.item.v1;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.extensions.IForgeItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * General-purpose Fabric-provided extensions for {@link Item} subclasses.
 *
 * <p>Note: This interface is automatically implemented on all items via Mixin and interface injection.
 *
 * <p>Note to maintainers: Functions should only be added to this interface if they are general-purpose enough,
 * to be evaluated on a case-by-case basis. Otherwise, they are better suited for more specialized APIs.
 */
public interface FabricItem extends IForgeItem {
	/**
	 * When the NBT of an item stack in the main hand or off hand changes, vanilla runs an "update animation".
	 * This function is called on the client side when the NBT or count of the stack has changed, but not the item,
	 * and returning false cancels this animation.
	 *
	 * @param player   the current player; this may be safely cast to {@link net.minecraft.client.network.ClientPlayerEntity} in client-only code
	 * @param hand     the hand; this function applies both to the main hand and the off hand
	 * @param oldStack the previous stack, of this item
	 * @param newStack the new stack, also of this item
	 * @return true to run the vanilla animation, false to cancel it.
	 */
	default boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return !FabricItemInternals.allowForgeCall() || shouldCauseReequipAnimation(oldStack, newStack, false);
	}

	/**
	 * When the NBT of the selected stack changes, block breaking progress is reset.
	 * This function is called when the NBT of the selected stack has changed,
	 * and returning true allows the block breaking progress to continue.
	 *
	 * @param player   the player breaking the block
	 * @param oldStack the previous stack, of this item
	 * @param newStack the new stack, also of this item
	 * @return true to allow continuing block breaking, false to reset the progress.
	 */
	default boolean allowContinuingBlockBreaking(@Nullable PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
		return FabricItemInternals.allowForgeCall() && !shouldCauseBlockBreakReset(oldStack, newStack);
	}

	/**
	 * Return the attribute modifiers to apply when this stack is worn in a living entity equipment slot.
	 * Stack-aware version of {@link Item#getAttributeModifiers(EquipmentSlot)}.
	 *
	 * <p>Note that attribute modifiers are only updated when the stack changes, i.e. when {@code ItemStack.areEqual(old, new)} is false.
	 *
	 * @param stack the current stack
	 * @param slot  the equipment slot this stack is in
	 * @return the attribute modifiers
	 */
	default Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		if (FabricItemInternals.allowForgeCall()) {
			return getAttributeModifiers(slot, stack);
		}
		return HashMultimap.create();
	}

	/**
	 * Determines if mining with this item allows drops to be harvested from the specified block state.
	 * Stack-aware version of {@link Item#isSuitableFor(BlockState)}.
	 *
	 * @param stack the current stack
	 * @param state the block state of the targeted block
	 * @return true if drops can be harvested
	 */
	default boolean isSuitableFor(ItemStack stack, BlockState state) {
		return FabricItemInternals.allowForgeCall() && isCorrectToolForDrops(stack, state);
	}

	/**
	 * Returns a leftover item stack after {@code stack} is consumed in a recipe.
	 * (This is also known as "recipe remainder".)
	 * For example, using a lava bucket in a furnace as fuel will leave an empty bucket.
	 *
	 * <p>Here is an example for a recipe remainder that increments the item's damage.
	 *
	 * <pre>
	 *  if (stack.getDamage() < stack.getMaxDamage() - 1) {
	 *  	ItemStack moreDamaged = stack.copy();
	 *  	moreDamaged.setDamage(stack.getDamage() + 1);
	 *  	return moreDamaged;
	 *  }
	 *
	 *  return ItemStack.EMPTY;
	 * </pre>
	 *
	 *
	 * <p>This is a stack-aware version of {@link Item#getRecipeRemainder()}.
	 *
	 * <p>Note that simple item remainders can also be set via {@link Item.Settings#recipeRemainder(Item)}.
	 *
	 * <p>If you want to get a remainder for a stack,
	 * is recommended to use the stack version of this method: {@link FabricItemStack#getRecipeRemainder()}.
	 *
	 * @param stack the consumed {@link ItemStack}
	 * @return the leftover item stack
	 */
	default ItemStack getRecipeRemainder(ItemStack stack) {
		if (FabricItemInternals.allowForgeCall()) {
			return getCraftingRemainingItem(stack);
		}
		return ItemStack.EMPTY;
	}

	@Override
	default ItemStack getCraftingRemainingItem(ItemStack stack) {
		ItemStack fabricRemainder = FabricItemInternals.nonRecursiveApiCall(() -> getRecipeRemainder(stack));
		if (!fabricRemainder.isEmpty()) {
			return fabricRemainder;
		}
		return IForgeItem.super.hasCraftingRemainingItem(stack) ? IForgeItem.super.getCraftingRemainingItem(stack) : ItemStack.EMPTY;
	}

	@Override
	default boolean hasCraftingRemainingItem(ItemStack stack) {
		return !FabricItemInternals.nonRecursiveApiCall(() -> getRecipeRemainder(stack)).isEmpty() || IForgeItem.super.hasCraftingRemainingItem(stack);
	}

	@Override
	default Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		// Fetch forge attribute modifiers first
		Multimap<EntityAttribute, EntityAttributeModifier> modifiers = HashMultimap.create(IForgeItem.super.getAttributeModifiers(slot, stack));
		// Add all fabric attribute modifiers
		modifiers.putAll(FabricItemInternals.nonRecursiveApiCall(() -> getAttributeModifiers(stack, slot)));
		return modifiers;
	}

	@Override
	default boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return FabricItemInternals.nonRecursiveApiCall(() -> isSuitableFor(stack, state)) || IForgeItem.super.isCorrectToolForDrops(stack, state);
	}

	@Override
	default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (IForgeItem.super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)) {
			PlayerEntity player = FabricItemInternals.getClientPlayer();
			if (player == null) return false;
			Hand hand = oldStack == player.getMainHandStack() ? Hand.MAIN_HAND : Hand.OFF_HAND;
			return FabricItemInternals.nonRecursiveApiCall(() -> allowNbtUpdateAnimation(player, hand, oldStack, newStack));
		}
		return false;
	}

	@Override
	default boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return IForgeItem.super.shouldCauseBlockBreakReset(oldStack, newStack) && FabricItemInternals.nonRecursiveApiCall(() -> !allowContinuingBlockBreaking(FabricItemInternals.getClientPlayer(), oldStack, newStack));
	}

	@Override
	@Nullable
	default EquipmentSlot getEquipmentSlot(ItemStack stack) {
		EquipmentSlotProvider equipmentSlotProvider = ((ItemExtensions) stack.getItem()).fabric_getEquipmentSlotProvider();
		return equipmentSlotProvider != null ? equipmentSlotProvider.getPreferredEquipmentSlot(stack) : IForgeItem.super.getEquipmentSlot(stack);
	}

	@Override
	@SuppressWarnings("unchecked")
	default <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		CustomDamageHandler handler = ((ItemExtensions) this).fabric_getCustomDamageHandler();
		if (handler != null) {
			return handler.damage(stack, amount, entity, (Consumer<LivingEntity>) onBroken);
		}
		return IForgeItem.super.damageItem(stack, amount, entity, onBroken);
	}
}
