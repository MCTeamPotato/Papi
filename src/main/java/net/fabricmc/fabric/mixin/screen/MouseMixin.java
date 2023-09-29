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

import net.fabricmc.fabric.impl.client.screen.MouseExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

@Mixin(Mouse.class)
public abstract class MouseMixin implements MouseExtensions {
	@Shadow
	@Final
	private MinecraftClient client;
	@Unique
	private Screen currentScreen;
	@Unique
	private Double horizontalScrollAmount;

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
	private void beforeMouseScrollEvent(long window, double horizontal, double vertical, CallbackInfo ci, double verticalAmount, double mouseX, double mouseY) {
		// Apply same calculations to horizontal scroll as vertical scroll amount has
		this.horizontalScrollAmount = this.client.options.discreteMouseScroll ? Math.signum(horizontal) : horizontal * this.client.options.mouseWheelSensitivity;
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void afterMouseScrollEvent(long window, double horizontal, double vertical, CallbackInfo ci, double verticalAmount, double mouseX, double mouseY) {
		this.horizontalScrollAmount = null;
	}

	@Override
	public double getHorizontalScroll() {
		return this.horizontalScrollAmount;
	}
}
