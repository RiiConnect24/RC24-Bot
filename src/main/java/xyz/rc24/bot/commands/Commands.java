package xyz.rc24.bot.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.thegamecommunity.brigadier.command.argument.LiteralArgument;
import com.thegamecommunity.brigadier.command.argument.suggestion.AnyStringSuggestionProvider;
import com.thegamecommunity.discord.command.brigadier.tree.DiscordArgumentBuilder;
import com.thegamecommunity.discord.command.brigadier.tree.DiscordBaseCommandNodeBuilder;

import xyz.rc24.bot.commands.botadm.Bash;
import xyz.rc24.bot.commands.botadm.Shutdown;
import xyz.rc24.bot.commands.general.BirthdayCmd;
import xyz.rc24.bot.commands.general.CountCmd;
import xyz.rc24.bot.commands.general.FlagCmd;
import xyz.rc24.bot.commands.general.InviteCmd;
import xyz.rc24.bot.commands.general.PingCmd;
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
	private final Dispatcher dispatcher = new Dispatcher();
	
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
		PingCmd.register(dispatcher);
		RiiTagCmd.register(dispatcher);
		RuleCmd.register(dispatcher);
		WadsCmd.register(dispatcher);
		WiiWare.register(dispatcher);
		
		FlagCmd.register(dispatcher);
		

		/**
		 * OWNER COMMANDS
		 */
		Bash.register(dispatcher);
		Shutdown.register(dispatcher);
	}
	
	public Dispatcher getDispatcher() {
		return dispatcher;
	}
	
	public static DiscordArgumentBuilder<RiiContext, String> suggestableString(String name) {
		return argument(name, LiteralArgument.of(name));
	}
	
	public static DiscordBaseCommandNodeBuilder<RiiContext> base(String name) {
		return DiscordBaseCommandNodeBuilder.discord(name);
	}
	
	public static DiscordBaseCommandNodeBuilder<RiiContext> base(String name, String description, String help) {
		return DiscordBaseCommandNodeBuilder.discord(name, description, help);
	}
	
	public static <T> DiscordArgumentBuilder<RiiContext, T> argument(String name, ArgumentType<T> type) {
		return DiscordArgumentBuilder.arg(name, type);
	}
	
	public static DiscordArgumentBuilder<RiiContext, String> anyString(String name) {
		return argument(name, StringArgumentType.string()).suggests(new AnyStringSuggestionProvider<>(name));
	}
	
	public static DiscordArgumentBuilder<RiiContext, String> anyStringGreedy(String name) {
		return argument(name, StringArgumentType.greedyString()).suggests(new AnyStringSuggestionProvider<>(name, true));
	}
	
}
