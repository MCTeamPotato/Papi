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
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(World.class)
public abstract class WorldMixin {
	@Shadow
	public abstract boolean isClient();

	@Unique
	private World getThis() {
		return (World)(Object)this;
	}

	@Inject(method = "addBlockEntity", at = @At("TAIL"))
	protected void onLoadBlockEntity(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
		if (getThis() instanceof ServerWorld) { // Only fire this event if we are a server world
			ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.invoker().onLoad(blockEntity, (ServerWorld) getThis());
		}
	}

	// Mojang what the hell, why do you need three ways to unload block entities
	@Redirect(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", ordinal = 1, remap = false))
	protected boolean onUnloadBlockEntity(List<Object> blockEntityList, Object blockEntity) {
		if (getThis() instanceof ServerWorld && blockEntity instanceof BlockEntity) {
			ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload((BlockEntity) blockEntity, (ServerWorld) getThis());
		}
		return blockEntityList.remove(blockEntity);
	}

	@Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", remap = false))
	protected boolean onRemoveBlockEntity(List<Object> blockEntityList, Object blockEntity) {
		if (getThis() instanceof ServerWorld && blockEntity instanceof BlockEntity) {
			ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload((BlockEntity) blockEntity, (ServerWorld) getThis());
		}
		return blockEntityList.remove(blockEntity);
	}

	@Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;removeAll(Ljava/util/Collection;)Z", ordinal = 1, remap = false))
	protected boolean onPurgeRemovedBlockEntities(List<Object> blockEntityList, Collection<Object> removals) {
		if (getThis() instanceof ServerWorld) {
			for (Object removal : removals) {
				if (removal instanceof BlockEntity){
					ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload((BlockEntity) removal, (ServerWorld) getThis());
				}
			}
		}

		// Mimic vanilla logic
		return blockEntityList.removeAll(removals);
	}

	@Inject(at = @At("RETURN"), method = "tickBlockEntities")
	protected void tickWorldAfterBlockEntities(CallbackInfo ci) {
		if (getThis() instanceof ServerWorld) {
			ServerTickEvents.END_WORLD_TICK.invoker().onEndTick((ServerWorld) getThis());
		}
	}
}
