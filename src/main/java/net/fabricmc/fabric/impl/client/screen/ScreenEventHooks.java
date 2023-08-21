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

package net.fabricmc.fabric.impl.client.screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ScreenEventHooks {

    @SubscribeEvent
    public static void beforeScreenDraw(ScreenEvent.Render.Pre event) {
        Screen screen = event.getScreen();
        ScreenEvents.beforeRender(screen).invoker().beforeRender(screen, event.getPoseStack(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void afterScreenDraw(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        ScreenEvents.afterRender(screen).invoker().afterRender(screen, event.getPoseStack(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void beforeKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        Screen screen = event.getScreen();
        if (!ScreenKeyboardEvents.allowKeyPress(screen).invoker().allowKeyPress(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
            event.setCanceled(true);
        } else {
            ScreenKeyboardEvents.beforeKeyPress(screen).invoker().beforeKeyPress(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers());
        }
    }

    @SubscribeEvent
    public static void afterKeyPressed(ScreenEvent.KeyPressed.Post event) {
        Screen screen = event.getScreen();
        ScreenKeyboardEvents.afterKeyPress(screen).invoker().afterKeyPress(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    @SubscribeEvent
    public static void beforeKeyReleased(ScreenEvent.KeyReleased.Pre event) {
        Screen screen = event.getScreen();
        if (!ScreenKeyboardEvents.allowKeyRelease(screen).invoker().allowKeyRelease(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
            event.setCanceled(true);
        } else {
            ScreenKeyboardEvents.beforeKeyRelease(screen).invoker().beforeKeyRelease(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers());
        }
    }

    @SubscribeEvent
    public static void afterKeyReleased(ScreenEvent.KeyReleased.Post event) {
        Screen screen = event.getScreen();
        ScreenKeyboardEvents.afterKeyRelease(screen).invoker().afterKeyRelease(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    @SubscribeEvent
    public static void beforeMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        Screen screen = event.getScreen();
        if (!ScreenMouseEvents.allowMouseClick(screen).invoker().allowMouseClick(screen, event.getMouseX(), event.getMouseY(), event.getButton())) {
            event.setCanceled(true);
        } else {
            ScreenMouseEvents.beforeMouseClick(screen).invoker().beforeMouseClick(screen, event.getMouseX(), event.getMouseY(), event.getButton());
        }
    }

    @SubscribeEvent
    public static void afterMouseClicked(ScreenEvent.MouseButtonPressed.Post event) {
        Screen screen = event.getScreen();
        ScreenMouseEvents.afterMouseClick(screen).invoker().afterMouseClick(screen, event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @SubscribeEvent
    public static void beforeMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        Screen screen = event.getScreen();
        if (!ScreenMouseEvents.allowMouseRelease(screen).invoker().allowMouseRelease(screen, event.getMouseX(), event.getMouseY(), event.getButton())) {
            event.setCanceled(true);
        } else {
            ScreenMouseEvents.beforeMouseRelease(screen).invoker().beforeMouseRelease(screen, event.getMouseX(), event.getMouseY(), event.getButton());
        }
    }

    @SubscribeEvent
    public static void afterMouseReleased(ScreenEvent.MouseButtonReleased.Post event) {
        Screen screen = event.getScreen();
        ScreenMouseEvents.afterMouseRelease(screen).invoker().afterMouseRelease(screen, event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @SubscribeEvent
    public static void beforeMouseScroll(ScreenEvent.MouseScrolled.Pre event) {
        Screen screen = event.getScreen();
        double horizontalScroll = ((MouseExtensions) screen.getMinecraft().mouse).getHorizontalScroll();
        if (!ScreenMouseEvents.allowMouseScroll(screen).invoker().allowMouseScroll(screen, event.getMouseX(), event.getMouseY(), horizontalScroll, event.getScrollDelta())) {
            event.setCanceled(true);
        } else {
            ScreenMouseEvents.beforeMouseScroll(screen).invoker().beforeMouseScroll(screen, event.getMouseX(), event.getMouseY(), horizontalScroll, event.getScrollDelta());
        }
    }

    @SubscribeEvent
    public static void afterMouseScroll(ScreenEvent.MouseScrolled.Post event) {
        Screen screen = event.getScreen();
        double horizontalScroll = ((MouseExtensions) screen.getMinecraft().mouse).getHorizontalScroll();
        ScreenMouseEvents.afterMouseScroll(screen).invoker().afterMouseScroll(screen, event.getMouseX(), event.getMouseY(), horizontalScroll, event.getScrollDelta());
    }

    private ScreenEventHooks() {
    }
}