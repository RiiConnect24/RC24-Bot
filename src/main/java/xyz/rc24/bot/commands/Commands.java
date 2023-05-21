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
import xyz.rc24.bot.commands.general.BirthdayCmd;
import xyz.rc24.bot.commands.general.CountCmd;
import xyz.rc24.bot.commands.general.InviteCmd;
import xyz.rc24.bot.commands.general.RiiTagCmd;
import xyz.rc24.bot.commands.general.RuleCmd;
import xyz.rc24.bot.commands.wii.AddCmd;
import xyz.rc24.bot.commands.wii.BlocksCmd;
import xyz.rc24.bot.commands.wii.CodeCmd;
import xyz.rc24.bot.commands.wii.DNSCmd;
import xyz.rc24.bot.commands.wii.ErrorInfoCmd;
import xyz.rc24.bot.commands.wii.WadsCmd;
import xyz.rc24.bot.commands.wii.WiiWare;

@SuppressWarnings("rawtypes")
public class Commands {
	private final CommandDispatcher<CommandContext> slashDispatcher = new Dispatcher();
	private final CommandDispatcher<CommandContext> nonSlashDispatcher = new Dispatcher();
	public static final Commands DISPATCHER = new Commands();
	
	public Commands() {
		
		/**
		 * SLASH COMMANDS
		 */
		AddCmd.register(slashDispatcher);
		BirthdayCmd.register(slashDispatcher);
		BlocksCmd.register(slashDispatcher);
		CodeCmd.register(slashDispatcher);
		CountCmd.register(slashDispatcher);
		DNSCmd.register(slashDispatcher);
		ErrorInfoCmd.register(slashDispatcher);
		InviteCmd.register(slashDispatcher);
		RiiTagCmd.register(slashDispatcher); //this cmd might still be broken
		RuleCmd.register(slashDispatcher);
		WadsCmd.register(slashDispatcher);
		WiiWare.register(slashDispatcher);
		
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
