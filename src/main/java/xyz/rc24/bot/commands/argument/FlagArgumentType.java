package xyz.rc24.bot.commands.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import xyz.rc24.bot.commands.CommandUtils;
import xyz.rc24.bot.commands.exception.ParseExceptions;
import xyz.rc24.bot.core.entities.Flag;
import static xyz.rc24.bot.core.entities.Flag.*;

public class FlagArgumentType implements ArgumentType<Flag> {

	public static final FlagArgumentType ANY_FLAG = of(Flag.values());
	public static final FlagArgumentType KNOWN_FLAGS;
	public static final FlagArgumentType NON_COUNTRIES;
	public static final FlagArgumentType COUNTRIES;
	static {
		List<Flag> knownFlags = new ArrayList<>(Arrays.asList(Flag.values()));
		knownFlags.remove(Flag.UNKNOWN);
		KNOWN_FLAGS = of(knownFlags.toArray(new Flag[]{}));
		
		Flag[] nonCountries = new Flag[] {ANTARCTICA, EUROPEAN_UNION, PIRATE_FLAG, RAINBOW_FLAG, TRANSGENDER_FLAG, UNITED_NATIONS};
		NON_COUNTRIES = of(nonCountries);
		
		List<Flag> countries = new ArrayList<>(knownFlags);
		countries.removeAll(Arrays.asList(nonCountries));
		COUNTRIES = of(countries.toArray(new Flag[]{}));
	}
	 
	
	private final ArrayList<Flag> flags = new ArrayList<Flag>();
	
	private FlagArgumentType(Flag... flags) {
		this.flags.addAll(Arrays.asList(flags));
	}
	
	public static FlagArgumentType of(Flag... flags) {
		return new FlagArgumentType(flags);
	}
	
	@Override
	public <S> Flag parse(S context, StringReader reader) throws CommandSyntaxException {
		String text;
		if(reader.peek() == '\"') {
			text = CommandUtils.readQuotedString(reader);
		}
		else {
			text = CommandUtils.readString(reader);
		}
		
		Flag flag = Flag.fromSuggestion(text);
		if(flags.contains(flag)) {
			return flag;
		}
		
		if(flag == Flag.UNKNOWN) {
			throw ParseExceptions.NONEXISTANT.create("Flag", text);
		}
		else {
			throw ParseExceptions.INVALID_CHOICE_CNT.create(flags);

		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(com.mojang.brigadier.context.CommandContext<S> context, SuggestionsBuilder builder) {
		String flagText = CommandUtils.lastArgOf(builder.getInput());
		for(Flag f : flags) {
			if(f.getEmote().equals(flagText)) {
				builder.suggest(f.getEmote() + ":" + f.getName());
				return builder.buildFuture();
			}
			if(f.getName().toLowerCase().startsWith(flagText.toLowerCase())) {
				if(f.getName().indexOf(' ') > -1) {
					builder.suggest("\"" + f.getEmote() + ":" + f.getName() + "\"");
				}
				else {
					builder.suggest(f.getEmote() + ":" + f.getName());
				}
			}
		}
		return builder.buildFuture();
	}
	
}
