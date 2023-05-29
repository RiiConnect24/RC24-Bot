package xyz.rc24.bot.listeners;

import com.thegamecommunity.discord.event.receiver.DefaultDiscordEventReceiver;

import xyz.rc24.bot.commands.Dispatcher;
import xyz.rc24.bot.commands.RiiContext;

public class GlobalEventReceiver extends DefaultDiscordEventReceiver {

	public GlobalEventReceiver(Dispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public RiiContext createContext(Object o) {
		return new RiiContext(o);
	}
	
}
