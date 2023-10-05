package net.fabricmc.fabric.impl.command;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.NotNull;

public class CommandForgeImpl {
    public static void registerCommands(@NotNull RegisterCommandsEvent event) {
        CommandManager.RegistrationEnvironment environment = event.getEnvironment();
        CommandRegistrationCallback.EVENT.invoker().register(event.getDispatcher(), environment.equals(CommandManager.RegistrationEnvironment.ALL) || environment.equals(CommandManager.RegistrationEnvironment.DEDICATED));
    }
}
