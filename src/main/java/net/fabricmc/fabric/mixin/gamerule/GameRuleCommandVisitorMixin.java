package net.fabricmc.fabric.mixin.gamerule;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.impl.gamerule.EnumRuleCommand;
import net.fabricmc.fabric.impl.gamerule.EnumRuleType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GameRuleCommand.class, priority = 0)
public abstract class GameRuleCommandVisitorMixin {
	@Shadow
	static <T extends GameRules.Rule<T>> int executeQuery(ServerCommandSource source, GameRules.Key<T> key) {
		T rule = source.getServer().getGameRules().get(key);
		source.sendFeedback(Text.translatable("commands.gamerule.query", key.getName(), rule.toString()), false);
		return rule.getCommandResult();
	}

	@Shadow
	static <T extends GameRules.Rule<T>> int executeSet(CommandContext<ServerCommandSource> context, GameRules.Key<T> key) {
		ServerCommandSource serverCommandSource = context.getSource();
		T rule = serverCommandSource.getServer().getGameRules().get(key);
		rule.set(context, "value");
		serverCommandSource.sendFeedback(Text.translatable("commands.gamerule.set", key.getName(), rule.toString()), true);
		return rule.getCommandResult();
	}
	/**
	 * @author Kasualix
	 * @reason impl EnumRuleCommand register
	 **/
	@Overwrite
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("gamerule").requires((source) -> source.hasPermissionLevel(2));
		GameRules.accept(new GameRules.Visitor() {
			public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
				if (type instanceof EnumRuleType) {
					//noinspection rawtypes,unchecked
					EnumRuleCommand.register(literalArgumentBuilder, (GameRules.Key) key, (EnumRuleType) type);
					return;
				}
				literalArgumentBuilder.then((CommandManager.literal(key.getName()).executes((context) -> executeQuery(context.getSource(), key))).then(type.argument("value").executes((context) -> executeSet(context, key))));
			}
		});
		dispatcher.register(literalArgumentBuilder);
	}
}
