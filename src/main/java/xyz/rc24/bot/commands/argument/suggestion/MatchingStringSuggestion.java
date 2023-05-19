package xyz.rc24.bot.commands.argument.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;

public class MatchingStringSuggestion extends MatchingSuggestion<String>{

	public MatchingStringSuggestion(StringRange range, String text) {
		super(range, text);
	}
	
	public MatchingStringSuggestion(StringRange range, String text, Message tooltip) {
		super(range, text, tooltip);
	}

	@Override
	public boolean matches(String s) {
		if(s.isBlank() || getText() == null) {
			return false;
		}
		return getText().toLowerCase().endsWith(s.toLowerCase());
	}

	@Override
	public String getText() {
		return getValue();
	}

}
