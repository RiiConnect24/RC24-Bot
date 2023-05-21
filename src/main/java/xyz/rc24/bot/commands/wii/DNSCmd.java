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

package xyz.rc24.bot.commands.wii;

import net.dv8tion.jda.api.Permission;
import xyz.rc24.bot.commands.CommandContext;
import xyz.rc24.bot.commands.Commands;

import com.mojang.brigadier.CommandDispatcher;

/**
 * @author Gamebuster
 */

public class DNSCmd {
	
	private static final String PRIMARY_DNS = "167.86.108.126";
	private static final String SECONDARY_DNS = "1.1.1.1";
    
    public static final void register(CommandDispatcher<CommandContext> dispatcher) {
    	dispatcher.register(Commands.global("dns")
    		.executes((context) -> {
    			sendDNSInfo(context.getSource());
    			return 1;
    		})	
    	);
    }

    private static final void sendDNSInfo(CommandContext context) {
        context.queueMessage("`" + PRIMARY_DNS + "` should be your primary DNS.\n`" + SECONDARY_DNS + "` should be your secondary DNS.", context.hasPermission(Permission.MESSAGE_SEND), false);
    }
}
