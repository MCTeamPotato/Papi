package net.fabricmc.fabric.mixin.papi.screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeHooksClient.class, remap = false)
public abstract class ForgeHooksClientMixin {
    @Inject(method = "onScreenKeyPressedPre", at = @At("HEAD"), cancellable = true)
    private static void beforeKeyPressedEvent(Screen guiScreen, int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> ci) {
        if (guiScreen == null) return;
        if (ScreenKeyboardEvents.allowKeyPress(guiScreen).invoker().allowKeyPress(guiScreen, keyCode, scanCode, modifiers)) {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }
        ScreenKeyboardEvents.beforeKeyPress(guiScreen).invoker().beforeKeyPress(guiScreen, keyCode, scanCode, modifiers);
    }

    @Inject(method = "onScreenKeyPressedPost", at = @At("HEAD"))
    private static void afterKeyPressedEvent(Screen guiScreen, int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> ci) {
        if (guiScreen == null) return;
        ScreenKeyboardEvents.afterKeyPress(guiScreen).invoker().afterKeyPress(guiScreen, keyCode, scanCode, modifiers);
    }

    @Inject(method = "onScreenKeyReleasedPre", at = @At("HEAD"), cancellable = true)
    private static void beforeKeyReleasedEvent(Screen guiScreen, int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> ci) {
        if (guiScreen == null) return;
        if (ScreenKeyboardEvents.allowKeyRelease(guiScreen).invoker().allowKeyRelease(guiScreen, keyCode, scanCode, modifiers)) {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }
        ScreenKeyboardEvents.beforeKeyRelease(guiScreen).invoker().beforeKeyRelease(guiScreen, keyCode, scanCode, modifiers);
    }

    @Inject(method = "onScreenKeyReleasedPost", at = @At("HEAD"))
    private static void afterKeyReleasedEvent(Screen guiScreen, int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> ci) {
        if (guiScreen == null) return;
        ScreenKeyboardEvents.afterKeyRelease(guiScreen).invoker().afterKeyRelease(guiScreen, keyCode, scanCode, modifiers);
    }

    @Inject(method = "onScreenMouseClickedPre", at = @At("HEAD"), cancellable = true)
    private static void beforeMouseClickedEvent(Screen guiScreen, double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
        if (guiScreen == null) return;
        if (!ScreenMouseEvents.allowMouseClick(guiScreen).invoker().allowMouseClick(guiScreen, mouseX, mouseY, button)) {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }
        ScreenMouseEvents.beforeMouseClick(guiScreen).invoker().beforeMouseClick(guiScreen, mouseX, mouseY, button);
    }

    @Inject(method = "onScreenMouseClickedPost", at = @At("HEAD"))
    private static void afterMouseClickedEvent(Screen guiScreen, double mouseX, double mouseY, int button, boolean handled, CallbackInfoReturnable<Boolean> cir) {
        if (guiScreen == null) return;
        ScreenMouseEvents.afterMouseClick(guiScreen).invoker().afterMouseClick(guiScreen, mouseX, mouseY, button);
    }

    @Inject(method = "onScreenMouseReleasedPre", at = @At("HEAD"), cancellable = true)
    private static void beforeMouseReleasedEvent(Screen guiScreen, double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
        if (guiScreen == null) return;
        if (!ScreenMouseEvents.allowMouseRelease(guiScreen).invoker().allowMouseRelease(guiScreen, mouseX, mouseY, button)) {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }

        ScreenMouseEvents.beforeMouseRelease(guiScreen).invoker().beforeMouseRelease(guiScreen, mouseX, mouseY, button);
    }

    @Inject(method = "onScreenMouseReleasedPost", at = @At("HEAD"))
    private static void afterMouseReleasedEvent(Screen guiScreen, double mouseX, double mouseY, int button, boolean handled, CallbackInfoReturnable<Boolean> cir) {
        if (guiScreen == null) return;
        ScreenMouseEvents.afterMouseRelease(guiScreen).invoker().afterMouseRelease(guiScreen, mouseX, mouseY, button);
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private static void onBeforeRenderScreen(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        if (screen == null) return;
        ScreenEvents.beforeRender(screen).invoker().beforeRender(screen, matrices, mouseX, mouseY, tickDelta);
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private static void onAfterRenderScreen(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        if (screen == null) return;
        ScreenEvents.afterRender(screen).invoker().afterRender(screen, matrices, mouseX, mouseY, tickDelta);
    }
}
