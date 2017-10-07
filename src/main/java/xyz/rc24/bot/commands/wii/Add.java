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

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import redis.clients.jedis.JedisPool;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.mangers.CodeManager;

import java.util.List;
import java.util.Map;

/**
 * Allows another user to share friend wii.
 * @author Spotlight
 */

public class Add extends Command {
    private final CodeManager manager;
    public Add(JedisPool pool) {
        this.manager = new CodeManager(pool);
        this.name = "add";
        this.help = "Sends your friend wii to another user.";
        this.category = Categories.WII;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        Member member;
        if (event.getArgs().isEmpty()) {
            member = event.getMember();
        } else {
            List<Member> potentialMembers = FinderUtil.findMembers(event.getArgs(), event.getGuild());
            if (potentialMembers.isEmpty()) {
                event.replyError("I couldn't find a user by that name!");
                return;
            } else {
                member = potentialMembers.get(0);
            }
        }

        // Get wii for the user running the command
        Map<CodeManager.Type, Map<String, String>> userCodes = manager.getAllCodes(event.getMember().getUser().getIdLong());
        // If it's empty/null, (something) will return an empty map.
        Map<String, String> authorWiiCodes = userCodes.get(CodeManager.Type.WII);
        if (authorWiiCodes.isEmpty()) {
            event.replyError("**" + member.getEffectiveName() + "** has not added any Wii friend codes!");
            return;
        }

        Map<CodeManager.Type, Map<String, String>> memberCodes = manager.getAllCodes(member.getUser().getIdLong());
        Map<String, String> memberWiiCodes = memberCodes.get(CodeManager.Type.WII);
        if (memberWiiCodes.isEmpty()) {
            event.replyError("**" + member.getEffectiveName() + "** has not added any Wii friend codes!");
            return;
        }

        event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(getAddMessage(memberWiiCodes, member, false)).queue(
                (success) -> event.reactSuccess(),
                (failure) -> event.replyError("Hey, " + event.getMember().getAsMention() + ": I couldn't DM you. Make sure your DMs are enabled.")
        ));

        member.getUser().openPrivateChannel().queue(pc -> pc.sendMessage(getAddMessage(authorWiiCodes, event.getMember(), true)).queue(
                (success) -> event.reactSuccess(),
                (failure) -> event.replyError("Hey, " + member.getAsMention() + ": I couldn't DM you. Make sure your DMs are enabled.")
        ));
    }

    private String getAddMessage(Map<String, String> theirCodes, Member member, Boolean other) {
        StringBuilder addMessage = new StringBuilder();

        // Create a human-readable format of the user's Wii wii.
        StringBuilder theirCodesButString = new StringBuilder();
        for (Map.Entry<String, String> code : theirCodes.entrySet()) {
            theirCodesButString.append("`").append(code.getKey()).append("`:\n")
                    .append(code.getValue()).append("\n");
        }

        if (other) {
            addMessage.append("**").append(member.getEffectiveName()).append("** has requested to add your Wii's friend code!\n");
        } else {
            addMessage.append("You have requested to add **").append(member.getEffectiveName()).append("**'s Wii.\n");
        }
        addMessage.append(Const.typesToReadableName.get(CodeManager.Type.WII)).append(":\n");
        addMessage.append(theirCodesButString.toString());
        return addMessage.toString();
    }

}
