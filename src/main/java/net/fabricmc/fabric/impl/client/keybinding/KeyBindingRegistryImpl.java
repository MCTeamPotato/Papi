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

package net.fabricmc.fabric.impl.client.keybinding;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccessor;
import net.minecraft.client.option.KeyBinding;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class KeyBindingRegistryImpl {
	private static final List<KeyBinding> MODDED_KEY_BINDINGS = new ReferenceArrayList<>(); // ArrayList with identity based comparisons for contains/remove/indexOf etc., required for correctly handling duplicate keybinds

	private KeyBindingRegistryImpl() {
	}

	private static Map<String, Integer> getCategoryMap() {
		return KeyBindingAccessor.fabric_getCategoryMap();
	}

	@SuppressWarnings("UnusedReturnValue")
	public static boolean addCategory(String categoryTranslationKey) {
		Map<String, Integer> map = getCategoryMap();

		if (map.containsKey(categoryTranslationKey)) {
			return false;
		}

		Optional<Integer> largest = map.values().stream().max(Integer::compareTo);
		int largestInt = largest.orElse(0);
		map.put(categoryTranslationKey, largestInt + 1);
		return true;
	}

	@Contract("_ -> param1")
	public static @NotNull KeyBinding registerKeyBinding(KeyBinding binding) {
		for (KeyBinding existingKeyBindings : MODDED_KEY_BINDINGS) {
			if (existingKeyBindings == binding) {
				throw new IllegalArgumentException("Attempted to register a key binding twice: " + binding.getTranslationKey());
			} else if (existingKeyBindings.getTranslationKey().equals(binding.getTranslationKey())) {
				throw new IllegalArgumentException("Attempted to register two key bindings with equal ID: " + binding.getTranslationKey() + "!");
			}
		}

		// This will do nothing if the category already exists.
		addCategory(binding.getCategory());
		MODDED_KEY_BINDINGS.add(binding);
		return binding;
	}

	public static void registerKeys(@NotNull RegisterKeyMappingsEvent event) {
		MODDED_KEY_BINDINGS.forEach(event::register);
	}
}
