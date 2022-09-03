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

import ch.qos.logback.classic.Logger;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Categories;

import java.io.IOException;

/**
 * @author Larsenv
 */

public class CountCmd extends SlashCommand
{
    private final boolean debug;
    private final OkHttpClient httpClient;

    private final Logger logger = RiiConnect24Bot.getLogger(CountCmd.class);

    public CountCmd(Bot bot)
    {
        this.debug = bot.getConfig().isDebug();
        this.httpClient = bot.getHttpClient();
        this.name = "count";
        this.help = "Looks up the number of Miis on the Check Mii Out Channel and Wiis registered to use Wii Mail.";
        this.category = Categories.WII;
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        String url = "https://miicontestp.wii.rc24.xyz/cgi-bin/count.cgi";
        
        if(debug)
            logger.info("Sending request to '{}'", url);

        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                event.reply("Hm, something went wrong on our end.").setEphemeral(true).queue();
                logger.error("Something went wrong whilst checking the RiiConnect24 count stats: {}", e.getMessage(), e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
            {
                if(!(response.isSuccessful()))
                {
                    onFailure(call, new IOException("Not success response code: " + response.code()));
                    response.close();
                    return;
                }

                try
                {
                    event.reply(response.body().string()).queue();
                } catch (Exception e) {
                    event.reply("Hm, something went wrong on our end.").setEphemeral(true).queue();
                }

                response.close();
            }
        });
    }
}
