package xyz.rc24.bot.listeners;

import com.thegamecommunity.discord.command.DiscordDispatcher;
import com.thegamecommunity.discord.event.receiver.DefaultDiscordEventReceiver;

public class GlobalEventReceiver extends DefaultDiscordEventReceiver {

	public GlobalEventReceiver(DiscordDispatcher dispatcher) {
		super(dispatcher);
	}
	
}
