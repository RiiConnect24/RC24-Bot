package xyz.rc24.bot.commands;

import java.util.function.Consumer;

import com.thegamecommunity.discord.command.DiscordContext;

import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.Config;
import xyz.rc24.bot.RiiConnect24Bot;

public class RiiContext<E> extends DiscordContext<E> {

	public RiiContext(E e) {
		super(e);
	}

	public Bot getBot() {
		return RiiConnect24Bot.getInstance();
	}
	
	/**
	 * 
	 * @param ephemeral whether the deferred message should be ephemral. Has no effect if this context does not have in interaction hook.
	 * @return a new DiscordContext based on this context's interaction hook, or this if the context does not have an interaction hook.
	 */
	@Override
	public RiiContext defer(boolean ephemeral) {
		Interaction interaction = getInteraction();
		if(interaction != null) {
			((IReplyCallback) interaction).deferReply(ephemeral).queue();
			return new RiiContext(((IDeferrableCallback) interaction).getHook());
		}
		return this;
	}
	
	/**
	 * @return a new context in a private message channel with
	 * the current author.
	 * 
	 * If the author has no private context, returns this;
	 * 
	 * If you need to check if the context is a user context, just 
	 * call isPrivateContext() on the returned context
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RiiContext getPrivateContext() {
		if(getUser() == null || !getUser().hasPrivateChannel()) {
			return this;
		}
		try {
			return new RiiContext(getUser());
		}
		catch(UnsupportedOperationException e) {
			return this; //just in case
		}
	}
	
	public boolean isOwnerContext() {
		if(getUser() != null) {
			Long id = getUser().getIdLong();
			Config config = getBot().getConfig();
			return config.getPrimaryOwner() == id || config.getSecondaryOwners().contains(id);
		}
		return false;
	}
	
	public static Consumer<RiiContext> requiresServerContext = (context) -> context.replyServerOnlyCommand();
	
	public static Consumer<RiiContext> requiresDiscordContext = (context) -> context.replyDiscordOnlyCommand();
	
}
