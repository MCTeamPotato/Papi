package net.fabricmc.fabric.mixin.screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeHooksClient.class)
public abstract class ForgeHooksClientMixin {
    @Unique
    private static Screen renderingScreen;

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private static void onPreDraw(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        renderingScreen = screen;
        ScreenEvents.beforeRender(renderingScreen).invoker().beforeRender(renderingScreen, matrices, mouseX, mouseY, tickDelta);
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private static void onPostDraw(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        ScreenEvents.afterRender(renderingScreen).invoker().afterRender(renderingScreen, matrices, mouseX, mouseY, tickDelta);
        renderingScreen = null;
    }
}
