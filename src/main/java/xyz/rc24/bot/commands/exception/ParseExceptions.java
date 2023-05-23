package xyz.rc24.bot.commands.exception;

import java.util.Arrays;
import java.util.Collection;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.dv8tion.jda.api.entities.User;

public interface ParseExceptions {
	public Dynamic2CommandExceptionType NOT_VALID = new Dynamic2CommandExceptionType((type, value) -> new LiteralMessage(type + "`" + value + "` is not valid for this command."));
	public Dynamic3CommandExceptionType NOT_VALID_BECAUSE = new Dynamic3CommandExceptionType((type, value, reason) -> new LiteralMessage(type + "`" + value + "` is not valid for this command because " + reason));
	public Dynamic2CommandExceptionType NONEXISTANT = new Dynamic2CommandExceptionType((type, value) -> new LiteralMessage(type + " `" + value + "` does not exist or could not be found."));
	
	public DynamicCommandExceptionType INVALID_CHOICE = new DynamicCommandExceptionType((value) -> new LiteralMessage("Invalid choice, must choose from one of the following: `" + Arrays.toString(((Collection<?>)value).toArray()) + "`"));
	
	public DynamicCommandExceptionType DISCORD_NOT_FOUND = new DynamicCommandExceptionType(input -> new LiteralMessage("Could not find a discord user using the input `" + input + "`"));
	public Dynamic2CommandExceptionType DISCORD_AMBIGUITY = new Dynamic2CommandExceptionType((input, found) -> new LiteralMessage("`" + input + "` matches " + ((User[])found).length + " users, supply a discriminator or specify an ID"));
}
