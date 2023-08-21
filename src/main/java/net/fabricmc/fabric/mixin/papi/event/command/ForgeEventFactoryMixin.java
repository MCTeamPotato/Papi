package net.fabricmc.fabric.mixin.papi.event.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeEventFactory.class, remap = false)
public class ForgeEventFactoryMixin {
    /**
     * Wait an inject in a constructor?
     * This is a new addition to Fabric's fork of mixin.
     * If you are not using fabric's fork of mixin this will fail.
     *
     * @reason Add commands before ambiguities are calculated.
     */
    @Inject(method = "onCommandRegister", at = @At("HEAD"))
    private static void fabric_addCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
        CommandRegistrationCallback.EVENT.invoker().register(dispatcher, environment == CommandManager.RegistrationEnvironment.DEDICATED);
    }
}
