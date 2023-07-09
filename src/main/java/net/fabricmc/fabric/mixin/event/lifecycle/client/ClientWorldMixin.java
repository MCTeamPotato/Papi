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

package net.fabricmc.fabric.mixin.event.lifecycle.client;

import net.fabricmc.fabric.mixin.event.lifecycle.WorldMixin;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends WorldMixin {
	// Call our load event after vanilla has loaded the entity
	@Inject(method = "addEntityPrivate", at = @At("TAIL"))
	private void onEntityLoad(int id, Entity entity, CallbackInfo ci) {
		ClientEntityEvents.ENTITY_LOAD.invoker().onLoad(entity, getThis());
	}

	// Call our unload event before vanilla does.
	@Inject(method = "finishRemovingEntity", at = @At("HEAD"))
	private void onEntityUnload(Entity entity, CallbackInfo ci) {
		ClientEntityEvents.ENTITY_UNLOAD.invoker().onUnload(entity, getThis());
	}

	// We override our injection on the client world so only the client's block entity invocations will run
	@Override
	protected void onLoadBlockEntity(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.invoker().onLoad(blockEntity, getThis());
	}

	// We override our injection on the client world so only the client's block entity invocations will run
	@Override
	protected boolean onUnloadBlockEntity(List<Object> blockEntityList, Object blockEntity) {
		if (blockEntity instanceof BlockEntity){
			ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload((BlockEntity) blockEntity, getThis());
		}
		return super.onUnloadBlockEntity(blockEntityList, blockEntity);
	}

	@Override
	protected boolean onRemoveBlockEntity(List<Object> blockEntityList, Object blockEntity) {
		if (blockEntity instanceof BlockEntity){
			ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload((BlockEntity) blockEntity, getThis());
		}
		return super.onRemoveBlockEntity(blockEntityList, blockEntity);
	}

	@Override
	protected boolean onPurgeRemovedBlockEntities(List<Object> blockEntityList, Collection<Object> removals) {
		for (Object removal : removals) {
			if (removal instanceof BlockEntity){
				ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload((BlockEntity) removal, getThis());
			}
		}

		return super.onPurgeRemovedBlockEntities(blockEntityList, removals); // Call super
	}

	// We override our injection on the client world so only the client world's tick invocations will run
	@Override
	protected void tickWorldAfterBlockEntities(CallbackInfo ci) {
		ClientTickEvents.END_WORLD_TICK.invoker().onEndTick(getThis());
	}

	@Inject(method = "tickEntities", at = @At("HEAD"))
	private void startWorldTick(CallbackInfo ci) {
		ClientTickEvents.START_WORLD_TICK.invoker().onStartTick(getThis());
	}

	private ClientWorld getThis() {
		return (ClientWorld) (Object)this;
	}
}
