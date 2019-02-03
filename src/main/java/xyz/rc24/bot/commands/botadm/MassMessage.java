/*
 * MIT License
 *
 * Copyright (c) 2017-2019 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.rc24.bot.commands.botadm;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import xyz.rc24.bot.commands.Categories;

/**
 * @author Spotlight
 */

public class MassMessage extends Command
{
    private JedisPool pool;

    public MassMessage(JedisPool pool)
    {
        this.pool = pool;
        this.name = "super_secret_server_message";
        this.help = "Sends a message to _every_ log on the bot. USE WITH CAUTION!";
        this.category = Categories.ADMIN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        try(Jedis conn = pool.getResource())
        {
            // TODO: update to using keys* on redis db 2
//            Map<String, String> logConfigs = conn.hgetAll("logs");
//            List<TextChannel> serverList = new ArrayList<>();
//            Gson gson = new Gson();
//
//            // For every channel we have:
//            // If we can speak, add it to the growing total.
//            for (String serverJson : logConfigs.values()) {
//                ServerConfigManager.StorageFormat format = gson.fromJson(serverJson, ServerConfigManager.StorageFormat.class);
//                if (format.serverLog != null) {
//                    TextChannel serverChannel = event.getJDA().getTextChannelById(format.serverLog);
//                    try {
//                        if (serverChannel.canTalk()) {
//                            serverList.add(serverChannel);
//                        }
//                    } catch (NullPointerException ignored) {
//
//                    }
//                }
//                if (format.modLog != null) {
//                    TextChannel modChannel = event.getJDA().getTextChannelById(format.modLog);
//                    try {
//                        if (modChannel.canTalk()) {
//                            serverList.add(modChannel);
//                        }
//                    } catch (NullPointerException ignored) {
//
//                    }
//                }
//            }
//
//            // Actually send
//            for (TextChannel logChannel : serverList) {
//                logChannel.sendMessage(event.getArgs()).complete();
//            }
        }
    }
}
