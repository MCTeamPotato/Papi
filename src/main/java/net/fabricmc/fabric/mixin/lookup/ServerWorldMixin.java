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

package net.fabricmc.fabric.mixin.lookup;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.impl.lookup.block.BlockApiCacheImpl;
import net.fabricmc.fabric.impl.lookup.block.ServerWorldCache;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ServerWorldCache {
	@Unique
	private final Map<BlockPos, List<WeakReference<BlockApiCacheImpl<?, ?>>>> papi$apiLookupCaches = new Object2ReferenceOpenHashMap<>();
	/**
	 * Ensures that the apiLookupCaches map is iterated over every once in a while to clean up caches.
	 */
	@Unique
	private int papi$apiLookupAccessesWithoutCleanup = 0;

	@Override
	public void fabric_registerCache(@NotNull BlockPos pos, BlockApiCacheImpl<?, ?> cache) {
		List<WeakReference<BlockApiCacheImpl<?, ?>>> caches = papi$apiLookupCaches.computeIfAbsent(pos.toImmutable(), ignored -> new ArrayList<>());
		caches.removeIf(weakReference -> weakReference.get() == null);
		caches.add(new WeakReference<>(cache));
		papi$apiLookupAccessesWithoutCleanup++;
	}

	@Override
	public void fabric_invalidateCache(BlockPos pos) {
		List<WeakReference<BlockApiCacheImpl<?, ?>>> caches = papi$apiLookupCaches.get(pos);

		if (caches != null) {
			caches.removeIf(weakReference -> weakReference.get() == null);

			if (caches.isEmpty()) {
				papi$apiLookupCaches.remove(pos);
			} else {
				caches.forEach(weakReference -> {
					BlockApiCacheImpl<?, ?> cache = weakReference.get();

					if (cache != null) {
						cache.invalidate();
					}
				});
			}
		}

		papi$apiLookupAccessesWithoutCleanup++;

		// Try to invalidate GC'd lookups from the cache after 2 * the number of cached lookups
		if (papi$apiLookupAccessesWithoutCleanup > 2 * papi$apiLookupCaches.size()) {
			papi$apiLookupCaches.entrySet().removeIf(entry -> {
				entry.getValue().removeIf(weakReference -> weakReference.get() == null);
				return entry.getValue().isEmpty();
			});

			papi$apiLookupAccessesWithoutCleanup = 0;
		}
	}
}
