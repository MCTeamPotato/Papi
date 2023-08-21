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

package net.fabricmc.fabric.impl.client.model;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.client.model.*;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModelLoadingRegistryImpl implements ModelLoadingRegistry {
	private static final boolean DEBUG_MODEL_LOADING = !FMLLoader.isProduction()
			|| Boolean.parseBoolean(System.getProperty("fabric.debugModelLoading", "false"));

	@FunctionalInterface
	private interface CustomModelItf<T> {
		UnbakedModel load(T obj) throws ModelProviderException;
	}

	public static class LoaderInstance implements ModelProviderContext {
		private final Logger logger;
		private final ResourceManager manager;
		private final List<ModelVariantProvider> modelVariantProviders;
		private final List<ModelResourceProvider> modelResourceProviders;
		private final List<ExtraModelProvider> modelAppenders;
		private ModelLoader loader;

		private LoaderInstance(ModelLoadingRegistryImpl i, ModelLoader loader, ResourceManager manager) {
			this.logger = ModelLoadingRegistryImpl.LOGGER;
			this.loader = loader;
			this.manager = manager;
			this.modelVariantProviders = i.variantProviderSuppliers.stream().map((s) -> s.apply(manager)).collect(Collectors.toList());
			this.modelResourceProviders = i.resourceProviderSuppliers.stream().map((s) -> s.apply(manager)).collect(Collectors.toList());
			this.modelAppenders = i.appenders;
		}

		@Override
		public UnbakedModel loadModel(Identifier id) {
			if (loader == null) {
				throw new RuntimeException("Called loadModel too late!");
			}

			return ((ModelLoaderHooks) loader).fabric_loadModel(id);
		}

		public void onModelPopulation(Consumer<Identifier> addModel) {
			for (ExtraModelProvider appender : modelAppenders) {
				appender.provideExtraModels(manager, addModel);
			}
		}

		private <T> UnbakedModel loadCustomModel(CustomModelItf<T> function, Collection<T> loaders, String debugName) throws ModelProviderException {
			if (!DEBUG_MODEL_LOADING) {
				for (T provider : loaders) {
                    UnbakedModel model = function.load(provider);

                    if (model != null) {
                        return model;
                    }
                }

				return null;
			}

			UnbakedModel modelLoaded = null;
			T providerUsed = null;
			List<T> providersApplied = null;

			for (T provider : loaders) {
                UnbakedModel model = function.load(provider);

                if (model != null) {
                    if (providersApplied != null) {
                        providersApplied.add(provider);
                    } else if (providerUsed != null) {
                        providersApplied = Lists.newArrayList(providerUsed, provider);
                    } else {
                        modelLoaded = model;
                        providerUsed = provider;
                    }
                }
            }

			if (providersApplied != null) {
				StringBuilder builder = new StringBuilder("Conflict - multiple " + debugName + "s claimed the same unbaked model:");

				for (T loader : providersApplied) {
					builder.append("\n\t - ").append(loader.getClass().getName());
				}

				logger.error(builder.toString());
				return null;
			} else {
				return modelLoaded;
			}
		}

		@Nullable
		public UnbakedModel loadModelFromResource(Identifier resourceId) throws ModelProviderException {
			return loadCustomModel((r) -> r.loadModelResource(resourceId, this), modelResourceProviders, "resource provider");
		}

		@Nullable
		public UnbakedModel loadModelFromVariant(Identifier variantId) throws ModelProviderException {
			if (!(variantId instanceof ModelIdentifier modelId)) {
				return loadModelFromResource(variantId);
			} else {
				UnbakedModel model = loadCustomModel((r) -> r.loadModelVariant((ModelIdentifier) variantId, this), modelVariantProviders, "resource provider");

				if (model != null) {
					return model;
				}

				// Replicating the special-case from ModelLoader as loadModelFromJson is insufficiently patchable
				if (Objects.equals(modelId.getVariant(), "inventory")) {
					Identifier resourceId = new Identifier(modelId.getNamespace(), "item/" + modelId.getPath());
					model = loadModelFromResource(resourceId);

                    return model;
				}

				return null;
			}
		}

		public void finish() {
			loader = null;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelLoadingRegistryImpl.class);

	private final List<Function<ResourceManager, ModelVariantProvider>> variantProviderSuppliers = new ArrayList<>();
	private final List<Function<ResourceManager, ModelResourceProvider>> resourceProviderSuppliers = new ArrayList<>();
	private final List<ExtraModelProvider> appenders = new ArrayList<>();

	@Override
	public void registerModelProvider(ExtraModelProvider appender) {
		appenders.add(appender);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void registerAppender(ModelAppender appender) {
		registerModelProvider((manager, consumer) -> appender.appendAll(manager, consumer::accept));
	}

	@Override
	public void registerResourceProvider(Function<ResourceManager, ModelResourceProvider> providerSupplier) {
		resourceProviderSuppliers.add(providerSupplier);
	}

	@Override
	public void registerVariantProvider(Function<ResourceManager, ModelVariantProvider> providerSupplier) {
		variantProviderSuppliers.add(providerSupplier);
	}

	public static LoaderInstance begin(ModelLoader loader, ResourceManager manager) {
		return new LoaderInstance((ModelLoadingRegistryImpl) INSTANCE, loader, manager);
	}
}