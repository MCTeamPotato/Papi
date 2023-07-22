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

package net.fabricmc.fabric.impl.resource.loader;

import com.google.common.base.Charsets;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Internal utilities for managing resource packs.
 */
public final class ModResourcePackUtil {
	public static final int PACK_FORMAT_VERSION = SharedConstants.getGameVersion().getPackVersion();

	private ModResourcePackUtil() {
	}

	/**
	 * Appends mod resource packs to the given list.
	 *
	 * @param packs   the resource pack list to append
	 * @param type    the type of resource
	 * @param subPath the resource pack sub path directory in mods, may be {@code null}
	 */
	public static void appendModResourcePacks(List<ModResourcePack> packs, ResourceType type, @Nullable String subPath) {
		for (ModInfo container : FMLLoader.getLoadingModList().getMods()) {
			Path path = container.getOwningFile().getFile().getFilePath();

			if (subPath != null) {
				Path childPath = path.resolve(subPath.replace("/", path.getFileSystem().getSeparator())).toAbsolutePath().normalize();

				if (!childPath.startsWith(path) || !Files.exists(childPath)) {
					continue;
				}

				path = childPath;
			}

			ModResourcePack pack = new ModNioResourcePack(container, path, null, ResourcePackActivationType.ALWAYS_ENABLED);

			if (!pack.getNamespaces(type).isEmpty()) {
				packs.add(pack);
			}
		}
	}

	public static boolean containsDefault(ModInfo info, String filename) {
		return "pack.mcmeta".equals(filename);
	}

	public static InputStream openDefault(ModInfo info, String filename) {
		if (filename.equals("pack.mcmeta")) {
			String description = info.getDisplayName();

			if (description == null) {
				description = "";
			} else {
				description = description.replaceAll("\"", "\\\"");
			}

			String pack = String.format("{\"pack\":{\"pack_format\":" + PACK_FORMAT_VERSION + ",\"description\":\"%s\"}}", description);
			return IOUtils.toInputStream(pack, Charsets.UTF_8);
		}
		return null;
	}

	public static String getName(ModInfo info) {
		if (info.getDisplayName() != null) {
			return info.getDisplayName();
		} else {
			return "Fabric Mod \"" + info.getModId() + "\"";
		}
	}
}
