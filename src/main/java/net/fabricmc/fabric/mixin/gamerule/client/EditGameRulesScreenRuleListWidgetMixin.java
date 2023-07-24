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

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

// For any future maintainers who wonder why this class does not compile because of jsr305, please reload gradle using `--refresh-dependencies`.
@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(EditGameRulesScreen.RuleListWidget.class)
public abstract class EditGameRulesScreenRuleListWidgetMixin extends net.minecraft.client.gui.widget.EntryListWidget<EditGameRulesScreen.AbstractRuleWidget> {
	@Unique
	private final Map<CustomGameRuleCategory, List<EditGameRulesScreen.AbstractRuleWidget>> fabricCategories = new HashMap<>();

	public EditGameRulesScreenRuleListWidgetMixin(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		super(client, width, height, top, bottom, itemHeight);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initializeFabricGameruleCategories(EditGameRulesScreen screen, GameRules gameRules, CallbackInfo ci) {
		this.fabricCategories.forEach((category, widgetList) -> {
			this.addEntry(screen.new RuleCategoryWidget(category.getName()));

			for (EditGameRulesScreen.AbstractRuleWidget widget : widgetList) {
				this.addEntry(widget);
			}
		});
	}

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
	private void onInit(Stream<Map.Entry<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget>> stream, Consumer<? super Map.Entry<GameRules.Key<?>, EditGameRulesScreen.AbstractRuleWidget>> consumer) {
		stream.forEach(keyAbstractRuleWidgetEntry -> {
			final GameRules.Key<?> ruleKey = keyAbstractRuleWidgetEntry.getKey();
			AtomicInteger i = new AtomicInteger();
			CustomGameRuleCategory.getCategory(ruleKey).ifPresent(key -> {
				this.fabricCategories.computeIfAbsent(key, c -> new ArrayList<>()).add(keyAbstractRuleWidgetEntry.getValue());
				i.set(1);
			});
			if (i.get() == 0) {
				consumer.accept(keyAbstractRuleWidgetEntry);
			}
		});
	}
}