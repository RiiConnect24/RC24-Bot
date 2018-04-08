/*
 * The MIT License
 *
 * Copyright 2017 Spotlight, Artu, Seriel.
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

package xyz.rc24.bot.commands.wii;

/*
 * The MIT License
 *
 * Copyright 2017 RiiConnect24 and its contributors.
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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import redis.clients.jedis.JedisPool;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.managers.CodeManager;
import xyz.rc24.bot.managers.ServerConfigManager;

import java.util.List;
import java.util.Map;

/**
 * Allows another user to share friend codes.
 *
 * @author Spotlight
 */

public class Add extends Command
{
    private final CodeManager manager;
    private final ServerConfigManager configManager;

    public Add(JedisPool pool)
    {
        this.manager = new CodeManager(pool);
        this.configManager = new ServerConfigManager();
        this.name = "add";
        this.help = "Sends your friend wii to another user.";
        this.category = Categories.WII;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Member member;
        if(event.getArgs().isEmpty())
            member = event.getMember();
        else
        {
            List<Member> potentialMembers = FinderUtil.findMembers(event.getArgs(), event.getGuild());
            if(potentialMembers.isEmpty())
            {
                event.replyError("I couldn't find a user by that name!");
                return;
            }
            else
                member = potentialMembers.get(0);
        }

        CodeManager.Type serverAddType = configManager.getDefaultAddType(event.getGuild().getIdLong());

        // Get wii for the user running the command
        Map<CodeManager.Type, Map<String, String>> authorCodes = manager.getAllCodes(event.getMember().getUser().getIdLong());
        // If it's empty/null, (something) will return an empty map.
        Map<String, String> authorTypeCodes = authorCodes.get(serverAddType);
        if(authorTypeCodes.isEmpty())
        {
            event.replyError("**" + member.getEffectiveName() + "** has not added any friend codes!");
            return;
        }

        Map<CodeManager.Type, Map<String, String>> memberCodes = manager.getAllCodes(member.getUser().getIdLong());
        Map<String, String> memberTypeCodes = memberCodes.get(serverAddType);
        if(memberTypeCodes.isEmpty())
        {
            event.replyError("**" + member.getEffectiveName() + "** has not added any friend codes!");
            return;
        }

        event.replyInDm(getAddMessageHeader(serverAddType, member, true) + "\n\n" + getCodeLayout(memberTypeCodes),
                (success) -> event.reactSuccess(),
                (failure) -> event.replyError("Hey, " + event.getAuthor().getAsMention() + ": I couldn't DM you. Make sure your DMs are enabled."));

        member.getUser().openPrivateChannel().queue(pc -> pc.sendMessage(
                getAddMessageHeader(serverAddType, event.getMember(), false) + "\n\n" + getCodeLayout(authorTypeCodes)
        ).queue((success) -> event.reactSuccess(),
                (failure) -> event.replyError("Hey, " + member.getAsMention() + ": I couldn't DM you. Make sure your DMs are enabled.")));
    }

    private String getAddMessageHeader(CodeManager.Type type, Member member, Boolean isCommandRunner)
    {
        if(isCommandRunner)
            return "**" + member.getEffectiveName() + "** has requested to add your " + Const.typesToProductName.get(type) + " friend code(s)!";
        else
            return "You have requested to add **" + member.getEffectiveName() + "**'s " + Const.typesToProductName.get(type) + " friend code(s).";
    }

    private String getCodeLayout(Map<String, String> theirCodes)
    {
        // Create a human-readable format of the user's Wii wii.
        StringBuilder theirCodesButString = new StringBuilder();
        for (Map.Entry<String, String> code : theirCodes.entrySet())
            theirCodesButString.append("`").append(code.getKey()).append("`:\n").append(code.getValue()).append("\n");

        return theirCodesButString.toString();
    }
}
