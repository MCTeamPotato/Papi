package net.fabricmc.fabric.mixin.client.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {
    /**
     * @author Kasualix, TexTrue
     * @reason impl api
     */
    @Overwrite
    static TooltipComponent of(TooltipData data) {
        TooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(data);
        if (component != null) return component;

        if (data instanceof BundleTooltipData) {
            return new BundleTooltipComponent((BundleTooltipData)data);
        }
        TooltipComponent result = MinecraftForgeClient.getClientTooltipComponent(data);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Unknown TooltipComponent");
    }
}
