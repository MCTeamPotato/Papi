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

package net.fabricmc.fabric.impl.registry.sync;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.mixin.registry.sync.RegistryAccessor;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.ArrayList;
import java.util.List;

public class FabricRegistryInit {
	public static void init() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> RegistrySyncManager.sendPacket(server, handler.player));
	}

	private static final List<MutableRegistry<?>> REGISTRIES = new ArrayList<>();

	public static void addRegistry(MutableRegistry<?> registry) {
		REGISTRIES.add(registry);
	}

	public static void submitRegistries() {
		if (Registry.REGISTRIES instanceof SimpleRegistry<?> rootRegistry)
			rootRegistry.unfreeze();

		REGISTRIES.forEach(registry -> RegistryAccessor.getROOT().add((RegistryKey<MutableRegistry<?>>) registry.getKey(), registry, Lifecycle.stable()));

		if (Registry.REGISTRIES instanceof SimpleRegistry<?> rootRegistry)
			rootRegistry.freeze();
	}
}
