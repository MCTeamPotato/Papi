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

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.niofs.union.UnionFileSystemProvider;
import net.fabricmc.papi.util.LoaderUtil;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.resource.PathResourcePack;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;

public class ModNioResourcePack extends PathResourcePack implements ModResourcePack {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModNioResourcePack.class);
	private static final FileSystem DEFAULT_FS = FileSystems.getDefault();
	private static final UnionFileSystemProvider UFSP = (UnionFileSystemProvider) FileSystemProvider.installedProviders().stream().filter(fsp->fsp.getScheme().equals("union")).findFirst().orElseThrow(()->new IllegalStateException("Couldn't find UnionFileSystemProvider"));

	private final IModInfo modInfo;
	private final ResourcePackActivationType activationType;

	public static ModNioResourcePack create(String name, IModInfo mod, String subPath, ResourceType type, ResourcePackActivationType activationType) {
		List<Path> rootPaths = LoaderUtil.getModContainerPaths(mod);
		List<Path> paths;

		if (subPath == null) {
			paths = rootPaths;
		} else {
			paths = new ArrayList<>(rootPaths.size());

			for (Path path : rootPaths) {
				path = path.toAbsolutePath().normalize();
				Path childPath = path.resolve(subPath.replace("/", path.getFileSystem().getSeparator())).normalize();

				if (!childPath.startsWith(path) || !exists(childPath)) {
					continue;
				}

				paths.add(childPath);
			}
		}

		if (paths.isEmpty()) return null;

		Path union = paths.size() == 1 ? paths.get(0) : UFSP.newFileSystem((a, b) -> true, paths.toArray(Path[]::new)).getRoot();
		ModNioResourcePack ret = new ModNioResourcePack(name, LoaderUtil.getModContainerMetadata(mod), union, type, null, activationType);

		return ret.getNamespaces(type).isEmpty() ? null : ret;
	}

	private ModNioResourcePack(String name, IModInfo modInfo, Path path, ResourceType type, AutoCloseable closer, ResourcePackActivationType activationType) {
		super(name, path);

		this.modInfo = modInfo;
		this.activationType = activationType;
	}

	@Override
	public IModInfo getFabricModMetadata() {
		return modInfo;
	}

	public ResourcePackActivationType getActivationType() {
		return this.activationType;
	}

	private static boolean exists(Path path) {
		// NIO Files.exists is notoriously slow when checking the file system
		return path.getFileSystem() == DEFAULT_FS ? path.toFile().exists() : Files.exists(path);
	}
}
