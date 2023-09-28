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

package net.fabricmc.fabric.impl.item;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;
import java.util.function.Supplier;

public final class FabricItemInternals {
	public static final ThreadLocal<Boolean> FORGE_CALL = ThreadLocal.withInitial(() -> false);
	private static final WeakHashMap<Item.Settings, ExtraData> extraData = new WeakHashMap<>();

	private FabricItemInternals() {
	}

	public static ExtraData computeExtraData(Item.Settings settings) {
		return extraData.computeIfAbsent(settings, s -> new ExtraData());
	}

	public static void onBuild(Item.Settings settings, Item item) {
		ExtraData data = extraData.get(settings);

		if (data != null) {
			((ItemExtensions) item).fabric_setEquipmentSlotProvider(data.equipmentSlotProvider);
			((ItemExtensions) item).fabric_setCustomDamageHandler(data.customDamageHandler);
		}
	}

    public static void modifyItemAttributeModifiers(@NotNull ItemAttributeModifierEvent event) {
        ModifyItemAttributeModifiersCallback.EVENT.invoker().modifyAttributeModifiers(event.getItemStack(), event.getSlotType(), event.getModifiers());
    }

    public static @Nullable PlayerEntity getClientPlayer() {
        return MinecraftClient.getInstance().player;
    }

	public static <T> T nonRecursiveApiCall(Supplier<T> supplier) {
		FORGE_CALL.set(true);
		T result = supplier.get();
		FORGE_CALL.set(false);
		return result;
	}

	public static boolean allowForgeCall() {
		return !FORGE_CALL.get();
	}

    public static final class ExtraData {
		private @Nullable EquipmentSlotProvider equipmentSlotProvider;
		private @Nullable CustomDamageHandler customDamageHandler;

		public void equipmentSlot(EquipmentSlotProvider equipmentSlotProvider) {
			this.equipmentSlotProvider = equipmentSlotProvider;
		}

		public void customDamage(CustomDamageHandler handler) {
			this.customDamageHandler = handler;
		}
	}
}
