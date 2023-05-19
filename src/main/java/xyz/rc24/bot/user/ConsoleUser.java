package xyz.rc24.bot.user;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

public class ConsoleUser implements User {

	public static final ConsoleUser INSTANCE = new ConsoleUser();
	
	private ConsoleUser() {}

	@Override
	public String getAsMention() {
		return "CONSOLE";
	}

	@Override
	public long getIdLong() {
		return 0;
	}

	@Override
	public String getName() {
		return "CONSOLE";
	}

	@Override
	public String getDiscriminator() {
		return "";
	}

	@Override
	public String getAvatarId() {
		return null;
	}

	@Override
	public String getDefaultAvatarId() {
		return "0";
	}

	@Override
	public CacheRestAction<Profile> retrieveProfile() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAsTag() {
		return "CONSOLE";
	}

	@Override
	public boolean hasPrivateChannel() {
		return false;
	}

	@Override
	public CacheRestAction<PrivateChannel> openPrivateChannel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Guild> getMutualGuilds() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean isBot() {
		return true;
	}

	@Override
	public boolean isSystem() {
		return true;
	}

	@Override
	public JDA getJDA() {
		//TODO: get JDA instance
		throw new UnsupportedOperationException();
	}

	@Override
	public EnumSet<UserFlag> getFlags() {
		return UserFlag.getFlags(0);
	}

	@Override
	public int getFlagsRaw() {
		return 0;
	}
	
}
