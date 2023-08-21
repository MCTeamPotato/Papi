package net.fabricmc.papi.impl.screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ScreenEventsImpl {
    @SubscribeEvent
    public static void beforeScreenDraw(ScreenEvent.DrawScreenEvent.Pre event) {
        Screen screen = event.getScreen();
        ScreenEvents.beforeRender(screen).invoker().beforeRender(screen, event.getPoseStack(), event.getMouseX(), event.getMouseY(), event.getPartialTicks());
    }

    @SubscribeEvent
    public static void afterScreenDraw(ScreenEvent.DrawScreenEvent.Post event) {
        Screen screen = event.getScreen();
        ScreenEvents.afterRender(screen).invoker().afterRender(screen, event.getPoseStack(), event.getMouseX(), event.getMouseY(), event.getPartialTicks());
    }

    @SubscribeEvent
    public static void beforeKeyPressed(ScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        Screen screen = event.getScreen();
        if (!ScreenKeyboardEvents.allowKeyPress(screen).invoker().allowKeyPress(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
            event.setCanceled(true);
        } else {
            ScreenKeyboardEvents.beforeKeyPress(screen).invoker().beforeKeyPress(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers());
        }
    }

    @SubscribeEvent
    public static void afterKeyPressed(ScreenEvent.KeyboardKeyPressedEvent.Post event) {
        Screen screen = event.getScreen();
        ScreenKeyboardEvents.afterKeyPress(screen).invoker().afterKeyPress(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    @SubscribeEvent
    public static void beforeKeyReleased(ScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
        Screen screen = event.getScreen();
        if (!ScreenKeyboardEvents.allowKeyRelease(screen).invoker().allowKeyRelease(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
            event.setCanceled(true);
        } else {
            ScreenKeyboardEvents.beforeKeyRelease(screen).invoker().beforeKeyRelease(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers());
        }
    }

    @SubscribeEvent
    public static void afterKeyReleased(ScreenEvent.KeyboardKeyReleasedEvent.Post event) {
        Screen screen = event.getScreen();
        ScreenKeyboardEvents.afterKeyRelease(screen).invoker().afterKeyRelease(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    @SubscribeEvent
    public static void beforeMouseClicked(ScreenEvent.MouseClickedEvent.Pre event) {
        Screen screen = event.getScreen();
        if (!ScreenMouseEvents.allowMouseClick(screen).invoker().allowMouseClick(screen, event.getMouseX(), event.getMouseY(), event.getButton())) {
            event.setCanceled(true);
        } else {
            ScreenMouseEvents.beforeMouseClick(screen).invoker().beforeMouseClick(screen, event.getMouseX(), event.getMouseY(), event.getButton());
        }
    }

    @SubscribeEvent
    public static void afterMouseClicked(ScreenEvent.MouseClickedEvent.Post event) {
        Screen screen = event.getScreen();
        ScreenMouseEvents.afterMouseClick(screen).invoker().afterMouseClick(screen, event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @SubscribeEvent
    public static void beforeMouseReleased(ScreenEvent.MouseReleasedEvent.Pre event) {
        Screen screen = event.getScreen();
        if (!ScreenMouseEvents.allowMouseRelease(screen).invoker().allowMouseRelease(screen, event.getMouseX(), event.getMouseY(), event.getButton())) {
            event.setCanceled(true);
        } else {
            ScreenMouseEvents.beforeMouseRelease(screen).invoker().beforeMouseRelease(screen, event.getMouseX(), event.getMouseY(), event.getButton());
        }
    }

    @SubscribeEvent
    public static void afterMouseReleased(ScreenEvent.MouseReleasedEvent.Post event) {
        Screen screen = event.getScreen();
        ScreenMouseEvents.afterMouseRelease(screen).invoker().afterMouseRelease(screen, event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @SubscribeEvent
    public static void beforeMouseScroll(ScreenEvent.MouseScrollEvent.Pre event) {
        Screen screen = event.getScreen();
        double horizontalScroll = ((MouseExtensions) screen.getMinecraft().mouse).getHorizontalScroll();
        if (!ScreenMouseEvents.allowMouseScroll(screen).invoker().allowMouseScroll(screen, event.getMouseX(), event.getMouseY(), horizontalScroll, event.getScrollDelta())) {
            event.setCanceled(true);
        } else {
            ScreenMouseEvents.beforeMouseScroll(screen).invoker().beforeMouseScroll(screen, event.getMouseX(), event.getMouseY(), horizontalScroll, event.getScrollDelta());
        }
    }

    @SubscribeEvent
    public static void afterMouseScroll(ScreenEvent.MouseScrollEvent.Post event) {
        Screen screen = event.getScreen();
        double horizontalScroll = ((MouseExtensions) screen.getMinecraft().mouse).getHorizontalScroll();
        ScreenMouseEvents.afterMouseScroll(screen).invoker().afterMouseScroll(screen, event.getMouseX(), event.getMouseY(), horizontalScroll, event.getScrollDelta());
    }

    private ScreenEventsImpl() {
    }
}
