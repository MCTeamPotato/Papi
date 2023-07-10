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

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.world.ServerWorld;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

@Mixin(TeleportCommand.class)
abstract class TeleportCommandMixin {
	/**
	 * We need to fire the change world event for entities that are teleported using the `/teleport` command.
	 */
	@Inject(method = "teleport", at = @At(
			value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;onDimensionChanged(Lnet/minecraft/entity/Entity;)V", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD
	), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void afterEntityTeleportedToWorld(
			ServerCommandSource source, Entity target, ServerWorld world, double x, double y, double z,
			Set<PlayerPositionLookS2CPacket.Flag> movementFlags, float yaw, float pitch, @Coerce Object facingLocation, CallbackInfo ci
	) {
		ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.invoker().afterChangeWorld(target, target, ((ServerWorld) target.world), world);
	}
}