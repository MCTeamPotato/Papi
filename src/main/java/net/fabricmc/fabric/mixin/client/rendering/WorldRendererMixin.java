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

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.impl.client.rendering.WorldRenderContextImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
	@Final
	@Shadow
	private BufferBuilderStorage bufferBuilders;
	@Shadow private ClientWorld world;
	@Shadow private ShaderEffect transparencyShader;
	@Final
	@Shadow
	private MinecraftClient client;

	@Unique private WorldRenderContextImpl papi$context;

	@Unique private boolean papi$didRenderParticles;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BufferBuilderStorage bufferBuilders, CallbackInfo ci) {
		this.papi$context = new WorldRenderContextImpl();
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void beforeRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		papi$context.prepare((WorldRenderer) (Object) this, matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f, bufferBuilders.getEntityVertexConsumers(), world.getProfiler(), transparencyShader != null, world);
		WorldRenderEvents.START.invoker().onStart(papi$context);
		papi$didRenderParticles = false;
	}

	@Inject(method = "setupTerrain", at = @At("RETURN"))
	private void afterTerrainSetup(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator, CallbackInfo ci) {
		papi$context.setFrustum(frustum);
		WorldRenderEvents.AFTER_SETUP.invoker().afterSetup(papi$context);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDDLnet/minecraft/util/math/Matrix4f;)V", ordinal = 2, shift = Shift.AFTER))
	private void afterTerrainSolid(CallbackInfo ci) {
		WorldRenderEvents.BEFORE_ENTITIES.invoker().beforeEntities(papi$context);
	}

	@Inject(method = "render", at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0))
	private void afterEntities(CallbackInfo ci) {
		WorldRenderEvents.AFTER_ENTITIES.invoker().afterEntities(papi$context);
	}

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", shift = At.Shift.AFTER, ordinal = 1))
	private void beforeRenderOutline(CallbackInfo ci) {
		papi$context.renderBlockOutline = WorldRenderEvents.BEFORE_BLOCK_OUTLINE.invoker().beforeBlockOutline(papi$context, client.crosshairTarget);
	}

	@Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
	private void onDrawBlockOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (!papi$context.renderBlockOutline) {
			// Was cancelled before we got here, so do not
			// fire the BLOCK_OUTLINE event per contract of the API.
			ci.cancel();
		} else {
			papi$context.prepareBlockOutline(entity, cameraX, cameraY, cameraZ, blockPos, blockState);

			if (!WorldRenderEvents.BLOCK_OUTLINE.invoker().onBlockOutline(papi$context, papi$context)) {
				ci.cancel();
			}


			// The immediate mode VertexConsumers use a shared buffer, so we have to make sure that the immediate mode VCP
			// can accept block outline lines rendered to the existing vertexConsumer by the vanilla block overlay.
			VertexConsumerProvider provider = papi$context.consumers();
			if (provider == null) return;
			provider.getBuffer(RenderLayer.getLines());
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/debug/DebugRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDD)V", ordinal = 0))
	private void beforeDebugRender(CallbackInfo ci) {
		WorldRenderEvents.BEFORE_DEBUG_RENDER.invoker().beforeDebugRender(papi$context);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/render/Frustum;)V"))
	private void onRenderParticles(CallbackInfo ci) {
		// set a flag so we know the next pushMatrix call is after particles
		papi$didRenderParticles = true;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"))
	private void beforeClouds(CallbackInfo ci) {
		if (papi$didRenderParticles) {
			papi$didRenderParticles = false;
			WorldRenderEvents.AFTER_TRANSLUCENT.invoker().afterTranslucent(papi$context);
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderChunkDebugInfo(Lnet/minecraft/client/render/Camera;)V"))
	private void onChunkDebugRender(CallbackInfo ci) {
		WorldRenderEvents.LAST.invoker().onLast(papi$context);
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void afterRender(CallbackInfo ci) {
		WorldRenderEvents.END.invoker().onEnd(papi$context);
	}

	@Inject(method = "reload()V", at = @At("HEAD"))
	private void onReload(CallbackInfo ci) {
		InvalidateRenderStateCallback.EVENT.invoker().onInvalidate();
	}

	@Inject(at = @At("HEAD"), method = "renderWeather", cancellable = true)
	private void renderWeather(LightmapTextureManager manager, float tickDelta, double x, double y, double z, CallbackInfo info) {
		if (this.client.world != null) {
			DimensionRenderingRegistry.WeatherRenderer renderer = DimensionRenderingRegistry.getWeatherRenderer(world.getRegistryKey());

			if (renderer != null) {
				renderer.render(papi$context);
				info.cancel();
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FDDD)V", cancellable = true)
	private void renderCloud(MatrixStack matrices, Matrix4f matrix4f, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
		if (this.client.world != null) {
			DimensionRenderingRegistry.CloudRenderer renderer = DimensionRenderingRegistry.getCloudRenderer(world.getRegistryKey());

			if (renderer != null) {
				renderer.render(papi$context);
				info.cancel();
			}
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V", shift = At.Shift.AFTER, ordinal = 0), method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", cancellable = true)
	private void renderSky(MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo info) {
		if (this.client.world != null) {
			DimensionRenderingRegistry.SkyRenderer renderer = DimensionRenderingRegistry.getSkyRenderer(world.getRegistryKey());

			if (renderer != null) {
				renderer.render(papi$context);
				info.cancel();
			}
		}
	}
}