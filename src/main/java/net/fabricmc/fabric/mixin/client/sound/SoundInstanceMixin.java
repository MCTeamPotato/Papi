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

package net.fabricmc.fabric.mixin.client.sound;

import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundLoader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.sound.SoundInstance;

import net.fabricmc.fabric.api.client.sound.v1.FabricSoundInstance;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.concurrent.CompletableFuture;

@Mixin(SoundInstance.class)
public interface SoundInstanceMixin extends FabricSoundInstance {
    /**
     * @author Su5ed
     * @reason Override forge method in SoundInstance
     */
    @Overwrite
    default CompletableFuture<AudioStream> getStream(SoundLoader soundBuffers, @NotNull Sound sound, boolean looping) {
        return getAudioStream(soundBuffers, sound.getLocation(), looping);
    }
}
