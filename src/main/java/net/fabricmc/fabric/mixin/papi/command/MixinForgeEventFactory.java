package net.fabricmc.fabric.mixin.papi.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeEventFactory.class, remap = false)
public abstract class MixinForgeEventFactory {
    @Inject(method = "onCommandRegister", at = @At("HEAD"))
    private static void fabric_addCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandManager.RegistrationEnvironment environment, CommandRegistryAccess context, CallbackInfo ci) {
        CommandRegistrationCallback.EVENT.invoker().register(dispatcher, context, environment);
    }
}
