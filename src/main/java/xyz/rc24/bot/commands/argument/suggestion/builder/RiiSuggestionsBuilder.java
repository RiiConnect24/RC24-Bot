package xyz.rc24.bot.commands.argument.suggestion.builder;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import xyz.rc24.bot.commands.argument.suggestion.AnyStringSuggestion;
import xyz.rc24.bot.commands.argument.suggestion.MatchingStringSuggestion;

public class RiiSuggestionsBuilder extends SuggestionsBuilder {

	public RiiSuggestionsBuilder(SuggestionsBuilder builder) {
		this(builder.getInput(), builder.getStart());
	}
	
	public RiiSuggestionsBuilder(String input, int start) {
		super(input, start);
	}
	
	@Override
	public RiiSuggestionsBuilder suggest(final Suggestion suggestion) {
		return (RiiSuggestionsBuilder) super.suggest(suggestion); //super always returns this
	}
	
	public <S> RiiSuggestionsBuilder suggestAnyString(String name) {
		return suggest(new AnyStringSuggestion(getDefaultRange(), "<" + name + ">"));
	}
	
	public <S> RiiSuggestionsBuilder suggestAnyString(String name, Message tooltip) {
		return suggest(new AnyStringSuggestion(getDefaultRange(), "<" + name + ">", tooltip));
	}
	
	public <S> RiiSuggestionsBuilder suggestAsMatchable(String text) {
		return suggest(new MatchingStringSuggestion(getDefaultRange(), text));
	}
	
	protected StringRange getDefaultRange() {
		return StringRange.between(getStart(), getInput().length());
	}

}
