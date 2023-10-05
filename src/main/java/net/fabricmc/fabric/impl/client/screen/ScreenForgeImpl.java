package net.fabricmc.fabric.impl.client.screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public final class ScreenForgeImpl {
    @SubscribeEvent
    public static void beforeKeyPressedEvent(GuiScreenEvent.KeyboardKeyPressedEvent.@NotNull Pre event) {
        Screen screen = event.getGui();
        int keyCode = event.getKeyCode();
        int scanCode = event.getScanCode();
        int modifiers = event.getModifiers();
        if (!ScreenKeyboardEvents.allowKeyPress(screen).invoker().allowKeyPress(screen, keyCode, scanCode, modifiers)) {
            event.setCanceled(true);
        } else {
            ScreenKeyboardEvents.beforeKeyPress(screen).invoker().beforeKeyPress(screen, keyCode, scanCode, modifiers);
        }
    }

    @SubscribeEvent
    public static void afterKeyPressedEvent(GuiScreenEvent.KeyboardKeyPressedEvent.@NotNull Post event) {
        Screen screen = event.getGui();
        ScreenKeyboardEvents.afterKeyPress(screen).invoker().afterKeyPress(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    @SubscribeEvent
    public static void beforeKeyReleased(GuiScreenEvent.KeyboardKeyReleasedEvent.@NotNull Pre event) {
        Screen screen = event.getGui();
        int keyCode = event.getKeyCode();
        int scanCode = event.getScanCode();
        int modifiers = event.getModifiers();
        if (!ScreenKeyboardEvents.allowKeyRelease(screen).invoker().allowKeyRelease(screen, keyCode, scanCode, modifiers)) {
            event.setCanceled(true);
        } else {
            ScreenKeyboardEvents.beforeKeyRelease(screen).invoker().beforeKeyRelease(screen, keyCode, scanCode, modifiers);
        }
    }

    @SubscribeEvent
    public static void afterKeyReleased(GuiScreenEvent.KeyboardKeyReleasedEvent.@NotNull Post event) {
        Screen screen = event.getGui();
        ScreenKeyboardEvents.afterKeyRelease(screen).invoker().afterKeyRelease(screen, event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    @SubscribeEvent
    public static void beforeMouseClickedEvent(GuiScreenEvent.MouseClickedEvent.@NotNull Pre event) {
        Screen screen = event.getGui();
        double mouseX = event.getMouseX();
        double mouseY = event.getMouseX();
        int button = event.getButton();
        if (!ScreenMouseEvents.allowMouseClick(screen).invoker().allowMouseClick(screen, mouseX, mouseY, button)) {
            event.setCanceled(true);
        } else {
            ScreenMouseEvents.beforeMouseClick(screen).invoker().beforeMouseClick(screen, mouseX, mouseY, button);
        }
    }

    @SubscribeEvent
    public static void afterMouseClickedEvent(GuiScreenEvent.MouseClickedEvent.@NotNull Post event) {
        Screen screen = event.getGui();
        ScreenMouseEvents.afterMouseClick(screen).invoker().afterMouseClick(screen, event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @SubscribeEvent
    public static void beforeMouseReleased(GuiScreenEvent.MouseReleasedEvent.@NotNull Pre event) {
        Screen screen = event.getGui();
        double mouseX = event.getMouseX();
        double mouseY = event.getMouseX();
        int button = event.getButton();
        if (!ScreenMouseEvents.allowMouseRelease(screen).invoker().allowMouseRelease(screen, mouseX, mouseY, button)) {
            event.setCanceled(true);
        } else {
            ScreenMouseEvents.beforeMouseRelease(screen).invoker().beforeMouseRelease(screen, mouseX, mouseY, button);
        }
    }

    @SubscribeEvent
    public static void afterMouseReleased(GuiScreenEvent.MouseReleasedEvent.@NotNull Post event) {
        Screen screen = event.getGui();
        ScreenMouseEvents.afterMouseRelease(screen).invoker().afterMouseRelease(screen, event.getMouseX(), event.getMouseY(), event.getButton());
    }


    @SubscribeEvent
    public static void beforeMouseScroll(GuiScreenEvent.MouseScrollEvent.@NotNull Pre event) {
        Screen screen = event.getGui();
        double mouseX = event.getMouseX();
        double mouseY = event.getMouseX();
        double scrollDelta = event.getScrollDelta();
        double horizontalScroll = ((MouseExtensions) screen.getMinecraft().mouse).getHorizontalScroll();
        if (!ScreenMouseEvents.allowMouseScroll(screen).invoker().allowMouseScroll(screen, mouseX, mouseY, horizontalScroll, scrollDelta)) {
            event.setCanceled(true);
        } else {
            ScreenMouseEvents.beforeMouseScroll(screen).invoker().beforeMouseScroll(screen, mouseX, mouseY, horizontalScroll, scrollDelta);
        }
    }

    @SubscribeEvent
    public static void afterMouseScroll(GuiScreenEvent.MouseScrollEvent.@NotNull Post event) {
        Screen screen = event.getGui();
        ScreenMouseEvents.afterMouseScroll(screen).invoker().afterMouseScroll(screen, event.getMouseX(), event.getMouseY(), ((MouseExtensions) screen.getMinecraft().mouse).getHorizontalScroll(), event.getScrollDelta());
    }
}