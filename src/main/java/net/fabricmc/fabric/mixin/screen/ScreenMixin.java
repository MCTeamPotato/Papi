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

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.client.screen.ButtonList;
import net.fabricmc.fabric.impl.client.screen.ScreenEventFactory;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
abstract class ScreenMixin implements ScreenExtensions {
	@Shadow
	@Final
	protected List<AbstractButtonWidget> buttons;
	@Shadow
	@Final
	protected List<Element> children;

	@Unique
	private ButtonList<AbstractButtonWidget> papi$fabricButtons;
	@Unique
	private Event<ScreenEvents.Remove> papi$removeEvent;
	@Unique
	private Event<ScreenEvents.BeforeTick> papi$beforeTickEvent;
	@Unique
	private Event<ScreenEvents.AfterTick> papi$afterTickEvent;
	@Unique
	private Event<ScreenEvents.BeforeRender> papi$beforeRenderEvent;
	@Unique
	private Event<ScreenEvents.AfterRender> papi$afterRenderEvent;

	// Keyboard
	@Unique
	private Event<ScreenKeyboardEvents.AllowKeyPress> papi$allowKeyPressEvent;
	@Unique
	private Event<ScreenKeyboardEvents.BeforeKeyPress> papi$beforeKeyPressEvent;
	@Unique
	private Event<ScreenKeyboardEvents.AfterKeyPress> papi$afterKeyPressEvent;
	@Unique
	private Event<ScreenKeyboardEvents.AllowKeyRelease> papi$allowKeyReleaseEvent;
	@Unique
	private Event<ScreenKeyboardEvents.BeforeKeyRelease> papi$beforeKeyReleaseEvent;
	@Unique
	private Event<ScreenKeyboardEvents.AfterKeyRelease> papi$afterKeyReleaseEvent;

	// Mouse
	@Unique
	private Event<ScreenMouseEvents.AllowMouseClick> papi$allowMouseClickEvent;
	@Unique
	private Event<ScreenMouseEvents.BeforeMouseClick> papi$beforeMouseClickEvent;
	@Unique
	private Event<ScreenMouseEvents.AfterMouseClick> papi$afterMouseClickEvent;
	@Unique
	private Event<ScreenMouseEvents.AllowMouseRelease> papi$allowMouseReleaseEvent;
	@Unique
	private Event<ScreenMouseEvents.BeforeMouseRelease> papi$beforeMouseReleaseEvent;
	@Unique
	private Event<ScreenMouseEvents.AfterMouseRelease> papi$afterMouseReleaseEvent;
	@Unique
	private Event<ScreenMouseEvents.AllowMouseScroll> papi$allowMouseScrollEvent;
	@Unique
	private Event<ScreenMouseEvents.BeforeMouseScroll> papi$beforeMouseScrollEvent;
	@Unique
	private Event<ScreenMouseEvents.AfterMouseScroll> papi$afterMouseScrollEvent;

	@Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
	private void beforeInitScreen(MinecraftClient client, int width, int height, CallbackInfo ci) {
		// All elements are repopulated on the screen, so we need to reinitialize all events
		this.papi$fabricButtons = null;
		this.papi$removeEvent = ScreenEventFactory.createRemoveEvent();
		this.papi$beforeRenderEvent = ScreenEventFactory.createBeforeRenderEvent();
		this.papi$afterRenderEvent = ScreenEventFactory.createAfterRenderEvent();
		this.papi$beforeTickEvent = ScreenEventFactory.createBeforeTickEvent();
		this.papi$afterTickEvent = ScreenEventFactory.createAfterTickEvent();

		// Keyboard
		this.papi$allowKeyPressEvent = ScreenEventFactory.createAllowKeyPressEvent();
		this.papi$beforeKeyPressEvent = ScreenEventFactory.createBeforeKeyPressEvent();
		this.papi$afterKeyPressEvent = ScreenEventFactory.createAfterKeyPressEvent();
		this.papi$allowKeyReleaseEvent = ScreenEventFactory.createAllowKeyReleaseEvent();
		this.papi$beforeKeyReleaseEvent = ScreenEventFactory.createBeforeKeyReleaseEvent();
		this.papi$afterKeyReleaseEvent = ScreenEventFactory.createAfterKeyReleaseEvent();

		// Mouse
		this.papi$allowMouseClickEvent = ScreenEventFactory.createAllowMouseClickEvent();
		this.papi$beforeMouseClickEvent = ScreenEventFactory.createBeforeMouseClickEvent();
		this.papi$afterMouseClickEvent = ScreenEventFactory.createAfterMouseClickEvent();
		this.papi$allowMouseReleaseEvent = ScreenEventFactory.createAllowMouseReleaseEvent();
		this.papi$beforeMouseReleaseEvent = ScreenEventFactory.createBeforeMouseReleaseEvent();
		this.papi$afterMouseReleaseEvent = ScreenEventFactory.createAfterMouseReleaseEvent();
		this.papi$allowMouseScrollEvent = ScreenEventFactory.createAllowMouseScrollEvent();
		this.papi$beforeMouseScrollEvent = ScreenEventFactory.createBeforeMouseScrollEvent();
		this.papi$afterMouseScrollEvent = ScreenEventFactory.createAfterMouseScrollEvent();

		ScreenEvents.BEFORE_INIT.invoker().beforeInit(client, (Screen) (Object) this, width, height);
	}

