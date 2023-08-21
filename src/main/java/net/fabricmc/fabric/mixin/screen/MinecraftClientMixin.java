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

package net.fabricmc.fabric.mixin.screen;

import net.minecraft.client.RunArgs;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {
	@Unique
	private static boolean DEBUG_SCREEN = false;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(RunArgs args, CallbackInfo ci) {
		DEBUG_SCREEN = !FMLEnvironment.production || Boolean.getBoolean("fabric.debugScreen");
	}

	@Shadow
	public Screen currentScreen;

	@Unique
	private Screen tickingScreen;

	@Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;removed()V", shift = At.Shift.AFTER))
	private void onScreenRemove(@Nullable Screen screen, CallbackInfo ci) {
		ScreenEvents.remove(this.currentScreen).invoker().onRemove(this.currentScreen);
	}

	@Inject(method = "stop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;removed()V", shift = At.Shift.AFTER))
	private void onScreenRemoveBecauseStopping(CallbackInfo ci) {
		ScreenEvents.remove(this.currentScreen).invoker().onRemove(this.currentScreen);
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"))
	private void onTick(Runnable task, String errorTitle, String screenName) {
		Screen.wrapScreenError(() -> {
			this.tickingScreen = this.currentScreen;
			ScreenEvents.beforeTick(this.tickingScreen).invoker().beforeTick(this.tickingScreen);

			this.currentScreen.tick();

			ScreenEvents.afterTick(this.tickingScreen).invoker().afterTick(this.tickingScreen);
			this.tickingScreen = null;
		}, "Ticking screen", this.currentScreen.getClass().getCanonicalName());
	}
}
