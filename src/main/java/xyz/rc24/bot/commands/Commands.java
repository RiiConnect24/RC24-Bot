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
	private final CommandDispatcher<CommandContext> dispatcher = new Dispatcher();
	public static final Commands DISPATCHER = new Commands();
	
	public Commands() {
		
		/**
		 * SLASH COMMANDS
		 */
		AddCmd.register(dispatcher);
		BirthdayCmd.register(dispatcher);
		BlocksCmd.register(dispatcher);
		CodeCmd.register(dispatcher);
		CountCmd.register(dispatcher);
		DNSCmd.register(dispatcher);
		ErrorInfoCmd.register(dispatcher);
		InviteCmd.register(dispatcher);
		RiiTagCmd.register(dispatcher); //this cmd might still be broken
		RuleCmd.register(dispatcher);
		WadsCmd.register(dispatcher);
		WiiWare.register(dispatcher);
		

		/**
		 * OWNER COMMANDS
		 */
		Bash.register(dispatcher);
		Shutdown.register(dispatcher);
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
		return argument(name, StringArgumentType.greedyString()).suggests(new AnyStringSuggestionProvider<>(name, true));
	}
	
	public CommandDispatcher<CommandContext> getDispatcher() {
		return dispatcher;
	}
	
}
