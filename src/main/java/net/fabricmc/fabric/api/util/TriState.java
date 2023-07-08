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

package net.fabricmc.fabric.api.util;

public enum TriState {
	FALSE,
	DEFAULT,
	TRUE;

	public static final TriState TRI_STATE_FALSE = FALSE;
	public static final TriState TRI_STATE_DEFAULT = DEFAULT;
	public static final TriState TRI_STATE_TRUE = TRUE;

	public static TriState of(boolean b) {
		return b ? TRI_STATE_TRUE : TRI_STATE_FALSE;
	}

	public boolean get() {
		return this == TRI_STATE_TRUE;
	}
}
