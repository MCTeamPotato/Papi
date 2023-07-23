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

package net.fabricmc.fabric.mixin.client.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringVisitable;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Optional;

@SuppressWarnings({"UnstableApiUsage", "OptionalUsedAsFieldOrParameterType"})
@Mixin(Screen.class)
public class ScreenMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;gatherTooltipComponents(Lnet/minecraft/item/ItemStack;Ljava/util/List;Ljava/util/Optional;IIILnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V")
	private static List<TooltipComponent> injectRenderTooltipLambda(ItemStack stack, List<? extends StringVisitable> textElements, Optional<TooltipData> itemComponent, int mouseX, int screenWidth, int screenHeight, @Nullable TextRenderer forcedFont, TextRenderer fallbackFont) {
		List<TooltipComponent> components = ForgeHooksClient.gatherTooltipComponents(stack, textElements, itemComponent, mouseX, screenWidth, screenHeight, forcedFont, fallbackFont);
		TooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(itemComponent.orElse(null));

		if (component != null) {
			components.add(1, component);
		}
		return components;
	}
}
