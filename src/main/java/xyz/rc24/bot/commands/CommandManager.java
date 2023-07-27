package xyz.rc24.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import xyz.rc24.bot.commands.botadm.BashCommand;
import xyz.rc24.bot.commands.botadm.ShutdownCommand;
import xyz.rc24.bot.commands.general.*;
import xyz.rc24.bot.commands.tools.DefaultAddCommand;
import xyz.rc24.bot.commands.tools.StatsCommand;
import xyz.rc24.bot.commands.wii.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {

	private final HashMap<String, Command> commands = new HashMap<>();

	public CommandManager() {
		registerCommand(new WiiWareCommand());
		registerCommand(new WadsCommand());
		registerCommand(new ErrorInfoCommand());
		registerCommand(new DNSCommand());
		registerCommand(new CodeCommand());
		registerCommand(new BlocksCommand());
		registerCommand(new AddCommand());
		registerCommand(new StatsCommand());
		registerCommand(new DefaultAddCommand());
		registerCommand(new RuleCommand());
		registerCommand(new RiiTagCommand());
		registerCommand(new InviteCommand());
		registerCommand(new FlagCommand());
		registerCommand(new CountCommand());
		registerCommand(new BirthdayCommand());
		registerCommand(new ShutdownCommand());
		registerCommand(new BashCommand());
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		Command command = commands.get(event.getName());
		command.onCommand(event);
	}

	private void registerCommand(Command command) {
		commands.putIfAbsent(command.getCommandData().getName(), command);
	}

	public HashMap<String, Command> getCommands() {
		return commands;
	}

	public List<SlashCommandData> getCommandDataList() {
		return this.commands.values().stream().map(Command::getCommandData).collect(Collectors.toList());
	}

}
