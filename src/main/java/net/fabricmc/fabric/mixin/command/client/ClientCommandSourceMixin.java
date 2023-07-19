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

package net.fabricmc.fabric.mixin.command.client;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientCommandSource.class)
abstract class ClientCommandSourceMixin implements FabricClientCommandSource {
	@Shadow
	@Final
	private MinecraftClient client;

	@Override
	public void papi$sendFeedback(Text message) {
		this.client.inGameHud.getChatHud().addMessage(message);
		this.client.getNarratorManager().narrate(message);
	}

	@Override
	public void papi$sendError(Text message) {
		papi$sendFeedback(Text.literal("").append(message).formatted(Formatting.RED));
	}

	@Override
	public MinecraftClient papi$getClient() {
		return client;
	}

	@Override
	public ClientPlayerEntity papi$getPlayer() {
		return client.player;
	}

	@Override
	public ClientWorld papi$getWorld() {
		return client.world;
	}
}