package xyz.rc24.bot.commands;

import java.util.function.Function;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandUtils<Slash, Interaction> {
	
	private final CommandDispatcher<CommandContext<Slash>> slashCommandDispatcher;
	private final CommandDispatcher<CommandContext<Interaction>> interactionDispatcher;
	private final Function<Object, CommandContext<?>> contextGenerator;
	
	public CommandUtils(CommandDispatcher<CommandContext<Slash>> commandDispatcher, CommandDispatcher<CommandContext<Interaction>> interactionDispatcher, Function<?, CommandContext<?>> contextGenerator) {
		this.slashCommandDispatcher = commandDispatcher;
		this.interactionDispatcher = interactionDispatcher;
		this.contextGenerator = (Function<Object, CommandContext<?>>) contextGenerator;
	}
	
	public <E> CommandContext<E> context(E e) {
		return (CommandContext<E>)contextGenerator.apply(e);
	}
	
	public CommandDispatcher<CommandContext<Slash>> getSlashCommandDispatcher() {
		return slashCommandDispatcher;
	}
	
	public CommandDispatcher<CommandContext<Interaction>> getInteractionDispatcher() {
		return interactionDispatcher;
	}
	
	public static String readString(StringReader reader) {
		StringBuilder ret = new StringBuilder("");
		while(reader.canRead() && !Character.isSpaceChar(reader.peek())) {
			ret.append(reader.read());
		}
		return ret.toString();
	}
	
	public static String readUntilEnd(StringReader reader) {
		StringBuilder ret = new StringBuilder("");
		while(reader.canRead()) {
			ret.append(reader.read());
		}
		return ret.toString();
	}
	
	public static String readQuotedString(StringReader reader) throws CommandSyntaxException {
		StringBuilder ret = new StringBuilder("");
		if(reader.canRead()) {
			if(reader.peek() == '"') {
				reader.read();
			}
			else {
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(reader);
			}
		}
		boolean foundEndQuote = false;
		while(reader.canRead()) {
			char c = reader.read();
			if(c == '"') {
				foundEndQuote = true;
				break;
			}
			ret.append(c);
		}
		if(!foundEndQuote) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext(reader);
		}
		return ret.toString();
	}
	
	public static String lastArgOf(String command) {
		if(command.indexOf(' ') > 0) {
			return command.substring(command.lastIndexOf(' ') + 1);
		}
		return "";
	}
}
