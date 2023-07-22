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
import net.minecraft.block.BedBlock;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(BedBlock.class)
abstract class BedBlockMixin {
	@Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Either;ifLeft(Ljava/util/function/Consumer;)Lcom/mojang/datafixers/util/Either;"))
	private Either<?, ?> onUse(Either<PlayerEntity.SleepFailureReason, ?> instance, Consumer<PlayerEntity.SleepFailureReason> consumer) {
		return instance.ifLeft(o -> {
			if (o.toText() == null) return;
			consumer.accept(o);
		});
	}
}
