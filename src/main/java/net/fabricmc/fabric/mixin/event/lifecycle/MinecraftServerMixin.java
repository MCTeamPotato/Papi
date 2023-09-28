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

package net.fabricmc.fabric.mixin.event.lifecycle;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Shadow
	private MinecraftServer.ResourceManagerHolder resourceManagerHolder;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V"), method = "tick")
	private void onStartTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		ServerTickEvents.START_SERVER_TICK.invoker().onStartTick((MinecraftServer) (Object) this);
	}

	@Inject(at = @At("TAIL"), method = "tick")
	private void onEndTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
		ServerTickEvents.END_SERVER_TICK.invoker().onEndTick((MinecraftServer) (Object) this);
		ServerTickCallback.EVENT.invoker().tick((MinecraftServer) (Object) this);
	}

	@Inject(method = "reloadResources", at = @At("HEAD"))
	private void startResourceReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.invoker().startDataPackReload((MinecraftServer) (Object) this, this.resourceManagerHolder.resourceManager());
	}

	@Inject(method = "reloadResources", at = @At("TAIL"))
	private void endResourceReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		cir.getReturnValue().handleAsync((value, throwable) -> {
			// Hook into fail
			ServerLifecycleEvents.END_DATA_PACK_RELOAD.invoker().endDataPackReload((MinecraftServer) (Object) this, this.resourceManagerHolder.resourceManager(), throwable == null);
			return value;
		}, (MinecraftServer) (Object) this);
	}
}
