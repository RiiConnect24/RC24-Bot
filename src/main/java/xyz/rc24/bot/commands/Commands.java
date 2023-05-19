package xyz.rc24.bot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import xyz.rc24.bot.commands.argument.GlobalLiteralArgumentBuilder;
import xyz.rc24.bot.commands.argument.LiteralArgument;
import xyz.rc24.bot.commands.argument.suggestion.AnyStringSuggestionProvider;
import xyz.rc24.bot.commands.botadm.Bash;
import xyz.rc24.bot.commands.botadm.Shutdown;

@SuppressWarnings("rawtypes")
public class Commands {
	private final CommandDispatcher<CommandContext> slashDispatcher = new Dispatcher();
	private final CommandDispatcher<CommandContext> nonSlashDispatcher = new Dispatcher();
	public static final Commands DISPATCHER = new Commands();
	
	public Commands() {
		//HelpCommand.register(dispatcher);
		
		/**
		 * NON SLASH COMMANDS
		 */
		Bash.register(nonSlashDispatcher);
		Shutdown.register(nonSlashDispatcher);
	}
	
	@Deprecated
	public static LiteralArgumentBuilder<CommandContext> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}
	
	public static RequiredArgumentBuilder<CommandContext, String> suggestableString(String name) {
		return argument(name, LiteralArgument.of(name));
	}
	
	public static GlobalLiteralArgumentBuilder<CommandContext> global(String name) {
		return GlobalLiteralArgumentBuilder.literal(name);
	}
	
	public static <T> RequiredArgumentBuilder<CommandContext, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
	public static RequiredArgumentBuilder<CommandContext, String> anyString(String name) {
		return argument(name, StringArgumentType.string()).suggests(new AnyStringSuggestionProvider<>(name));
	}
	
	public static RequiredArgumentBuilder<CommandContext, String> anyStringGreedy(String name) {
		return argument(name, StringArgumentType.greedyString()).suggests(new AnyStringSuggestionProvider<>(name));
	}
	
	public CommandDispatcher<CommandContext> getSlashDispatcher() {
		return this.nonSlashDispatcher;
	}
	
	public CommandDispatcher<CommandContext> getNonSlashDispatcher() {
		return this.nonSlashDispatcher;
	}
	
}
