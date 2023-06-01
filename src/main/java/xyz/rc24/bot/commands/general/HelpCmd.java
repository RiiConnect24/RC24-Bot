package xyz.rc24.bot.commands.general;

/*
import java.util.List;
import java.util.Map;

import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.thegamecommunity.discord.command.brigadier.tree.DiscordBaseCommandNodeBuilder;

import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.Dispatcher;
import xyz.rc24.bot.commands.RiiContext;
*/

public class HelpCmd {

	/*
	
	private static DiscordBaseCommandNodeBuilder<RiiContext> helpCommandBaseNode;
	private static Map<CommandNode<RiiContext>, String> usages;
	
	public static final void register(Dispatcher dispatcher) {
		helpCommandBaseNode = Commands.base("help");
		helpCommandBaseNode.setDescription("Shows information about commands.");
		helpCommandBaseNode.setHelp("help [<command>]");
		helpCommandBaseNode.executes((context) -> {
			showHelp(dispatcher, context.getSource(), context.getNodes());
			return 1;
		});
		
		dispatcher.register(helpCommandBaseNode);
	}
	
	public static final void showHelp(Dispatcher dispatcher, RiiContext context, List<ParsedCommandNode<RiiContext>> nodes) {
		String help = dispatcher;
	}
	
	public static final void buildHelper(Dispatcher dispatcher) {
		for(CommandNode<RiiContext> child : dispatcher.getRoot().getChildren()) {
			
		}
	}
	
	*/
	
}
