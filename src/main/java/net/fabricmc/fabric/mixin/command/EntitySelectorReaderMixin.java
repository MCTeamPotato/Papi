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

package net.fabricmc.fabric.mixin.command;

import net.fabricmc.fabric.api.command.v2.FabricEntitySelectorReader;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

@Mixin(EntitySelectorReader.class)
public class EntitySelectorReaderMixin implements FabricEntitySelectorReader {
	@Unique
	private final Set<Identifier> papi$flags = new HashSet<>();

	@Override
	public void papi$setCustomFlag(Identifier key, boolean value) {
		if (value) {
			this.papi$flags.add(key);
		} else {
			this.papi$flags.remove(key);
		}
	}

	@Override
	public boolean papi$getCustomFlag(Identifier key) {
		return this.papi$flags.contains(key);
	}
}