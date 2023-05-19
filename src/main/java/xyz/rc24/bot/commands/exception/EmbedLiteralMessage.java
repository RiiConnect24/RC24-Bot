package xyz.rc24.bot.commands.exception;

import com.mojang.brigadier.LiteralMessage;

import net.dv8tion.jda.api.EmbedBuilder;

public class EmbedLiteralMessage extends LiteralMessage {

	private EmbedBuilder embed = new EmbedBuilder();
	
	public EmbedLiteralMessage(String string) {
		super(string);
	}
	
	@Override
	public String getString() {
		return null;
	}

}
