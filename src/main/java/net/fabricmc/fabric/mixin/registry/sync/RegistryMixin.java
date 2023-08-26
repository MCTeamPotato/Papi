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

package net.fabricmc.fabric.mixin.registry.sync;

import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.impl.registry.sync.FabricRegistry;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.EnumSet;
import java.util.Set;

@Mixin(Registry.class)
public abstract class RegistryMixin<T> implements RegistryAttributeHolder, FabricRegistry {
    @Unique
    private final EnumSet<RegistryAttribute> papi$attributes = EnumSet.noneOf(RegistryAttribute.class);

    @Override
    public RegistryAttributeHolder addAttribute(RegistryAttribute attribute) {
        papi$attributes.add(attribute);
        return this;
    }

    @Override
    public boolean hasAttribute(RegistryAttribute attribute) {
        return papi$attributes.contains(attribute);
    }

    @Override
    public void build(Set<RegistryAttribute> attributes) {
        this.papi$attributes.addAll(attributes);
    }
}