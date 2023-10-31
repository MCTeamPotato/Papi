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

package net.fabricmc.fabric.impl.lookup.block;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.Papi;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiLookupMap;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BlockApiLookupImpl<A, C> implements BlockApiLookup<A, C> {
	private static final ApiLookupMap<BlockApiLookup<?, ?>> LOOKUPS = ApiLookupMap.create(BlockApiLookupImpl::new);

	@SuppressWarnings("unchecked")
	public static <A, C> BlockApiLookup<A, C> get(Identifier lookupId, Class<A> apiClass, Class<C> contextClass) {
		return (BlockApiLookup<A, C>) LOOKUPS.getLookup(lookupId, apiClass, contextClass);
	}

	private final Identifier identifier;
	private final Class<A> apiClass;
	private final Class<C> contextClass;
	private final ApiProviderMap<Block, BlockApiProvider<A, C>> providerMap = ApiProviderMap.create();
	private final List<BlockApiProvider<A, C>> fallbackProviders = new CopyOnWriteArrayList<>();

	@SuppressWarnings("unchecked")
	private BlockApiLookupImpl(Identifier identifier, Class<?> apiClass, Class<?> contextClass) {
		this.identifier = identifier;
		this.apiClass = (Class<A>) apiClass;
		this.contextClass = (Class<C>) contextClass;
	}

	@Nullable
	@Override
	public A find(World world, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity, C context) {
		Objects.requireNonNull(world, "World may not be null.");
		Objects.requireNonNull(pos, "BlockPos may not be null.");
		// Providers have the final say whether a null context is allowed.

		// Get the block state and the block entity
		if (blockEntity == null) {
			if (state == null) {
				state = world.getBlockState(pos);
			}

			if (state.hasBlockEntity()) {
				blockEntity = world.getBlockEntity(pos);
			}
		} else {
			if (state == null) {
				state = blockEntity.getCachedState();
			}
		}

		@Nullable
		BlockApiProvider<A, C> provider = getProvider(state.getBlock());
		A instance = null;

		if (provider != null) {
			instance = provider.find(world, pos, state, blockEntity, context);
		}

		if (instance != null) {
			return instance;
		}

		// Query the fallback providers
		for (BlockApiProvider<A, C> fallbackProvider : fallbackProviders) {
			instance = fallbackProvider.find(world, pos, state, blockEntity, context);

			if (instance != null) {
				return instance;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerSelf(BlockEntityType<?>... blockEntityTypes) {
		for (BlockEntityType<?> blockEntityType : blockEntityTypes) {
			Block supportBlock = ((BlockEntityTypeAccessor) blockEntityType).getBlocks().iterator().next();
			Objects.requireNonNull(supportBlock, "Could not get a support block for block entity type.");
			BlockEntity blockEntity = blockEntityType.instantiate(BlockPos.ORIGIN, supportBlock.getDefaultState());
			Objects.requireNonNull(blockEntity, "Instantiated block entity may not be null.");

			if (!apiClass.isAssignableFrom(blockEntity.getClass())) {
				String errorMessage = String.format(
						"Failed to register self-implementing block entities. API class %s is not assignable from block entity class %s.",
						apiClass.getCanonicalName(),
						blockEntity.getClass().getCanonicalName()
				);
				throw new IllegalArgumentException(errorMessage);
			}
		}

		registerForBlockEntities((blockEntity, context) -> (A) blockEntity, blockEntityTypes);
	}

	public void registerForBlocks(BlockApiProvider<A, C> provider, @NotNull Set<Block> blocks) {
		Objects.requireNonNull(provider, "BlockApiProvider may not be null.");

		if (blocks.isEmpty()) {
			throw new IllegalArgumentException("Must register at least one Block instance with a BlockApiProvider.");
		}

		for (Block block : blocks) {
			Objects.requireNonNull(block, "Encountered null block while registering a block API provider mapping.");

			if (providerMap.putIfAbsent(block, provider) != null) {
				Papi.LOGGER.warn("Encountered duplicate API provider registration for block: " + ForgeRegistries.BLOCKS.getKey(block));
			}
		}
	}

	@Override
	public void registerForBlocks(BlockApiProvider<A, C> provider, Block... blocks) {
		registerForBlocks(provider, new ObjectOpenHashSet<>(blocks));
	}

	@Override
	public void registerForBlockEntities(BlockEntityApiProvider<A, C> provider, BlockEntityType<?> @NotNull ... blockEntityTypes) {
		Objects.requireNonNull(provider, "BlockEntityApiProvider may not be null.");

		if (blockEntityTypes.length == 0) {
			throw new IllegalArgumentException("Must register at least one BlockEntityType instance with a BlockEntityApiProvider.");
		}

		BlockApiProvider<A, C> nullCheckedProvider = (world, pos, state, blockEntity, context) -> {
			if (blockEntity == null) {
				return null;
			} else {
				return provider.find(blockEntity, context);
			}
		};

		for (BlockEntityType<?> blockEntityType : blockEntityTypes) {
			Objects.requireNonNull(blockEntityType, "Encountered null block entity type while registering a block entity API provider mapping.");
			registerForBlocks(nullCheckedProvider, ((BlockEntityTypeAccessor) blockEntityType).getBlocks());
		}
	}

	@Override
	public void registerFallback(BlockApiProvider<A, C> fallbackProvider) {
		Objects.requireNonNull(fallbackProvider, "BlockApiProvider may not be null.");

		fallbackProviders.add(fallbackProvider);
	}

	@Override
	public Identifier getId() {
		return identifier;
	}

	@Override
	public Class<A> apiClass() {
		return apiClass;
	}

	@Override
	public Class<C> contextClass() {
		return contextClass;
	}

	@Override
	@Nullable
	public BlockApiProvider<A, C> getProvider(Block block) {
		return providerMap.get(block);
	}

	public List<BlockApiProvider<A, C>> getFallbackProviders() {
		return fallbackProviders;
	}
}
