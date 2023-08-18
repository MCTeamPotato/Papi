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
package net.fabricmc.fabric.mixin.resource.loader;

import net.fabricmc.fabric.impl.resource.loader.FabricResource;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePackSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

/**
 * Implements {@link FabricResource} (resource source getter/setter)
 * for vanilla's basic {@link Resource} used for most game resources.
 *
 * see NamespaceResourceManagerMixin the usage site for this mixin
 */
@Mixin(Resource.class)
public class ResourceMixin implements FabricResource {
    @Unique
    private @Nullable ResourcePackSource fabric_packSource;

    @Override
    public ResourcePackSource getFabricPackSource() {
        return Objects.requireNonNullElse(fabric_packSource, ResourcePackSource.PACK_SOURCE_NONE);
    }

    @Override
    public void setFabricPackSource(ResourcePackSource packSource) {
        this.fabric_packSource = packSource;
    }
}