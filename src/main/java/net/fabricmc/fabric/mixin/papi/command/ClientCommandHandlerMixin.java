package net.fabricmc.fabric.mixin.papi.command;

import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.minecraftforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientCommandHandler.class, remap = false)
public abstract class ClientCommandHandlerMixin {
    @Inject(method = "runCommand", at = @At("RETURN"), cancellable = true)
    private static void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        if (ClientCommandInternals.executeCommand(command)) cir.setReturnValue(false);
    }
}
