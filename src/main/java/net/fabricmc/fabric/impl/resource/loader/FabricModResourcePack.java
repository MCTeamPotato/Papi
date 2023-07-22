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
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraftforge.fml.ModList;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The Fabric mods resource pack, holds all the mod resource packs as one pack.
 */
public class FabricModResourcePack extends GroupResourcePack {
	public FabricModResourcePack(ResourceType type, List<ModResourcePack> packs) {
		super(type, packs);
	}

	@Override
	public InputStream openRoot(String fileName) throws IOException {
		if ("pack.mcmeta".equals(fileName)) {
			String description = "Mod resources.";
			String pack = String.format("{\"pack\":{\"pack_format\":" + ModResourcePackUtil.PACK_FORMAT_VERSION + ",\"description\":\"%s\"}}", description);
			return IOUtils.toInputStream(pack, Charsets.UTF_8);
		} else if ("pack.png".equals(fileName)) {
			Path iconPath = ModList.get().getModFileById("papi").getFile().getFilePath().resolve(fileName);

			if (Files.exists(iconPath)) {
				// Create an InputStream from the icon image file and return it
				return Files.newInputStream(iconPath);
			}
		}

		// ReloadableResourceManagerImpl gets away with FileNotFoundException.
		throw new FileNotFoundException("\"" + fileName + "\" in Fabric mod resource pack");
	}

	@Override
	public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		try {
			InputStream inputStream = this.openRoot("pack.mcmeta");
			Throwable error = null;
			T metadata;

			try {
				metadata = AbstractFileResourcePack.parseMetadata(metaReader, inputStream);
			} catch (Throwable e) {
				error = e;
				throw e;
			} finally {
				if (inputStream != null) {
					if (error != null) {
						try {
							inputStream.close();
						} catch (Throwable e) {
							error.addSuppressed(e);
						}
					} else {
						inputStream.close();
					}
				}
			}

			return metadata;
		} catch (FileNotFoundException | RuntimeException e) {
			return null;
		}
	}

	@Override
	public String getName() {
		return "Fabric Mods";
	}
}
