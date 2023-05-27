package xyz.rc24.bot.commands;

import com.thegamecommunity.discord.command.DiscordDispatcher;

@SuppressWarnings("rawtypes")
public class Dispatcher extends DiscordDispatcher<RiiContext> {

	@Override
	@SuppressWarnings("unchecked")
	public RiiContext newContext(Object o) {
		return new RiiContext(o);
	}
	
}