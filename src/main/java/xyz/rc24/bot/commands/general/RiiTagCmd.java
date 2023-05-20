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
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import xyz.rc24.bot.commands.CommandContext;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.argument.DiscordUserArgumentType;

import java.io.IOException;

public class RiiTagCmd {
    private static final String URL = "https://tag.rc24.xyz/%s/tag.max.png?randomizer=%f";

    public static void register(CommandDispatcher<CommandContext> dispatcher) {
    	dispatcher.register(Commands.global("riitag")
    		.then(Commands.argument("user", new DiscordUserArgumentType())
    			.executes(context -> {
    				execute(context.getSource(), context.getArgument("user", User.class));
    				return 1;
    			})	
    		)	
    	);
    }

    private static void execute(CommandContext context, User user) {

        Request request = new Request.Builder().url(String.format(URL, user.getId(), 0D)).build();
        context.getBot().getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                context.queueMessage("The RiiTag server did not respond.", true, false);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() == 404) {
                    context.queueMessage("**" + user.getAsTag() + "** does not have a RiiTag!", true, false);
                    response.close();
                    return;
                }

                if (!(response.isSuccessful())) {
                    onFailure(call, new IOException("Server error: HTTP Code " + response.code()));
                    response.close();
                    return;
                }

                displayTag(context, user);
                response.close();
            }
        });
    }

    private static void displayTag(CommandContext context, User user) {
        EmbedBuilder embedBuilder = context.getEmbed()
                .setAuthor(user.getAsTag() + "'s RiiTag", null, user.getEffectiveAvatarUrl())
                .setColor(context.getServer() == null ? null : context.getServer().getSelfMember().getColor())
                .setImage(String.format(URL, user.getId(), Math.random()));

        context.queueMessage(MessageCreateData.fromEmbeds(embedBuilder.build()));
    }
}
