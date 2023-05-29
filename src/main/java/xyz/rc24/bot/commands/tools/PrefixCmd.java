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

package xyz.rc24.bot.commands.tools;

import net.dv8tion.jda.api.Permission;

import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.Dispatcher;
import xyz.rc24.bot.commands.RiiContext;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.database.GuildSettingsDataManager;

@Deprecated(forRemoval = true)
public class PrefixCmd {
    
    private static void register(Dispatcher dispatcher) {
    	dispatcher.register(
    		Commands.base("prefix").requires((context) -> context.isGuildContext(), RiiContext.requiresServerContext)
    			.executes((context) -> { //NO ARGS
    			replyPrefix(context.getSource());
    				return 1;
    			})
	    		.then(Commands.anyString("prefix").requires(Permission.MANAGE_SERVER)
	    			.executes((context) -> {
	    				setPrefix(context.getSource(), context.getArgument("prefix", String.class));
	    				return 1;
	    			})
	    		)
	    		.then(Commands.suggestableString("none").requires(Permission.MANAGE_SERVER)
	    			.executes((context) -> {
	    				setPrefix(context.getSource(), null);
	    				return 1;
	    			})
	    		)
    	);
    }
    
    private static void replyPrefix(RiiContext context) {
    	GuildSettings gs = context.getBot().getGuildSettingsDataManager().getSettings(context.getServer());
    	context.queueMessage("ℹ The prefix in this server is: " + gs.getPrefix());
    }
    
    private static void setPrefix(RiiContext context, String prefix) {
    	
		if(prefix != null && prefix.length() > 5) {
			context.queueMessage("The prefix length may not be longer than 5 characters!");
		}
		else {
			GuildSettingsDataManager dataManager = context.getBot().getGuildSettingsDataManager();
            if(dataManager.setPrefix(context.getServer().getIdLong(), null)) {
                context.queueMessage("Successfully disabled the custom prefix!");
            }
            else {
                context.queueMessage("Error whilst disabling the custom prefix! Please contact a developer.");
            }
		}
    }

}
