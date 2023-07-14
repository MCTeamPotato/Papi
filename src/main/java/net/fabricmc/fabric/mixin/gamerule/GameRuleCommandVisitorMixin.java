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

package net.fabricmc.fabric.mixin.gamerule;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.impl.gamerule.EnumRuleCommand;
import net.fabricmc.fabric.impl.gamerule.EnumRuleType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = GameRuleCommand.class, priority = 0)
public abstract class GameRuleCommandVisitorMixin {
	/**
	 * @author Kasualix
	 * @reason impl EnumRuleCommand register
	 **/
	@Overwrite
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("gamerule").requires((source) -> source.hasPermissionLevel(2));
		GameRules.accept(new GameRules.Visitor() {
			public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
				if (type instanceof EnumRuleType) {
					//noinspection rawtypes,unchecked
					EnumRuleCommand.register(literalArgumentBuilder, (GameRules.Key) key, (EnumRuleType) type);
					return;
				}
				literalArgumentBuilder.then((CommandManager.literal(key.getName()).executes((context) -> GameRuleCommand.executeQuery((ServerCommandSource)context.getSource(), key))).then(type.argument("value").executes((context) -> GameRuleCommand.executeSet(context, key))));
			}
		});
		dispatcher.register(literalArgumentBuilder);
	}
}
