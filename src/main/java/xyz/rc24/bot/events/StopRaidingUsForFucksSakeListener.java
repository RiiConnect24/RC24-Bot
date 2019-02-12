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

package xyz.rc24.bot.events;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Seriously, stop.
 *
 * @author Artuto
 */

public class StopRaidingUsForFucksSakeListener extends ListenerAdapter
{
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if(!(guild.getIdLong() == 206934458954153984L))
        {
            // Nothing to see here guys
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        long creation = member.getUser().getCreationTime().until(now, ChronoUnit.SECONDS);

        if(creation > 1800)
            return;

        try
        {
            guild.getController().ban(member, 1, "[AutoBan]")
                    .reason("[AutoBan]").queue(null, e -> {});
        }
        catch(Exception ignored) {}
    }
}
