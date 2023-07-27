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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Command;

import java.io.IOException;

public class RiiTagCommand implements Command {

    private static final String URL = "https://tag.rc24.xyz/%s/tag.max.png?randomizer=%f";

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        Member member = event.getOption("user").getAsMember();
        Request request = new Request.Builder().url(String.format(URL, member.getId(), 0D)).build();
        Bot bot = RiiConnect24Bot.getInstance();

        bot.getHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                event.reply("The RiiTag server did not respond.").setEphemeral(true).queue();
            }

            @Override
            public void onResponse(Call call, Response response) {

                if (response.code() == 404) {
                    event.reply(member.getAsMention() + " does not have a RiiTag!").setEphemeral(true).queue();
                    response.close();
                    return;
                }

                if (!(response.isSuccessful())) {
                    onFailure(call, new IOException("Server error: HTTP Code " + response.code()));
                    response.close();
                    return;
                }

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setAuthor(member.getEffectiveName() + "'s RiiTag", null, member.getEffectiveAvatarUrl())
                        .setColor(event.getGuild().getSelfMember().getColor())
                        .setImage(String.format(URL, member.getId(), Math.random()));

                event.replyEmbeds(embedBuilder.build()).queue();
                response.close();
            }
        });

    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("riitag", "Gets the user's Riitag").addOption(OptionType.USER, "user", "User", true);
    }

}
