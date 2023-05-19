package xyz.rc24.bot.commands.argument.suggestion.builder;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import xyz.rc24.bot.commands.argument.suggestion.AnyStringSuggestion;
import xyz.rc24.bot.commands.argument.suggestion.MatchingStringSuggestion;

public class ExciteSuggestionsBuilder extends SuggestionsBuilder {

	public ExciteSuggestionsBuilder(SuggestionsBuilder builder) {
		this(builder.getInput(), builder.getStart());
	}
	
	public ExciteSuggestionsBuilder(String input, int start) {
		super(input, start);
	}
	
	@Override
	public ExciteSuggestionsBuilder suggest(final Suggestion suggestion) {
		return (ExciteSuggestionsBuilder) super.suggest(suggestion); //super always returns this
	}
	
	public <S> ExciteSuggestionsBuilder suggestAnyString(String name) {
		return suggest(new AnyStringSuggestion(getDefaultRange(), "<" + name + ">"));
	}
	
	public <S> ExciteSuggestionsBuilder suggestAnyString(String name, Message tooltip) {
		return suggest(new AnyStringSuggestion(getDefaultRange(), "<" + name + ">", tooltip));
	}
	
	public <S> ExciteSuggestionsBuilder suggestAsMatchable(String text) {
		return suggest(new MatchingStringSuggestion(getDefaultRange(), text));
	}
	
	protected StringRange getDefaultRange() {
		return StringRange.between(getStart(), getInput().length());
	}

}
