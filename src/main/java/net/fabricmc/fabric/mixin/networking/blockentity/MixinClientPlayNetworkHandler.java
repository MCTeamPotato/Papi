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

package net.fabricmc.fabric.mixin.networking.blockentity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	@Unique
	private static Logger FABRIC_LOGGER = LogManager.getLogger();

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;getBlockEntityType()I", ordinal = 0), method = "onBlockEntityUpdate", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo info, BlockPos blockPos, BlockEntity entity) {
		if (entity instanceof BlockEntityClientSerializable) {
			if (packet.getBlockEntityType() == 127) {
				BlockEntityClientSerializable serializable = (BlockEntityClientSerializable) entity;
				String id = packet.getCompoundTag().getString("id");

				if (id != null) {
					Identifier otherIdObj = BlockEntityType.getId(entity.getType());

					if (otherIdObj == null) {
						FABRIC_LOGGER.error(entity.getClass() + " is missing a mapping! This is a bug!");
						info.cancel();
						return;
					}

					String otherId = otherIdObj.toString();

					if (otherId.equals(id)) {
						serializable.fromClientTag(packet.getCompoundTag());
					}
				}
			}

			info.cancel();
		}
	}
}