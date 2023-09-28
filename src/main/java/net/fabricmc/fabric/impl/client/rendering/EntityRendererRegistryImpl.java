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

package net.fabricmc.fabric.impl.client.rendering;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Helper class for registering EntityRenderers.
 */
@OnlyIn(Dist.CLIENT)
public final class EntityRendererRegistryImpl {
	private static final Map<EntityType<?>, EntityRendererFactory<?>> map = new Object2ObjectOpenHashMap<>();
	private static BiConsumer<EntityType<?>, EntityRendererFactory<?>> handler = map::put;

	public static <T extends Entity> void register(EntityType<? extends T> entityType, EntityRendererFactory<T> factory) {
		handler.accept(entityType, factory);
	}

	public static void setup(BiConsumer<EntityType<?>, EntityRendererFactory<?>> vanillaHandler) {
		map.forEach(vanillaHandler);
		handler = vanillaHandler;
	}

	private EntityRendererRegistryImpl() {
	}
}
