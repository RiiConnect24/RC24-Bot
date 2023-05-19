package xyz.rc24.bot.commands.argument.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;

public class AnyStringSuggestion extends MatchingSuggestion<String> {

	public AnyStringSuggestion(StringRange range, String text) {
		super(range, text);
	}
	
	public AnyStringSuggestion(StringRange range, String text, Message tooltip) {
		super(range, text, tooltip);
	}

	@Override
	public boolean matches(String s) {
		return s != null && !s.isBlank();
	}

	@Override
	public String getText() {
		return getValue();
	}

}