	@Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
	private void afterInitScreen(MinecraftClient client, int width, int height, CallbackInfo ci) {
		ScreenEvents.AFTER_INIT.invoker().afterInit(client, (Screen) (Object) this, width, height);
	}

	@Override
	public List<AbstractButtonWidget> fabric_getButtons() {
		// Lazy init to make the list access safe after Screen#init
		if (this.papi$fabricButtons == null) {
			this.papi$fabricButtons = new ButtonList<>((Screen) (Object) this, this.buttons, this.children);
		}

		return this.papi$fabricButtons;
	}

	@Unique
	private <T> Event<T> papi$ensureEventsAreInitialised(Event<T> event) {
		if (event == null) {
			throw new IllegalStateException(String.format("[fabric-screen-api-v1] The current screen (%s) has not been correctly initialised, please send this crash log to the mod author. This is usually caused by the screen not calling super.init(Lnet/minecraft/client/MinecraftClient;II)V", this.getClass().getSuperclass().getName()));
		}

		return event;
	}

	@Override
	public Event<ScreenEvents.Remove> fabric_getRemoveEvent() {
		return papi$ensureEventsAreInitialised(this.papi$removeEvent);
	}

	@Override
	public Event<ScreenEvents.BeforeTick> fabric_getBeforeTickEvent() {
		return papi$ensureEventsAreInitialised(this.papi$beforeTickEvent);
	}

	@Override
	public Event<ScreenEvents.AfterTick> fabric_getAfterTickEvent() {
		return papi$ensureEventsAreInitialised(this.papi$afterTickEvent);
	}

	@Override
	public Event<ScreenEvents.BeforeRender> fabric_getBeforeRenderEvent() {
		return papi$ensureEventsAreInitialised(this.papi$beforeRenderEvent);
	}

	@Override
	public Event<ScreenEvents.AfterRender> fabric_getAfterRenderEvent() {
		return papi$ensureEventsAreInitialised(this.papi$afterRenderEvent);
	}

	// Keyboard

	@Override
	public Event<ScreenKeyboardEvents.AllowKeyPress> fabric_getAllowKeyPressEvent() {
		return papi$ensureEventsAreInitialised(this.papi$allowKeyPressEvent);
	}

	@Override
	public Event<ScreenKeyboardEvents.BeforeKeyPress> fabric_getBeforeKeyPressEvent() {
		return papi$ensureEventsAreInitialised(this.papi$beforeKeyPressEvent);
	}

	@Override
	public Event<ScreenKeyboardEvents.AfterKeyPress> fabric_getAfterKeyPressEvent() {
		return papi$ensureEventsAreInitialised(this.papi$afterKeyPressEvent);
	}

	@Override
	public Event<ScreenKeyboardEvents.AllowKeyRelease> fabric_getAllowKeyReleaseEvent() {
		return papi$ensureEventsAreInitialised(this.papi$allowKeyReleaseEvent);
	}

	@Override
	public Event<ScreenKeyboardEvents.BeforeKeyRelease> fabric_getBeforeKeyReleaseEvent() {
		return papi$ensureEventsAreInitialised(this.papi$beforeKeyReleaseEvent);
	}

	@Override
	public Event<ScreenKeyboardEvents.AfterKeyRelease> fabric_getAfterKeyReleaseEvent() {
		return papi$ensureEventsAreInitialised(this.papi$afterKeyReleaseEvent);
	}

	// Mouse

	@Override
	public Event<ScreenMouseEvents.AllowMouseClick> fabric_getAllowMouseClickEvent() {
		return papi$ensureEventsAreInitialised(this.papi$allowMouseClickEvent);
	}

	@Override
	public Event<ScreenMouseEvents.BeforeMouseClick> fabric_getBeforeMouseClickEvent() {
		return papi$ensureEventsAreInitialised(this.papi$beforeMouseClickEvent);
	}

	@Override
	public Event<ScreenMouseEvents.AfterMouseClick> fabric_getAfterMouseClickEvent() {
		return papi$ensureEventsAreInitialised(this.papi$afterMouseClickEvent);
	}

	@Override
	public Event<ScreenMouseEvents.AllowMouseRelease> fabric_getAllowMouseReleaseEvent() {
		return papi$ensureEventsAreInitialised(this.papi$allowMouseReleaseEvent);
	}

	@Override
	public Event<ScreenMouseEvents.BeforeMouseRelease> fabric_getBeforeMouseReleaseEvent() {
		return papi$ensureEventsAreInitialised(this.papi$beforeMouseReleaseEvent);
	}

	@Override
	public Event<ScreenMouseEvents.AfterMouseRelease> fabric_getAfterMouseReleaseEvent() {
		return papi$ensureEventsAreInitialised(this.papi$afterMouseReleaseEvent);
	}

	@Override
	public Event<ScreenMouseEvents.AllowMouseScroll> fabric_getAllowMouseScrollEvent() {
		return papi$ensureEventsAreInitialised(this.papi$allowMouseScrollEvent);
	}

	@Override
	public Event<ScreenMouseEvents.BeforeMouseScroll> fabric_getBeforeMouseScrollEvent() {
		return papi$ensureEventsAreInitialised(this.papi$beforeMouseScrollEvent);
	}

	@Override
	public Event<ScreenMouseEvents.AfterMouseScroll> fabric_getAfterMouseScrollEvent() {
		return papi$ensureEventsAreInitialised(this.papi$afterMouseScrollEvent);
	}
}
