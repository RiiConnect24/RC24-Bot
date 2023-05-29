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

import com.mojang.brigadier.arguments.DoubleArgumentType;

import net.dv8tion.jda.api.Permission;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.Dispatcher;
import xyz.rc24.bot.commands.RiiContext;

public class BlocksCmd
{
    
    public static final void register(Dispatcher dispatcher) {
    	dispatcher.register(Commands.base("blocks").botRequires(Permission.MESSAGE_SEND, Permission.MESSAGE_EMBED_LINKS)
    		.executes((context) -> {
    			convert(context.getSource(), 0d);
    			return 1;
    		})
    		.then(Commands.argument("amount", DoubleArgumentType.doubleArg())
    			.executes((context) -> {
    				convert(context.getSource(), context.getArgument("amount", Double.class));
    				return 1;
    			})	
    		)	
    	);
    }

    private static final void convert(RiiContext context, Double blocks) {
        if(blocks == 0d){
            context.queueMessage("\u2139 1 block is 128kb\n8 blocks is 1MB");
            return;
        }

        double mb = blocks * 128d / 1024d;
        context.queueMessage("\u2139 " + blocks + " block(s) is " + mb + "MB(s)");
    }

}
