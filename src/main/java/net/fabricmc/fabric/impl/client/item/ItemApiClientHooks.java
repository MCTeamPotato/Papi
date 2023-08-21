package net.fabricmc.fabric.impl.client.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class ItemApiClientHooks {
    private ItemApiClientHooks() {}
    public static PlayerEntity getClientPlayerSafely() {
        return MinecraftClient.getInstance().player;
    }
}
