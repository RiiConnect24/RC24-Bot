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

import com.mojang.brigadier.CommandDispatcher;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.commands.CommandContext;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.core.entities.Poll;
import xyz.rc24.bot.core.entities.impl.MiitomoPoll;
import xyz.rc24.bot.managers.PollManager;

public class ReviveCmd 
{

    public static void register(CommandDispatcher<CommandContext> dispatcher) {
    	dispatcher.register(Commands.global("revive")
    		.executes(context -> {
    			execute(context.getSource());
    			return 1;
    		})
    	);
    }
    
    //It appears the poll manager does not exist?
    private static void execute(CommandContext context) {
    	
    	if(context.isDiscordContext()) {
	        // Get a random poll
	        Poll poll = context.getBot().getPollManager().getRandomPoll();
	
	        // Now we need to build the embed
	        EmbedBuilder embed = context.getEmbed();
	        {
	            embed.setTitle("<:EverybodyVotesChannel:317090360449040388> " + poll.getQuestion());
	
	            if(!(poll instanceof MiitomoPoll))
	            {
	                embed.setDescription("\uD83C\uDD70 " + poll.getResponse1() + "\n" +
	                        "_ _\n" + // Line separator
	                        "\uD83C\uDD71 " + poll.getResponse2());
	                embed.setFooter("This question was from the " + poll.getCountryFlag() + " EVC", null);
	            }
	            else
	                embed.setFooter("This question was from Miitomo");
	
	            // setColor(event.getSelfMember().getColor());
	        };
	
	        // Send embed to chat
	        context.queueMessage(MessageCreateData.fromEmbeds(embed.build()));
    	}
    	else {
    		context.replyDiscordOnlyCommand();
    	}
    }
}
