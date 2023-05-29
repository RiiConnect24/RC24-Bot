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

import java.time.Duration;
import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.Dispatcher;

public class PingCmd 
{
		
	public static void register(Dispatcher dispatcher) {
		dispatcher.register(Commands.base("ping", "Ping the bot", null).botRequires(Permission.MESSAGE_EMBED_LINKS)
			.executes((context) -> {
				EmbedBuilder builder = context.getSource().getEmbed().setDescription("Checking ping...");
				MessageEmbed embed = builder.build();
				MessageCreateData data = MessageCreateData.fromEmbeds(embed);
				Instant before = Instant.now();
				InteractionHook hook = context.getSource().completeMessage(data);
				Instant after = Instant.now();
				try {
					builder.setDescription("Ping is `" + Duration.between(before, after).toMillis() + "ms`\n\n" +
							"Gateway ping is `" + context.getSource().getJDA().getGatewayPing() + "ms`\n\n");
				}
				catch (ArithmeticException e) {
					builder.setDescription("Ping is *very high*!");
				}
				hook.editOriginalEmbeds(builder.build()).queue();
				return 1;
			})
		);
	}

}
