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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Command;

import java.awt.*;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class StatsCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(StatsCommand.class);

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        Bot bot = RiiConnect24Bot.getInstance();

        Request request = new Request.Builder()
                .url("http://164.132.44.106/stats.json")
                .addHeader("User-Agent", "RC24-Bot " + RiiConnect24Bot.VERSION)
                .build();

        bot.getHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                event.reply("Could not contact the Stats API! Please ask a owner to check the console. Error: ```" + e.getMessage() + "```").setEphemeral(true).queue();
                logger.error("Exception while contacting the Stats API! ", e);
            }

            @Override
            public void onResponse(Call call, Response response) {

                try (response) {

                    if (!(response.isSuccessful())) {
                        event.reply("An error occurred: " + response.code()).setEphemeral(true).queue();
                        return;
                    }

                    if (response.body() == null) {
                        event.reply("An error occurred: Response body is null!").setEphemeral(true).queue();
                        return;
                    }

                    EmbedBuilder eb = new EmbedBuilder();
                    MessageCreateBuilder mb = new MessageCreateBuilder();
                    eb.setDescription(parseJSON(response));
                    eb.setColor(Color.decode("#29B7EB"));
                    mb.setContent("<:RC24:302470872201953280> Service stats of RC24:").setEmbeds(eb.build());
                    mb.addEmbeds(eb.build());
                    event.reply(mb.build()).queue();

                } catch (Exception e) {
                    onFailure(call, new IOException(e));
                    response.close();
                } finally {
                    response.close();
                }
            }
        });
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("stats", "Retrieve the RC24 Stats");
    }

    private static String parseJSON(Response response) {

        JSONObject json = new JSONObject(new JSONTokener(response.body().byteStream()));
        Set<String> keys = new TreeSet<>(json.keySet());
        StringBuilder green = new StringBuilder();
        StringBuilder yellow = new StringBuilder();
        StringBuilder red = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        keys.forEach(k -> {
            String status = json.getString(k);
            switch (status) {
                case "green" -> green.append("+ ").append(k).append("\n");
                case "yellow" -> yellow.append("* ").append(k).append("\n");
                default -> red.append("- ").append(k).append("\n");
            }
        });

        sb.append("Supported by RiiConnect24:\n")
                .append("```diff\n").append(green).append("```\nIn progress...\n")
                .append("```fix\n").append(yellow.toString().isEmpty() ? "None!" : yellow)
                .append("```\nNot supported:\n")
                .append("```diff\n").append(red.toString().isEmpty() ? "None!" : red).append("\n```");

        return sb.toString();
    }
}
