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

package net.fabricmc.fabric.mixin.client.rendering;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import net.fabricmc.fabric.impl.client.rendering.RegistrationHelperImpl;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = EntityRenderers.class, priority = 0)
public abstract class EntityRenderersMixin {
	@Shadow()
	@Final
	private static Map<EntityType<?>, EntityRendererFactory<?>> RENDERER_FACTORIES;

	@Shadow @Final private static Map<String, EntityRendererFactory<AbstractClientPlayerEntity>> PLAYER_RENDERER_FACTORIES;

	@Inject(method = "<clinit>*", at = @At(value = "RETURN"))
	private static void onRegisterRenderers(CallbackInfo info) {
		EntityRendererRegistryImpl.setup(((t, factory) -> RENDERER_FACTORIES.put(t, factory)));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Unique
	private static EntityRenderer<?> papi$createEntityRenderer(EntityRendererFactory<?> entityRendererFactory, EntityRendererFactory.Context context, EntityType<?> entityType) {
		EntityRenderer<?> entityRenderer = entityRendererFactory.create(context);

		if (entityRenderer instanceof LivingEntityRenderer) { // Must be living for features
			LivingEntityFeatureRendererRegistrationCallback.EVENT.invoker()
					.registerRenderers((EntityType<? extends LivingEntity>) entityType, (LivingEntityRenderer) entityRenderer,
							new RegistrationHelperImpl(((LivingEntityRenderer)entityRenderer)::addFeature), context);
		}

		return entityRenderer;
	}

	/**
	 * @author Kasualix
	 * @reason impl api
	 */
	@Overwrite
	public static Map<EntityType<?>, EntityRenderer<?>> reloadEntityRenderers(EntityRendererFactory.Context ctx) {
		ImmutableMap.Builder<EntityType<?>, EntityRenderer<?>> builder = ImmutableMap.builder();
		RENDERER_FACTORIES.forEach((entityType, factory) -> {
			try {
				builder.put(entityType, papi$createEntityRenderer(factory, ctx, entityType));
			} catch (Exception exception) {
				throw new IllegalArgumentException("Failed to create model for " + ForgeRegistries.ENTITY_TYPES.getKey(entityType), exception);
			}
		});
		return builder.build();
	}

	/**
	 * @author Kasualix
	 * @reason impl api
	 */
	@Overwrite
	public static Map<String, EntityRenderer<? extends PlayerEntity>> reloadPlayerRenderers(EntityRendererFactory.Context ctx) {
		ImmutableMap.Builder<String, EntityRenderer<? extends PlayerEntity>> builder = ImmutableMap.builder();
		PLAYER_RENDERER_FACTORIES.forEach((type, factory) -> {
			try {
				builder.put(type, papi$createPlayerEntityRenderer(factory, ctx));
			} catch (Exception exception) {
				throw new IllegalArgumentException("Failed to create player model for " + type, exception);
			}
		});
		return builder.build();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Unique
	private static EntityRenderer<? extends PlayerEntity> papi$createPlayerEntityRenderer(EntityRendererFactory<AbstractClientPlayerEntity> playerEntityRendererFactory, EntityRendererFactory.Context context) {
		EntityRenderer<? extends PlayerEntity> entityRenderer = playerEntityRendererFactory.create(context);
		LivingEntityFeatureRendererRegistrationCallback.EVENT.invoker().registerRenderers(EntityType.PLAYER,
				(LivingEntityRenderer) entityRenderer, new RegistrationHelperImpl(((LivingEntityRenderer)entityRenderer)::addFeature), context);

		return entityRenderer;
	}
}
