package xyz.rc24.bot.commands.argument.suggestion;

import com.mojang.brigadier.context.StringRange;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import xyz.rc24.bot.commands.CommandUtils;

public class DiscordUserSuggestion extends MatchingSuggestion<User> {

	private Member member;
	
	public DiscordUserSuggestion(StringRange range, Member member) {
		this(range, member.getUser());
		this.member = member;
	}
	
	public DiscordUserSuggestion(StringRange range, User val) {
		super(range, val);
	}

	@Override
	public String getText() {
		if(member != null) {
			return toSuggestionString(member);
		}
		return toSuggestionString(getValue());
	}

	@Override
	public boolean matches(String s) {
		if(s.isBlank()) {
			return true;
		}
		s = CommandUtils.lastArgOf(s);
		if(member != null) {
			return 							
				(member.getIdLong() + "").startsWith(s) ||
				(member.getEffectiveName().toLowerCase() + "#" + member.getUser().getDiscriminator()).startsWith(CommandUtils.lastArgOf(s.toLowerCase().replace("@", ""))) ||
				(member.getUser().getAsTag().toLowerCase().startsWith(CommandUtils.lastArgOf(s.replace("@", ""))));
		}
		return
			(getValue().getIdLong() + "").startsWith(s) ||
			(getValue().getAsTag().startsWith(CommandUtils.lastArgOf(s.toLowerCase().replace("@", ""))));
	}

	private static String toSuggestionString(Member user) {
		return "<@" + user.getEffectiveName() + "#" + user.getUser().getDiscriminator() + ":(" + user.getIdLong() + ")>";
	}
	
	private static String toSuggestionString(User user) {
		return "<@" + user.getName() + "#" + user.getDiscriminator() + ":(" + user.getIdLong() + ")>";
	}
	
}
