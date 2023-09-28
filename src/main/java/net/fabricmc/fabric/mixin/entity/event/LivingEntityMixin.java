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

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow
	public abstract boolean isDead();

	@Shadow
	public abstract Optional<BlockPos> getSleepingPosition();

	@Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onKilledOther(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onEntityKilledOther(DamageSource source, CallbackInfo ci, Entity attacker) {
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.invoker().afterKilledOtherEntity((ServerWorld) this.world, attacker, (LivingEntity) (Object) this);
	}

	@Inject(method = "onDeath", at = @At(value = "INVOKE", target = "net/minecraft/world/World.sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"))
	private void notifyDeath(DamageSource source, CallbackInfo ci) {
		ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath((LivingEntity) (Object) this, source);
	}

	@Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isDead()Z", ordinal = 1))
	boolean beforeEntityKilled(LivingEntity livingEntity, DamageSource source, float amount) {
		return isDead() && ServerLivingEntityEvents.ALLOW_DEATH.invoker().allowDeath(livingEntity, source, amount);
	}

	@Inject(method = "sleep", at = @At("RETURN"))
	private void onSleep(BlockPos pos, CallbackInfo info) {
		EntitySleepEvents.START_SLEEPING.invoker().onStartSleeping((LivingEntity) (Object) this, pos);
	}

	@Inject(method = "wakeUp", at = @At("HEAD"))
	private void onWakeUp(CallbackInfo info) {
		BlockPos sleepingPos = getSleepingPosition().orElse(null);

		// If actually asleep - this method is often called with data loading, syncing etc. "just to be sure"
		if (sleepingPos != null) {
			EntitySleepEvents.STOP_SLEEPING.invoker().onStopSleeping((LivingEntity) (Object) this, sleepingPos);
		}
	}

	@Inject(method = "getSleepingDirection", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void onGetSleepingDirection(CallbackInfoReturnable<Direction> info, @Nullable BlockPos sleepingPos) {
		if (sleepingPos != null) {
			info.setReturnValue(EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.invoker().modifySleepDirection((LivingEntity) (Object) this, sleepingPos, info.getReturnValue()));
		}
	}

	@Unique
	private BlockState fabric_originalState;

	// This is needed 1) so that the vanilla logic in wakeUp runs for modded beds and 2) for the injector below.
	// The injector is shared because method_18404 and sleep share much of the structure here.
	@Dynamic("method_18404: Synthetic lambda body for Optional.ifPresent in wakeUp")
	@ModifyVariable(method = {"method_18404", "lambda$stopSleeping$10", "func_241348_f_"}, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), require = 2)
	private BlockState modifyBedForOccupiedState(BlockState state, BlockPos sleepingPos) {
		LivingEntity entity = (LivingEntity) (Object) this;
		ActionResult result = EntitySleepEvents.ALLOW_BED.invoker().allowBed(entity, sleepingPos, state, state.isBed(this.getWorld(), sleepingPos, entity));
		if (result.isAccepted()) {
			fabric_originalState = state;
			return Blocks.RED_BED.getDefaultState();
		}
		fabric_originalState = null;
		// If a valid bed, replace with vanilla red bed so that the vanilla instanceof check succeeds.
		return state;
	}

	@ModifyVariable(method = "sleep", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), require = 2)
	private BlockState modifyBedForOccupiedStateSleep(BlockState state, BlockPos sleepingPos) {
		LivingEntity entity = (LivingEntity) (Object) this;
		ActionResult result = EntitySleepEvents.ALLOW_BED.invoker().allowBed(entity, sleepingPos, state, state.isBed(this.world, sleepingPos, entity));
		if (result.isAccepted()) {
			fabric_originalState = state;
			return Blocks.RED_BED.getDefaultState();
		}
		fabric_originalState = null;
		// If a valid bed, replace with vanilla red bed so that the vanilla instanceof check succeeds.
		return state;
	}

	// The injector is shared because method_18404 and sleep share much of the structure here.
	@Dynamic("method_18404: Synthetic lambda body for Optional.ifPresent in wakeUp")
	@Redirect(method = {"method_18404", "lambda$stopSleeping$10", "func_241348_f_"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;setBedOccupied(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;Z)V"), require = 2)
	private void setOccupiedState(BlockState state, World world, BlockPos pos, LivingEntity entity, boolean occupied) {
		// This might have been replaced by a red bed above, so we get it again.
		// Note that we *need* to replace it so the state.with(OCCUPIED, ...) call doesn't crash
		// when the bed doesn't have the property.
		BlockState originalState = fabric_originalState != null ? fabric_originalState : state;

		if (!EntitySleepEvents.SET_BED_OCCUPATION_STATE.invoker().setBedOccupationState((LivingEntity) (Object) this, pos, originalState, occupied)) {
			originalState.setBedOccupied(world, pos, entity, occupied);
		}
		fabric_originalState = null;
	}

	@Redirect(method = "sleep", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;setBedOccupied(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;Z)V"), require = 2)
	private void setOccupiedStateSleep(BlockState state, World world, BlockPos pos, LivingEntity entity, boolean occupied) {
		// This might have been replaced by a red bed above, so we get it again.
		// Note that we *need* to replace it so the state.with(OCCUPIED, ...) call doesn't crash
		// when the bed doesn't have the property.
		BlockState originalState = fabric_originalState != null ? fabric_originalState : state;

		if (!EntitySleepEvents.SET_BED_OCCUPATION_STATE.invoker().setBedOccupationState((LivingEntity) (Object) this, pos, originalState, occupied)) {
			originalState.setBedOccupied(world, pos, entity, occupied);
		}
		fabric_originalState = null;
	}

	@Dynamic("method_18404: Synthetic lambda body for Optional.ifPresent in wakeUp")
	@Redirect(method = {"method_18404", "lambda$stopSleeping$10", "func_241348_f_"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BedBlock;findWakeUpPosition(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/CollisionView;Lnet/minecraft/util/math/BlockPos;F)Ljava/util/Optional;"), require = 1)
	private Optional<Vec3d> modifyWakeUpPosition(EntityType<?> type, @NotNull CollisionView world, BlockPos pos, float yaw) {
		Optional<Vec3d> original = Optional.empty();
		BlockState bedState = world.getBlockState(pos);

		if (bedState.getBlock() instanceof BedBlock) original = BedBlock.findWakeUpPosition(type, world, pos, yaw);

		return Optional.ofNullable(EntitySleepEvents.MODIFY_WAKE_UP_POSITION.invoker().modifyWakeUpPosition((LivingEntity) (Object) this, pos, bedState, original.orElse(null)));
	}
}
