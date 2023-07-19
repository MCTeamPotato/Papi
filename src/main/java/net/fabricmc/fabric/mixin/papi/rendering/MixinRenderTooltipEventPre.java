package net.fabricmc.fabric.mixin.papi.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = RenderTooltipEvent.Pre.class, remap = false)
public class MixinRenderTooltipEventPre extends RenderTooltipEvent{
    protected MixinRenderTooltipEventPre(@NotNull ItemStack itemStack, MatrixStack poseStack, int x, int y, @NotNull TextRenderer font, @NotNull List<TooltipComponent> components) {
        super(itemStack, poseStack, x, y, font, components);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ItemStack stack, MatrixStack poseStack, int x, int y, int screenWidth, int screenHeight, TextRenderer font, List<TooltipComponent> components, CallbackInfo ci) {
        TooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(stack.getTooltipData().orElse(null));

        if (component != null) {
            components.add(1, component);
            this.setCanceled(true);
        }
    }
}
