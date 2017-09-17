/*
 * Copyright (C) 2017 Artu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package xyz.rc24.bot.utils;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.List;

/**
 * @author Artu
 * <p>
 * The following code is property of jagrosh () with some changes made by me.
 * Contact me if any issue.
 */

public class FormatUtil {
    public static String listOfMembers(List<Member> list, String query) {
        StringBuilder out = new StringBuilder(" Multiple members found matching \"" + query + "\":");
        for (int i = 0; i < 6 && i < list.size(); i++)
            out.append("\n - ").append(list.get(i).getUser().getName()).append("#").append(list.get(i).getUser().getDiscriminator()).append(" (ID:").append(list.get(i).getUser().getId()).append(")");
        if (list.size() > 6)
            out.append("\n**And ").append(list.size() - 6).append(" more...**");
        return out.toString();
    }

    public static String listOfRoles(List<Role> list, String query) {
        StringBuilder out = new StringBuilder(" Multiple roles found matching \"" + query + "\":");
        for (int i = 0; i < 6 && i < list.size(); i++)
            out.append("\n - ").append(list.get(i).getName()).append(" (ID:").append(list.get(i).getId()).append(")");
        if (list.size() > 6)
            out.append("\n**And ").append(list.size() - 6).append(" more...**");
        return out.toString();
    }

    public static String listOfTcChannels(List<TextChannel> list, String query) {
        StringBuilder out = new StringBuilder(" Multiple roles found matching \"" + query + "\":");
        for (int i = 0; i < 6 && i < list.size(); i++)
            out.append("\n - ").append(list.get(i).getName()).append(" (ID:").append(list.get(i).getId()).append(")");
        if (list.size() > 6)
            out.append("\n**And ").append(list.size() - 6).append(" more...**");
        return out.toString();
    }

    public static String listOfVcChannels(List<VoiceChannel> list, String query) {
        StringBuilder out = new StringBuilder(" Multiple roles found matching \"" + query + "\":");
        for (int i = 0; i < 6 && i < list.size(); i++)
            out.append("\n - ").append(list.get(i).getName()).append(" (ID:").append(list.get(i).getId()).append(")");
        if (list.size() > 6)
            out.append("\n**And ").append(list.size() - 6).append(" more...**");
        return out.toString();
    }
}
