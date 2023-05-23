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

import com.mojang.brigadier.CommandDispatcher;

import net.dv8tion.jda.api.Permission;

import xyz.rc24.bot.commands.CommandContext;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.argument.CodeTypeArgumentType;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.database.GuildSettingsDataManager;

/**
 * @author Artuto
 */

public class DefaultAddCmd {
    private GuildSettingsDataManager dataManager;
    
    public static void register(CommandDispatcher<CommandContext> dispatcher) {
    	dispatcher.register(Commands.literal("defaultAdd")
    		.then(Commands.argument("type", CodeTypeArgumentType.KNOWN_CODES)
    			.executes((context) -> {
    				execute(context.getSource(), context.getArgument("type", CodeType.class));
    				return 1;
    			})
    		)
    	);
    }

    private static void execute(CommandContext context, CodeType type) {
    	
    	if(!context.hasPermission(Permission.MANAGE_SERVER)) {
    		context.replyInsufficientPermissions();
    		return;
    	}
    	if(!context.isDiscordContext()) {
    		context.replyDiscordOnlyCommand();
    		return;
    	}
    	
        if(context.getBot().getGuildSettingsDataManager().setDefaultAddType(type, context.getServer().getIdLong())) {
            context.queueMessage("Successfully set `" + type.getName() + "` as default `add` type!");
        }
        else {
            context.queueMessage("Error whilst updating the default add type! Please contact a developer.", true, false);
        }

    }
}
