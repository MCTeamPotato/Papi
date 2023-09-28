package net.fabricmc.fabric.impl.command;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.NotNull;

public class CommandApiForgeImpl {
    public static void registerCommands(@NotNull RegisterCommandsEvent event) {
        CommandRegistrationCallback.EVENT.invoker().register(event.getDispatcher(), event.getEnvironment() == CommandManager.RegistrationEnvironment.DEDICATED);
    }
}
