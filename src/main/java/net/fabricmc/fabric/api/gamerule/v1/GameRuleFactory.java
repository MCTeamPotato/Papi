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

package net.fabricmc.fabric.api.gamerule.v1;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.impl.gamerule.rule.BoundedIntRule;
import net.fabricmc.fabric.mixin.gamerule.BooleanRuleAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * A utility class containing factory methods to create game rule types.
 * A game rule is a persisted, per server data value which may control gameplay aspects.
 *
 * <p>Some factory methods allow specification of a callback that is invoked when the value of a game rule has changed.
 * Typically the callback is used for game rules which may influence game logic, such as {@link GameRules#DISABLE_RAIDS disabling raids}.
 *
 * <p>To register a game rule, you can use {@link GameRuleRegistry#register(String, GameRules.Category, GameRules.Type)}.
 * For example, to register a game rule that is an integer where the acceptable values are between 0 and 10, one would use the following:
 * <blockquote><pre>
 * public static final GameRules.Key&lt;GameRules.IntRule&gt; EXAMPLE_INT_RULE = GameRuleRegistry.register("exampleIntRule", GameRules.Category.UPDATES, GameRuleFactory.createIntRule(1, 10));
 * </pre></blockquote>
 *
 * <p>To register a game rule in a custom category, {@link GameRuleRegistry#register(String, CustomGameRuleCategory, GameRules.Type)} should be used.
 *
 * @see GameRuleRegistry
 */
public final class GameRuleFactory {
	/**
	 * Creates a boolean rule type.
	 *
	 * @param defaultValue the default value of the game rule
	 * @return a boolean rule type
	 */
	public static GameRules.Type<GameRules.BooleanRule> createBooleanRule(boolean defaultValue) {
		return createBooleanRule(defaultValue, (server, rule) -> {
		});
	}

	/**
	 * Creates a boolean rule type.
	 *
	 * @param defaultValue the default value of the game rule
	 * @param changedCallback a callback that is invoked when the value of a game rule has changed
	 * @return a boolean rule type
	 */
	public static GameRules.Type<GameRules.BooleanRule> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanRule> changedCallback) {
		return BooleanRuleAccessor.invokeCreate(defaultValue, changedCallback);
	}

	/**
	 * Creates an integer rule type.
	 *
	 * @param defaultValue the default value of the game rule
	 * @return an integer rule type
	 */
	public static GameRules.Type<GameRules.IntRule> createIntRule(int defaultValue) {
		return createIntRule(defaultValue, (server, rule) -> {
		});
	}

	/**
	 * Creates an integer rule type.
	 *
	 * @param defaultValue the default value of the game rule
	 * @param minimumValue the minimum value the game rule may accept
	 * @return an integer rule type
	 */
	public static GameRules.Type<GameRules.IntRule> createIntRule(int defaultValue, int minimumValue) {
		return createIntRule(defaultValue, minimumValue, Integer.MAX_VALUE, (server, rule) -> {
		});
	}

	/**
	 * Creates an integer rule type.
	 *
	 * @param defaultValue the default value of the game rule
	 * @param minimumValue the minimum value the game rule may accept
	 * @param changedCallback a callback that is invoked when the value of a game rule has changed
	 * @return an integer rule type
	 */
	public static GameRules.Type<GameRules.IntRule> createIntRule(int defaultValue, int minimumValue, BiConsumer<MinecraftServer, GameRules.IntRule> changedCallback) {
		return createIntRule(defaultValue, minimumValue, Integer.MAX_VALUE, changedCallback);
	}

	/**
	 * Creates an integer rule type.
	 *
	 * @param defaultValue the default value of the game rule
	 * @param minimumValue the minimum value the game rule may accept
	 * @param maximumValue the maximum value the game rule may accept
	 * @return an integer rule type
	 */
	public static GameRules.Type<GameRules.IntRule> createIntRule(int defaultValue, int minimumValue, int maximumValue) {
		return createIntRule(defaultValue, minimumValue, maximumValue, (server, rule) -> {
		});
	}

	/**
	 * Creates an integer rule type.
	 *
	 * @param defaultValue the default value of the game rule
	 * @param changedCallback a callback that is invoked when the value of a game rule has changed
	 * @return an integer rule type
	 */
	public static GameRules.Type<GameRules.IntRule> createIntRule(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntRule> changedCallback) {
		return createIntRule(defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, changedCallback);
	}

	/**
	 * Creates an integer rule type.
	 *
	 * @param defaultValue the default value of the game rule
	 * @param minimumValue the minimum value the game rule may accept
	 * @param maximumValue the maximum value the game rule may accept
	 * @param changedCallback a callback that is invoked when the value of a game rule has changed
	 * @return an integer rule type
	 */
	public static GameRules.Type<GameRules.IntRule> createIntRule(int defaultValue, int minimumValue, int maximumValue, @Nullable BiConsumer<MinecraftServer, GameRules.IntRule> changedCallback) {
		return new GameRules.Type<>(
				() -> IntegerArgumentType.integer(minimumValue, maximumValue),
				type -> new BoundedIntRule(type, defaultValue, minimumValue, maximumValue), // Internally use a bounded int rule
				changedCallback,
				GameRules.Visitor::visitInt
		);
	}


	private GameRuleFactory() {
	}
}
