/*
 * The MIT License
 *
 * Copyright 2017 Artu.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package xyz.rc24.bot.commands.tools;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import xyz.rc24.bot.utils.FormatUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Artu
 */

public class UserInfo extends Command {
    public UserInfo() {
        this.name = "userinfo";
        this.help = "Shows info about the specified user";
        this.category = new Command.Category("Specific commands");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String ranks = null;
        String roles = null;
        String emote = null;
        String status = null;
        EmbedBuilder builder = new EmbedBuilder();
        Member member = null;

        if (event.getArgs().isEmpty()) {
            member = event.getMessage().getMember();
        } else {
            List<Member> list = FinderUtil.findMembers(event.getArgs(), event.getGuild());

            if (list.isEmpty()) {
                event.replyWarning("I was not able to found a user with the provided arguments: '" + event.getArgs() + "'");
                return;
            } else if (list.size() > 1) {
                event.replyWarning(FormatUtil.listOfMembers(list, event.getArgs()));
                return;
            } else {
                member = list.get(0);
            }
        }

        StringBuilder rolesbldr = new StringBuilder();
        member.getRoles().forEach(r -> rolesbldr.append(" ").append(r.getAsMention()));

        String title = (member.getUser().isBot() ? ":information_source: Information about the bot **" + member.getUser().getName() + "**" + "#" + "**" + member.getUser().getDiscriminator() + "** <:bot:334859813915983872>" : ":information_source: Information about the user **" + member.getUser().getName() + "**" + "#" + "**" + member.getUser().getDiscriminator() + "**");

        if (rolesbldr.toString().isEmpty()) {
            roles = "**None**";
        } else {
            roles = rolesbldr.toString();
        }

        try {
            builder.addField("ID: ", "**" + member.getUser().getId() + "**", false);
            builder.addField("Nickname: ", (member.getNickname() == null ? "None" : "**" + member.getNickname() + "**"), false);
            builder.addField("Roles: ", roles, false);
            builder.addField(" Status: ", member.getOnlineStatus() + (member.getGame() == null ? "" : " ("
                    + (member.getGame().getType() == Game.GameType.TWITCH ? "On Live at [*" + member.getGame().getName() + "*]"
                    : "Playing **" + member.getGame().getName() + "**") + ")" + ""), false);
            builder.addField("Account Creation Date: ", "**" + member.getUser().getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "**", false);
            builder.addField("Guild Join Date: ", "**" + member.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "**", false);
            builder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
            builder.setColor(member.getColor());
            event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();
        } catch (Exception e) {
        }
    }
}
