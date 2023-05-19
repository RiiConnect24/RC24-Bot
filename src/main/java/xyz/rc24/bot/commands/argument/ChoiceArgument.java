package xyz.rc24.bot.commands.argument;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import xyz.rc24.bot.commands.CommandUtils;
import xyz.rc24.bot.commands.exception.ParseExceptions;

public class ChoiceArgument<T> implements ArgumentType<T>, Choice<T> {
	
	private final boolean force;
	private final Class<T> type;
	private final LinkedHashSet<T> choices = new LinkedHashSet<>();
	
	private ChoiceArgument(Class<T> type, boolean force, T... choices) {
		this.force = force;
		this.type = type;
		this.choices.addAll(Arrays.asList(choices));
	}
	
	public static ChoiceArgument<String> oneOf(Object... choices) {
		String[] choiceStrings = new String[choices.length];
		for(int i = 0; i < choices.length; i++) {
			choiceStrings[i] = choices[i].toString();
		}
		return new ChoiceArgument<String>(String.class, true, choiceStrings);
	}
	
	public static ChoiceArgument<String> oneOf(String... choices) {
		return new ChoiceArgument<String>(String.class, true, choices);
	}
	
	public static ChoiceArgument<Integer> oneOf(Integer... choices) {
		return new ChoiceArgument<Integer>(Integer.class, true, choices);
	}
	
	public static ChoiceArgument<Double> oneOf(Double... choices) {
		return new ChoiceArgument<Double>(Double.class, true, choices);
	}
	
	public static ChoiceArgument<String> suggest(Object... suggestions) {
		String[] suggestionStrings = new String[suggestions.length];
		for(int i = 0; i < suggestions.length; i++) {
			suggestionStrings[i] = suggestions[i].toString();
		}
		return new ChoiceArgument<String>(String.class, true, suggestionStrings);
	}
	
	public static ChoiceArgument<String> suggest(String... suggestions) {
		return new ChoiceArgument<String>(String.class, false, suggestions);
	}
	
	public static ChoiceArgument<Integer> suggest(Integer... suggestions) {
		return new ChoiceArgument<Integer>(Integer.class, false, suggestions);
	}
	
	public static ChoiceArgument<Double> suggest(Double... suggestions) {
		return new ChoiceArgument<Double>(Double.class, false, suggestions);
	}
	
	@SuppressWarnings("unchecked")
	public T[] getChoices(Class<T> type) {
		return (T[]) choices.toArray();
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final com.mojang.brigadier.context.CommandContext<S> context, final SuggestionsBuilder builder) {
		for(Object o : choices) {
			builder.suggest(o.toString());
		}
		
		return builder.buildFuture();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <S> T parse(S context, StringReader reader) throws CommandSyntaxException {
		String val = CommandUtils.readString(reader);
		if(type == String.class) {
			if(force) {
				for(String s : (LinkedHashSet<String>)choices) {
					if(s.equals(val)) {
						return (T) s;
					}
				}
				throw ParseExceptions.INVALID_CHOICE.create(choices);
			}
			return (T) CommandUtils.readString(reader);
		}
		else if (type == Integer.class) {
			if(force) {
				for(Integer i : (LinkedHashSet<Integer>)choices) {
					if(i.equals(Integer.parseInt(val))) {
						return (T) i;
					}
				}
				throw ParseExceptions.INVALID_CHOICE.create(choices);
			}
			return (T)(Integer) Integer.parseInt(CommandUtils.readString(reader));
		}
		else if (type == Double.class) {
			return (T)(Double) Double.parseDouble(CommandUtils.readString(reader));
		}
		else {
			throw new IllegalStateException("ChoiceArgument cannot accept a " + type.getClass().getCanonicalName());
		}
	}

}
