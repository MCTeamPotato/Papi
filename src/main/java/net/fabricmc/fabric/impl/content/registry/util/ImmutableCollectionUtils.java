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

package net.fabricmc.fabric.impl.content.registry.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ImmutableCollectionUtils {
	public static <T> Set<T> getAsMutableSet(@NotNull Supplier<Set<T>> getter, Consumer<Set<T>> setter) {
		Set<T> set = getter.get();

		if (!(set instanceof HashSet)) {
			setter.accept(set = new HashSet<>(set));
		}

		return set;
	}

	public static <T> List<T> getAsMutableList(@NotNull Supplier<List<T>> getter, Consumer<List<T>> setter) {
		List<T> set = getter.get();

		if (!(set instanceof ArrayList)) {
			setter.accept(set = new ArrayList<>(set));
		}

		return set;
	}

	public static <K, V> Map<K, V> getAsMutableMap(@NotNull Supplier<Map<K, V>> getter, Consumer<Map<K, V>> setter) {
		Map<K, V> map = getter.get();

		if (!(map instanceof HashMap)) {
			setter.accept(map = new HashMap<>(map));
		}

		return map;
	}
}
