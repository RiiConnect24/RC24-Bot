/*
 * MIT License
 *
 * Copyright (c) 2017-2020 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.bot.commands.general;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.thegamecommunity.discord.command.argument.DiscordUserArgumentType;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.Dispatcher;
import xyz.rc24.bot.commands.RiiContext;

/**
 * @author Artuto, Gamebuster
 */

public class BirthdayCmd
{
	
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM[/yyyy]");
    
    @SuppressWarnings("unused")
	public static final void register(Dispatcher dispatcher) {
    	dispatcher.register(Commands.base("birthday", "View the birthday of yourself or someone else", null)
    		.then(Commands.suggestableString("set")
    			.then(Commands.anyString("DD/MM")
    				.executes((context) -> {
    					
    					return 1;
    				}
    			))
    		)
    		
    		.then(Commands.argument("user", new DiscordUserArgumentType())
    			.executes(context -> {
    				getBirthday(context.getSource(), context.getArgument("user", User.class));
    				return 1;
    			})
    		)	
    	);
    }
    
    private static void setBirthday(RiiContext context, String date) {
        long id = context.getUser().getIdLong();
        LocalDate dateTime = parseDate(date);

        if(dateTime == null)
        {
            context.queueMessage("I couldn't parse your date.\n" +
                    "Try something like: `setbirthday 25/12` (date format: DD/MM).", true, false);
            return;
        }

        boolean success = RiiConnect24Bot.getInstance().getBirthdayDataManager().setBirthday(id, dateTime.getDayOfMonth() + "/" + dateTime.getMonthValue());

        if(success) {
            context.queueMessage("Updated successfully!", true, false);
        }
        else {
            context.queueMessage("There was an error updating your birthday! Please contact a developer.", true, false);
        }
    }

    private static void getBirthday(RiiContext context, User user) {

            String date = RiiConnect24Bot.getInstance().getBirthdayDataManager().getBirthday(user.getIdLong());

            if(date == null)
            {
                if(user.equals(context.getUser()))
                {
                    context.queueMessage("You haven't have set your birthday!" +
                            " Set it using  `/birthday set <MM/DD>`!", true, false);
                }
                else
                    context.queueMessage("This user hasn't set their birthday!", true, false);

                return;
            }

            if(user.equals(context.getUser())) {
                context.queueMessage("<a:birthdaycake:576200303662071808> Your birthday is set to **" + date + "** (date format: DD/MM)");
            }
            else {
            	String effectiveName;
            	Guild guild = context.getServer();
            	if(guild == null) {
            		effectiveName = user.getName();
            	}
            	else {
            		effectiveName = guild.getMember(user).getEffectiveName();
            	}
                context.queueMessage("<a:birthdaycake:576200303662071808> **" + effectiveName + "**'s birthday is set to **" + date + "** (date format: DD/MM)");
            }
    }
    
    private static LocalDate parseDate(String args)
    {
        try
        {
            return args.endsWith("/\\d{4}/") ? LocalDate.parse(args, formatter) : LocalDate.parse(args + "/2019", formatter);
        }
        catch(DateTimeParseException ignored) {}

        return null;
    }
}
