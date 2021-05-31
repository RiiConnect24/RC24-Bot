/*
 * MIT License
 *
 * Copyright (c) 2017-2021 RiiConnect24 and its contributors
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

package xyz.rc24.bot.listeners;

import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.rc24.bot.Config;
import xyz.rc24.bot.RiiConnect24Bot;

/**
 * Mirror messages from one server to another.
 *
 * @author Artuto
 */

public class Morpher extends ListenerAdapter
{
    // IDs
    private final long rootId;
    private final long ownerId;

    private final Logger logger = RiiConnect24Bot.getLogger(Morpher.class);

    public Morpher(Config config)
    {
        this.rootId = config.getMorpherRoot();
        this.ownerId = config.getPrimaryOwner();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        JDA jda = event.getJDA();

        if(event.getChannel().getIdLong() == rootId && canCrosspost(jda, event.getChannel()))
            event.getMessage().crosspost().queue();
    }

    private boolean canCrosspost(JDA jda, TextChannel origin)
    {
        String message = null;

        if(!(origin.getGuild().getSelfMember().hasPermission(origin, Permission.MESSAGE_MANAGE)))
            message = "I don't have `Manage Messages` permission in the root Morpher channel!";

        if(message == null && !(origin.canTalk()))
            message = "I couldn't access the Morpher mirror channel... could you please check it out?";

        if(message == null)
            return true;

        // Well, looks like we don't have permissions, or something.
        String finalMessage = message;
        jda.retrieveUserById(ownerId).flatMap(User::openPrivateChannel)
                .flatMap(pc -> pc.sendMessage(finalMessage))
                .queue(null, e -> logger.error("I can't access the Morpher mirror channel: {}", finalMessage));

        return false;
    }
}
