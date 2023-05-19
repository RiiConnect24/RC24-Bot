package xyz.rc24.bot.commands.argument;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.StringRange;

public interface SuggestableArgument<T> extends ArgumentType<T> {

	static final Pattern NORMAL_ARGUMENT_REGEX = Pattern.compile("(.+?)( |$)+");
	
	public default Pattern getDelimiterRegex() {
		return NORMAL_ARGUMENT_REGEX;
	}
	
	/**
	 * @param input the input to parse, usually is all of the arguments, but may be just one subargument.
	 * @return the StringRange representing the start and end of the argument
	 */
	public default StringRange getLastArgRange(String input) {
		
		int lastSeparator = input.indexOf(' ');
		if(lastSeparator == -1) { 
			return new StringRange(0, input.length());
		}
		else {
			Matcher matcher = getDelimiterRegex().matcher(input);
			MatchResult start = null;
			MatchResult end = null;
			
			MatchResult result = null;
			while(matcher.find()) {
				result = matcher.toMatchResult();
			}
			
			return new StringRange(result.start(), result.end());
		}
	}
	
	/**
	 * Gets the last fully complete and valid argument that the user typed.
	 * 
	 * This argument may or may not be the at the end of the input.
	 * @param input
	 * @return
	 */
	public default String getLastValidArg(String input) {
		if(input.isBlank()) {
			return input;
		}
		StringRange range = getLastArgRange(input);
		return input.substring(range.getStart(), range.getEnd());
	}
	

	public default StringRange getCurrentArgRange(String input) {
		StringRange lastRange = getLastArgRange(input);
		StringRange currentRange = new StringRange(lastRange.getEnd(), input.length());
		if(lastRange.getEnd() == currentRange.getEnd()) {
			return lastRange;
		}
		return currentRange;
	}
	
	/**
	 * Gets the last argument that the user typed.
	 * 
	 * This argument may not be valid, and is always at the end of the input.
	 * @param input
	 * @return
	 */
	public default String getCurrentArg(String input) {
		if(input.isBlank()) {
			return input;
		}
		StringRange range = getCurrentArgRange(input);
		return input.substring(range.getStart(), range.getEnd());
	}

	
	public default int getCurrentArgIndex(String input) {
		int index = 0;
		if (input.isBlank()) {
			return index;
		}
		Matcher matcher = getDelimiterRegex().matcher(input);
		
		while(matcher.find()) {
			index++;
		}
		return index;
	}
	
	public default boolean canSuggest(String input) {
		return getCurrentArgIndex(input) == 0 || !getCurrentArg(input).isBlank();
	}
	
}
