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

package net.fabricmc.fabric.mixin.networking.client;

import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.client.ClientLoginNetworkAddon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.text.Text;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLoginNetworkHandler.class)
abstract class ClientLoginNetworkHandlerMixin implements NetworkHandlerExtensions {
	@Shadow
	@Final
	private MinecraftClient client;

	@Unique
	private ClientLoginNetworkAddon papi$addon;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initAddon(CallbackInfo ci) {
		this.papi$addon = new ClientLoginNetworkAddon((ClientLoginNetworkHandler) (Object) this, this.client);
	}

	@Redirect(method = "onQueryRequest", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/network/NetworkHooks;onCustomPayload(Lnet/minecraftforge/network/ICustomPacket;Lnet/minecraft/network/ClientConnection;)Z"))
	private boolean handleQueryRequest(ICustomPacket<?> packet, final ClientConnection connection) {
		return this.papi$addon.handlePacket((LoginQueryRequestS2CPacket) packet) || NetworkHooks.onCustomPayload(packet, connection);
	}

	@Inject(method = "onDisconnected", at = @At("HEAD"))
	private void invokeLoginDisconnectEvent(Text reason, CallbackInfo ci) {
		this.papi$addon.handleDisconnect();
	}

	@Inject(method = "onSuccess", at = @At("HEAD"))
	private void handlePlayTransition(CallbackInfo ci) {
		papi$addon.handlePlayTransition();
	}

	@Override
	public ClientLoginNetworkAddon getAddon() {
		return this.papi$addon;
	}
}
