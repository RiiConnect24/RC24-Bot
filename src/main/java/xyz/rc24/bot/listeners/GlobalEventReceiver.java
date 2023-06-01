package xyz.rc24.bot.listeners;

import com.thegamecommunity.discord.event.receiver.DefaultDiscordEventReceiver;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Dispatcher;
import xyz.rc24.bot.commands.RiiContext;

public class GlobalEventReceiver extends DefaultDiscordEventReceiver {

	public GlobalEventReceiver(Dispatcher dispatcher) {
		super(dispatcher, RiiConnect24Bot.getInstance().config.isDebug());
	}
	
	@Override
	public RiiContext createContext(Object o) {
		return new RiiContext(o);
	}
	
	@Override
	public void onReady(ReadyEvent e) {
		super.onReady(e);
		RiiConnect24Bot.getInstance().jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.listening("commands"));
	}
	
}
