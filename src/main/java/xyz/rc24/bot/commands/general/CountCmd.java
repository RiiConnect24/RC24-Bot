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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.brigadier.CommandDispatcher;

import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.CommandContext;
import xyz.rc24.bot.commands.Commands;

import java.io.IOException;

/**
 * @author Larsenv
 */

public class CountCmd {

    private static final Logger logger = RiiConnect24Bot.getLogger(CountCmd.class);
    
    public static void register(CommandDispatcher<CommandContext> dispatcher) {
    	dispatcher.register(Commands.global("count")
    		.executes((context) -> {
    			execute(context.getSource());
    			return 1;
    		})
    	);
    }

   private static void execute(CommandContext context) {
        String url = "https://miicontestp.wii.rc24.xyz/cgi-bin/count.cgi";

        if (RiiConnect24Bot.getInstance().getConfig().isDebug())
            logger.info("Sending request to '{}'", url);

        Request request = new Request.Builder().url(url).build();
        RiiConnect24Bot.getInstance().getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                context.queueMessage("Hm, something went wrong on our end.", true, false);
                logger.error("Something went wrong whilst checking the RiiConnect24 count stats: {}", e.getMessage(),
                        e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (!(response.isSuccessful())) {
                    onFailure(call, new IOException("Not success response code: " + response.code()));
                    response.close();
                    return;
                }

                try {
                    context.queueMessage(response.body().string());
                } catch (Exception e) {
                    context.queueMessage("Hm, something went wrong on our end.", true, false);
                }

                response.close();
            }
        });
    }
}
