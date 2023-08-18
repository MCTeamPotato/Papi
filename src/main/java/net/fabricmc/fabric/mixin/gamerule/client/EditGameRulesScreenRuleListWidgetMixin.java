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

package net.fabricmc.fabric.mixin.gamerule.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// For any future maintainers who wonder why this class does not compile because of jsr305, please reload gradle using `--refresh-dependencies`.
@Mixin(EditGameRulesScreen.RuleListWidget.class)
public abstract class EditGameRulesScreenRuleListWidgetMixin extends EntryListWidget<EditGameRulesScreen.AbstractRuleWidget> {
	@Unique
	private final Map<CustomGameRuleCategory, List<EditGameRulesScreen.AbstractRuleWidget>> fabric_Categories = new Object2ObjectOpenHashMap<>();

	public EditGameRulesScreenRuleListWidgetMixin(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		super(client, width, height, top, bottom, itemHeight);
	}

	// EditGameRulesScreen is effectively a synthetic parameter
	@Dynamic
	@Inject(method = "<init>(Lnet/minecraft/client/gui/screen/world/EditGameRulesScreen;Lnet/minecraft/world/GameRules;)V", at = @At("TAIL"))
	private void initializeFabricGameruleCategories(EditGameRulesScreen screen, GameRules gameRules, CallbackInfo ci) {
		this.fabric_Categories.forEach((category, widgetList) -> {
			this.addEntry(screen.new RuleCategoryWidget(category.name()));

			for (EditGameRulesScreen.AbstractRuleWidget widget : widgetList) {
				this.addEntry(widget);
			}
		});
	}

	// Synthetic method
	@Dynamic
	@Inject(method = "method_27638(Ljava/util/Map$Entry;)V", at = @At("HEAD"), cancellable = true)
	private void ignoreKeysWithCustomCategories(Map.Entry<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget> entry, CallbackInfo ci) {
		final GameRules.Key<?> ruleKey = entry.getKey();
		CustomGameRuleCategory.getCategory(ruleKey).ifPresent(key -> {
			this.fabric_Categories.computeIfAbsent(key, c -> new ArrayList<>()).add(entry.getValue());
			ci.cancel();
		});
	}
}
