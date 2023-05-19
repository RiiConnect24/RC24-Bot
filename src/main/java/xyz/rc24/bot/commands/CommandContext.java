package xyz.rc24.bot.commands;

import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import xyz.rc24.bot.user.ConsoleUser;

public class CommandContext<E> {
	
	private E event;
	private EmbedBuilder embedBuilder;
	
	public CommandContext(E e) {
		if(e instanceof MessageReceivedEvent || e instanceof Interaction || e instanceof GuildReadyEvent || e instanceof User) {
			this.event = e;
		}
		else {
			throw new IllegalArgumentException(e.getClass().getCanonicalName());
		}
	}

	public User getAuthor() {
		if(event instanceof Interaction) {
			return ((Interaction) event).getUser();
		}
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getAuthor();
		}
		if(event instanceof User) {
			return (User) event;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEvent(Class<T> type) throws ClassCastException {	
		if(type.isAssignableFrom(event.getClass())) {
			return (T)event;
		}
		throw new ClassCastException(event + " cannot be cast to " + type.getCanonicalName());
	}
	
	public boolean isDiscordContext() {
		return event instanceof ISnowflake;
	}
	
	public void queueMessage(MessageCreateData messageData) {
		queueMessage(messageData, false, false);
	}
	
	public void queueMessage(MessageCreateData messageData, boolean ephemeral, boolean silent) {
		if(event instanceof IReplyCallback) {
			((IReplyCallback) event).reply(messageData).setEphemeral(ephemeral).setSuppressedNotifications(silent).queue();
		}
		else if(event instanceof MessageReceivedEvent) {
			((MessageReceivedEvent) event).getChannel().sendMessage(messageData).queue();
		}
		else if(event instanceof User) {
			if(event instanceof ConsoleUser) {
				System.out.println(messageData.getContent());
			}
			else {
				((User)event).openPrivateChannel().queue((att) -> {((PrivateChannel)att).sendMessage(messageData).queue();});
			}
		}
		else {
			throw new UnsupportedOperationException("Cannot reply to a " + event.getClass().getCanonicalName());
		}
	}
	
	public void queueMessage(String message) {
		message = trim(message);
		queueMessage(new MessageCreateBuilder().setContent(message).build(), false, false);
	}
	
	public void queueMessage(String message, boolean ephemeral, boolean silent) {
		message = trim(message);
		queueMessage(new MessageCreateBuilder().setContent(message).build(), ephemeral, silent);
	}
	
	public InteractionHook completeMessage(MessageCreateData messageData) {
		return completeMessage(messageData, false, false);
	}
	
	public InteractionHook completeMessage(MessageCreateData messageData, boolean ephemeral, boolean silent) {
		if(event instanceof IReplyCallback) {
			return ((IReplyCallback) event).reply(messageData).setEphemeral(ephemeral).setSuppressedNotifications(silent).complete();
		}
		else if(event instanceof MessageReceivedEvent) {
			((MessageReceivedEvent) event).getChannel().sendMessage(messageData).complete();
			return null;
		}
		else if(event instanceof User) {
			if(event instanceof ConsoleUser) {
				System.out.println(messageData.getContent());
			}
			else {
				PrivateChannel channel = ((User)event).openPrivateChannel().complete();
				channel.sendMessage(messageData).complete();
			}
			return null;
		}
		else {
			throw new UnsupportedOperationException("Cannot reply to a " + event.getClass().getCanonicalName());
		}
	}
	
	public InteractionHook completeMessage(String message) {
		message = trim(message);
		return completeMessage(new MessageCreateBuilder().setContent(message).build(), false, false);
	}
	
	public InteractionHook completeMessage(String message, boolean ephemeral, boolean silent) {
		message = trim(message);
		return completeMessage(new MessageCreateBuilder().setContent(message).build(), ephemeral, silent);
	}
	
	public void editMessage(MessageEditData messageEdit) {
		if(event instanceof IMessageEditCallback) {
			((IMessageEditCallback) event).editMessage(messageEdit);
		}
		else {
			completeMessage(MessageCreateData.fromEditData(messageEdit));
		}
	}
	
	public void editMessage(String message) {
		editMessage(new MessageEditBuilder().closeFiles().clear().setContent(message).build());
	}
	
	public void editMessage(String message, boolean clear) {
		if(!clear) {
			editMessage(new MessageEditBuilder().setContent(message).build());
		}
		else {
			editMessage(message);
		}
	}
	
	public void replyInsufficientPermissions() {
		queueMessage("You do not have permission to execute that command.");
	}
	
	public void replyDiscordOnlyCommand() {
		queueMessage("This command can only be executed in Discord.");
	}
	
	public void sendThrowable(Throwable t) {
		IReplyCallback callback = (IReplyCallback) event;
		if(!callback.isAcknowledged()) {
			callback.deferReply().queue();;
		}
		callback.getHook().editOriginal(t.toString()).queue();
	}
	
	public EmbedBuilder constructEmbedResponse(String command) {
		return constructEmbedResponse(command, null);
	}
	
	public EmbedBuilder constructEmbedResponse(String command, String title) {
		User user = getAuthor();
		embedBuilder = new EmbedBuilder();
		embedBuilder.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
		embedBuilder.setTimestamp(Instant.now());
		return embedBuilder;
	}
	
	public MessageChannel getChannel() {
		if(event instanceof Interaction) {
			return ((Interaction) event).getMessageChannel();
		}
		else if (event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getChannel();
		}
		return null;
	}
	
	public EmbedBuilder getEmbed() {
		return embedBuilder;
	}
	
	public Guild getServer() {
		if(event instanceof Interaction) {
			return ((Interaction) event).getGuild();
		}
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getGuild();
		}
		return null;
	}
	
	public boolean isConsoleMessage() {
		return event instanceof ConsoleUser;
	}

	private String trim(String s) {
		if(s.length() > 2000) {
			s = s.substring(0, 2000);
		}
		return s;
	}
	
}

