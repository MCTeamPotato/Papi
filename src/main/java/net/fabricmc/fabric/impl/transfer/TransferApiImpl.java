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

package net.fabricmc.fabric.impl.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

public class TransferApiImpl {
	public static final Logger LOGGER = LoggerFactory.getLogger("fabric-transfer-api-v1");
	public static final AtomicLong version = new AtomicLong();
	@SuppressWarnings("rawtypes")
	public static final Storage EMPTY_STORAGE = new Storage() {
		@Override
		public boolean supportsInsertion() {
			return false;
		}

		@Override
		public long insert(Object resource, long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public boolean supportsExtraction() {
			return false;
		}

		@Override
		public long extract(Object resource, long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public @NotNull Iterator<StorageView> iterator() {
			return Collections.emptyIterator();
		}

		@Override
		public long getVersion() {
			return 0;
		}
	};

	/**
	 * Not null when writing to an inventory in a transaction, null otherwise.
	 */
	public static final ThreadLocal<Object> SUPPRESS_SPECIAL_LOGIC = new ThreadLocal<>();

	public static <T> Iterator<T> singletonIterator(T it) {
		return new Iterator<T>() {
			boolean hasNext = true;

			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public T next() {
				if (!hasNext) {
					throw new NoSuchElementException();
				}

				hasNext = false;
				return it;
			}
		};
	}
}
