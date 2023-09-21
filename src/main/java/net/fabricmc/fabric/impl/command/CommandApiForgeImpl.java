package net.fabricmc.fabric.impl.command;

import com.mojang.brigadier.arguments.ArgumentType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CommandApiForgeImpl {
    @SuppressWarnings("rawtypes")
    private static final Map<Class, ArgumentSerializer<?, ?>> ARGUMENT_TYPE_CLASSES = new Object2ObjectOpenHashMap<>();
    private static final Map<Identifier, ArgumentSerializer<?, ?>> ARGUMENT_TYPES = new Object2ObjectOpenHashMap<>();

    public static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void registerArgumentType(Identifier id, Class<? extends A> clazz, ArgumentSerializer<A, T> serializer) {
        ARGUMENT_TYPE_CLASSES.put(clazz, serializer);
        ARGUMENT_TYPES.put(id, serializer);
    }
    public static void registerCommands(@NotNull RegisterCommandsEvent event) {
        CommandRegistrationCallback.EVENT.invoker().register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    public static void registerArgumentTypes(@NotNull RegisterEvent event) {
        event.register(ForgeRegistries.Keys.COMMAND_ARGUMENT_TYPES, helper -> {
            ARGUMENT_TYPE_CLASSES.forEach(ArgumentTypes::registerByClass);
            ARGUMENT_TYPES.forEach(helper::register);
        });
    }
}
