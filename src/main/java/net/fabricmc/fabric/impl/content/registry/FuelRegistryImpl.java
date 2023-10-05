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

package net.fabricmc.fabric.impl.content.registry;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

public final class FuelRegistryImpl implements FuelRegistry {
	public static final FuelRegistryImpl INSTANCE = new FuelRegistryImpl();
	private static final Logger LOGGER = LogManager.getLogger();
	private final Object2IntMap<ItemConvertible> itemCookTimes = new Object2IntLinkedOpenHashMap<>();
	private final Object2IntMap<Tag<Item>> tagCookTimes = new Object2IntLinkedOpenHashMap<>();
	private volatile Map<Item, Integer> fuelTimeCache = null; // thread safe via copy-on-write mechanism

	public FuelRegistryImpl() {
		MinecraftForge.EVENT_BUS.addListener(this::onFurnaceFueldBurnTime);
	}

	public Map<Item, Integer> getFuelTimes() {
		Map<Item, Integer> ret = fuelTimeCache;

		if (ret == null) {
			fuelTimeCache = ret = new IdentityHashMap<>(AbstractFurnaceBlockEntity.createFuelTimeMap()); // IdentityHashMap is faster than vanilla's LinkedHashMap and suitable for Item keys
		}

		return ret;
	}

	@Override
	public @NotNull Integer get(ItemConvertible item) {
		return ForgeHooks.getBurnTime(new ItemStack(item), null);
	}

	@Override
	public void add(ItemConvertible item, Integer cookTime) {
		if (cookTime > 32767) {
			LOGGER.warn("Tried to register an overly high cookTime: " + cookTime + " > 32767! (" + item + ")");
		}

		itemCookTimes.put(item, cookTime.intValue());
	}

	@Override
	public void add(Tag<Item> tag, Integer cookTime) {
		if (cookTime > 32767) {
			LOGGER.warn("Tried to register an overly high cookTime: " + cookTime + " > 32767! (" + getTagName(tag) + ")");
		}

		tagCookTimes.put(tag, cookTime.intValue());
	}

	@Override
	public void remove(ItemConvertible item) {
		add(item, 0);
	}

	@Override
	public void remove(Tag<Item> tag) {
		add(tag, 0);
	}

	@Override
	public void clear(ItemConvertible item) {
		itemCookTimes.removeInt(item);
	}

	@Override
	public void clear(Tag<Item> tag) {
		tagCookTimes.removeInt(tag);
	}

	private static String getTagName(Tag<?> tag) {
		if (tag instanceof Tag.Identified) {
			return ((Tag.Identified<?>) tag).getId().toString();
		}

		return tag.toString();
	}

	private void onFurnaceFueldBurnTime(@NotNull FurnaceFuelBurnTimeEvent event) {
		ItemStack stack = event.getItemStack();

		for (ItemConvertible item : itemCookTimes.keySet()) {
			if (stack.getItem().equals(item.asItem())) {
				int time = itemCookTimes.getInt(item);
				if (time > 0) {
					event.setBurnTime(time);
					return;
				}
			}
		}

		for (Tag<Item> tag : tagCookTimes.keySet()) {
			if (tag.contains(stack.getItem())) {
				int time = tagCookTimes.getInt(tag);
				if (time > 0) {
					event.setBurnTime(time);
					return;
				}
			}
		}
	}
}
