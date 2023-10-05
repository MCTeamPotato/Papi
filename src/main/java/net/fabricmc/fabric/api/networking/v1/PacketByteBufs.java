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

package net.fabricmc.fabric.api.networking.v1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Helper methods for working with and creating {@link PacketByteBuf}s.
 */
public final class PacketByteBufs {
	private static final PacketByteBuf EMPTY_PACKET_BYTE_BUF = new PacketByteBuf(Unpooled.EMPTY_BUFFER);

	/**
	 * Returns an empty instance of packet byte buf.
	 *
	 * @return an empty buf
	 */
	public static PacketByteBuf empty() {
		return EMPTY_PACKET_BYTE_BUF;
	}

	/**
	 * Returns a new heap memory-backed instance of packet byte buf.
	 *
	 * @return a new buf
	 */
	@Contract(" -> new")
	public static @NotNull PacketByteBuf create() {
		return new PacketByteBuf(Unpooled.buffer());
	}

	// Convenience methods for byte buf methods that return a new byte buf

	/**
	 * Wraps the newly created buf from {@code buf.readBytes} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param length the number of bytes to transfer
	 * @return the transferred bytes
	 * @see ByteBuf#readBytes(int)
	 */
	@Contract("_, _ -> new")
	public static @NotNull PacketByteBuf readBytes(ByteBuf buf, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.readBytes(length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.readSlice} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param length the size of the new slice
	 * @return the newly created slice
	 * @see ByteBuf#readSlice(int)
	 */
	@Contract("_, _ -> new")
	public static @NotNull PacketByteBuf readSlice(ByteBuf buf, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.readSlice(length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.readRetainedSlice} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param length the size of the new slice
	 * @return the newly created slice
	 * @see ByteBuf#readRetainedSlice(int)
	 */
	@Contract("_, _ -> new")
	public static @NotNull PacketByteBuf readRetainedSlice(ByteBuf buf, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.readRetainedSlice(length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.copy} in a packet byte buf.
	 *
	 * @param buf the original buf
	 * @return a copy of the buf
	 * @see ByteBuf#copy()
	 */
	@Contract("_ -> new")
	public static @NotNull PacketByteBuf copy(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.copy());
	}

	/**
	 * Wraps the newly created buf from {@code buf.copy} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param index  the starting index
	 * @param length the size of the copy
	 * @return a copy of the buf
	 * @see ByteBuf#copy(int, int)
	 */
	@Contract("_, _, _ -> new")
	public static @NotNull PacketByteBuf copy(ByteBuf buf, int index, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.copy(index, length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.slice} in a packet byte buf.
	 *
	 * @param buf the original buf
	 * @return a slice of the buf
	 * @see ByteBuf#slice()
	 */
	@Contract("_ -> new")
	public static @NotNull PacketByteBuf slice(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.slice());
	}

	/**
	 * Wraps the newly created buf from {@code buf.retainedSlice} in a packet byte buf.
	 *
	 * @param buf the original buf
	 * @return a slice of the buf
	 * @see ByteBuf#retainedSlice()
	 */
	@Contract("_ -> new")
	public static @NotNull PacketByteBuf retainedSlice(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.retainedSlice());
	}

	/**
	 * Wraps the newly created buf from {@code buf.slice} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param index  the starting index
	 * @param length the size of the copy
	 * @return a slice of the buf
	 * @see ByteBuf#slice(int, int)
	 */
	@Contract("_, _, _ -> new")
	public static @NotNull PacketByteBuf slice(ByteBuf buf, int index, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.slice(index, length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.retainedSlice} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param index  the starting index
	 * @param length the size of the copy
	 * @return a slice of the buf
	 * @see ByteBuf#retainedSlice(int, int)
	 */
	@Contract("_, _, _ -> new")
	public static @NotNull PacketByteBuf retainedSlice(ByteBuf buf, int index, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.retainedSlice(index, length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.duplicate} in a packet byte buf.
	 *
	 * @param buf the original buf
	 * @return a duplicate of the buf
	 * @see ByteBuf#duplicate()
	 */
	@Contract("_ -> new")
	public static @NotNull PacketByteBuf duplicate(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.duplicate());
	}

	/**
	 * Wraps the newly created buf from {@code buf.retainedDuplicate} in a packet byte buf.
	 *
	 * @param buf the original buf
	 * @return a duplicate of the buf
	 * @see ByteBuf#retainedDuplicate()
	 */
	@Contract("_ -> new")
	public static @NotNull PacketByteBuf retainedDuplicate(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new PacketByteBuf(buf.retainedDuplicate());
	}

	private PacketByteBufs() {
	}
}
