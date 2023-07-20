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

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Shadow private ServerResourceManager serverResourceManager;

	@Unique
	private MinecraftServer getThis() {
		return (MinecraftServer) (Object) this;
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"), method = "runServer")
	private void beforeSetupServer(CallbackInfo info) {
		ServerLifecycleEvents.SERVER_STARTING.invoker().onServerStarting(getThis());
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V", ordinal = 0), method = "runServer")
	private void afterSetupServer(CallbackInfo info) {
		ServerLifecycleEvents.SERVER_STARTED.invoker().onServerStarted(getThis());
	}

	@Inject(at = @At("HEAD"), method = "shutdown")
	private void beforeShutdownServer(CallbackInfo info) {
		ServerLifecycleEvents.SERVER_STOPPING.invoker().onServerStopping(getThis());
	}

	@Inject(at = @At("TAIL"), method = "shutdown")
	private void afterShutdownServer(CallbackInfo info) {
		ServerLifecycleEvents.SERVER_STOPPED.invoker().onServerStopped(getThis());
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V"), method = "tick")
	private void onStartTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		ServerTickEvents.START_SERVER_TICK.invoker().onStartTick(getThis());
	}

	@Inject(at = @At("TAIL"), method = "tick")
	private void onEndTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
		ServerTickEvents.END_SERVER_TICK.invoker().onEndTick(getThis());
	}

	/**
	 * When a world is closed, it means the world will be unloaded.
	 */
	@Redirect(method = "shutdown", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z", remap = false))
	private boolean closeWorld(IEventBus iEventBus, Event event) {
		if (!(event instanceof WorldEvent.Unload)) throw new RuntimeException();//This should not happen

		if (((WorldEvent.Unload) event).getWorld() instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld) ((WorldEvent.Unload) event).getWorld();
			ServerWorldEvents.UNLOAD.invoker().onWorldUnload(getThis(), serverWorld);

			for (BlockEntity blockEntity : serverWorld.blockEntities) {
				ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(blockEntity, serverWorld);
			}
		}

		return MinecraftForge.EVENT_BUS.post(event);
	}

	// The locals you have to manage for an inject are insane. And do it twice. A redirect is much cleaner.
	// Here is what it looks like with an inject: https://gist.github.com/i509VCB/f80077cc536eb4dba62b794eba5611c1
	@Redirect(method = "createWorlds", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private <K, V> V onLoadWorld(Map<K, V> worlds, K registryKey, V serverWorld) {
		final V result = worlds.put(registryKey, serverWorld);
		ServerWorldEvents.LOAD.invoker().onWorldLoad(getThis(), (ServerWorld) serverWorld);

		return result;
	}

	@Inject(method = "reloadResources", at = @At("HEAD"))
	private void startResourceReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.invoker().startDataPackReload(getThis(), this.serverResourceManager);
	}

	@Inject(method = "reloadResources", at = @At("TAIL"))
	private void endResourceReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		cir.getReturnValue().handleAsync((value, throwable) -> {
			// Hook into fail
			ServerLifecycleEvents.END_DATA_PACK_RELOAD.invoker().endDataPackReload(getThis(), this.serverResourceManager, throwable == null);
			return value;
		}, getThis());
	}
}
