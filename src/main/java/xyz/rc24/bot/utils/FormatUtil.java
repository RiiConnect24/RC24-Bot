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

public class FormatUtil
{
    public static String listOfMembers(List<Member> list, String query)
    {
        StringBuilder out = new StringBuilder(" Multiple members found matching \"" + query + "\":");
        for(int i = 0; i < 6 && i < list.size(); i++)
            out.append("\n - ").append(list.get(i).getUser().getName()).append("#").append(list.get(i).getUser().getDiscriminator()).append(" (ID:").append(list.get(i).getUser().getId()).append(")");
        if(list.size() > 6) out.append("\n**And ").append(list.size() - 6).append(" more...**");
        return out.toString();
    }

    public static String listOfRoles(List<Role> list, String query)
    {
        StringBuilder out = new StringBuilder(" Multiple roles found matching \"" + query + "\":");
        for(int i = 0; i < 6 && i < list.size(); i++)
            out.append("\n - ").append(list.get(i).getName()).append(" (ID:").append(list.get(i).getId()).append(")");
        if(list.size() > 6) out.append("\n**And ").append(list.size() - 6).append(" more...**");
        return out.toString();
    }

    public static String listOfTcChannels(List<TextChannel> list, String query)
    {
        StringBuilder out = new StringBuilder(" Multiple roles found matching \"" + query + "\":");
        for(int i = 0; i < 6 && i < list.size(); i++)
            out.append("\n - ").append(list.get(i).getName()).append(" (ID:").append(list.get(i).getId()).append(")");
        if(list.size() > 6) out.append("\n**And ").append(list.size() - 6).append(" more...**");
        return out.toString();
    }

    public static String listOfVcChannels(List<VoiceChannel> list, String query)
    {
        StringBuilder out = new StringBuilder(" Multiple roles found matching \"" + query + "\":");
        for(int i = 0; i < 6 && i < list.size(); i++)
            out.append("\n - ").append(list.get(i).getName()).append(" (ID:").append(list.get(i).getId()).append(")");
        if(list.size() > 6) out.append("\n**And ").append(list.size() - 6).append(" more...**");
        return out.toString();
    }
}
