package net.fabricmc.fabric.mixin.command.client;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraftforge.client.ClientCommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientCommandSourceStack.class)
public abstract class ClientCommandSourceStackMixin implements FabricClientCommandSource {

    @Override
    public void sendFeedback(Text message) {
        getClient().inGameHud.addChatMessage(MessageType.SYSTEM, message, Util.NIL_UUID);
    }

    @Override
    public void sendError(Text message) {
        sendFeedback(new LiteralText("").append(message).formatted(Formatting.RED));
    }

    @Override
    public MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    @Override
    public ClientPlayerEntity getPlayer() {
        return getClient().player;
    }

    @Override
    public ClientWorld getWorld() {
        return getClient().world;
    }
}
