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

package net.fabricmc.fabric.mixin.entity.event;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity{
	protected PlayerEntityMixin(EntityType<? extends LivingEntity> arg, World arg2) {
		super(arg, arg2);
	}

	@Inject(method = "trySleep", at = @At("HEAD"), cancellable = true)
	private void onTrySleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> info) {
		PlayerEntity.SleepFailureReason failureReason = EntitySleepEvents.ALLOW_SLEEPING.invoker().allowSleep((PlayerEntity) (Object) this, pos);

		if (failureReason != null) {
			info.setReturnValue(Either.left(failureReason));
			info.cancel();
		}
	}

	@Inject(method = "isSleepingLongEnough", at = @At("RETURN"), cancellable = true)
	private void onIsSleepingLongEnough(CallbackInfoReturnable<Boolean> info) {
		if (info.getReturnValueZ()) {
			info.setReturnValue(EntitySleepEvents.ALLOW_RESETTING_TIME.invoker().allowResettingTime((PlayerEntity) (Object) this));
			info.cancel();
		}
	}
}