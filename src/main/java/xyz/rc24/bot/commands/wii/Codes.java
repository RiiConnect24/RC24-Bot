package xyz.rc24.bot.commands.wii;

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

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.utils.FinderUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.mangers.CodeManager;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Manages wii for the user, stored on Redis.
 *
 * @author Spotlight
 */

public class Codes extends Command {

    private final CodeManager manager;

    public Codes(CodeManager manager) {
        this.manager = manager;
        this.name = "code";
        this.help = "Manages wii for the user.";
        this.children = new Command[]{new Add(), new Remove(), new Edit(), new Lookup(), new Help()};
        this.category = Categories.WII;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replyError("Please enter a valid option for the command.\n" +
                "Valid options: `add`, `edit`, `remove`, `lookup`, `help`.");
    }

    private class Lookup extends Command {
        Lookup() {
            this.name = "lookup";
            this.help = "Displays wii for the user.";
            this.category = new Command.Category("Wii-related");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
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
            // If there wasn't a user found, just halt execution.
            if (member == null) {
                event.replyError("I couldn't find that user!");
                return;
            }
            EmbedBuilder codeEmbed = new EmbedBuilder();
            // Set a default color for non-role usage.
            Color embedColor = Color.decode("#0083e2");
            if (!(event.getChannelType() == ChannelType.PRIVATE)) {
                embedColor = member.getColor();
            }
            codeEmbed.setColor(embedColor);
            codeEmbed.setAuthor("Profile for " + member.getEffectiveName(),
                    null, member.getUser().getEffectiveAvatarUrl());

            // Map: Type, then a further map of name/value.
            Map<CodeManager.Type, Map<String, String>> userCodes = manager.getAllCodes(member.getUser().getIdLong());

            for (Map.Entry<CodeManager.Type, Map<String, String>> typeData : userCodes.entrySet()) {
                // We define each code as the name (key) and the code (value) itself.
                Map<String, String> codes = typeData.getValue();
                // Make sure it's not null or empty and such
                if (codes != null && !codes.isEmpty()) {
                    StringBuilder fieldContents = new StringBuilder();
                    for (Map.Entry<String, String> codeData : codes.entrySet()) {
                        // Add in the format `nameOfCode`:
                        //                   value
                        fieldContents.append("`").append(codeData.getKey()).append("`:\n")
                                .append(codeData.getValue()).append("\n");
                    }
                    // Now that we have the code text set up, we'll just add it as a field.
                    codeEmbed.addField(Const.typesToReadableName.get(typeData.getKey()), fieldContents.toString(), true);
                }
                // I guess all wii for it were deleted if we got here.
                // Carry on!
            }
            // There won't be any fields if the types are all empty.
            if (codeEmbed.getFields().isEmpty()) {
                event.replyError("**" + member.getEffectiveName() + "** has not added any codes!");
            } else {
                event.reply(codeEmbed.build());
            }
        }
    }

    private class Add extends Command {
        Add() {
            this.name = "add";
            this.help = "Adds wii for the user.";
            this.category = new Command.Category("Wii-related");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        }

        @Override
        protected void execute(CommandEvent event) {
            String type = event.getArgs().split(" ")[0];
            if (!Const.namesToType.containsKey(type)) {
                event.replyError(Const.getCodeTypes());
                return;
            }

            // Begin the parsing.
            String toAdd = event.getArgs().substring(type.length());
            // For example, we might have | Name | code
            // We need to determine the name and code.
            String[] information = toAdd.split(" \\| ");
            String errorMessage = "Hm, I couldn't parse that.\nThe correct format is " +
                    "`" + event.getClient().getPrefix() + "code add " + type + " | name | code`.";
            // The array should have 3 (empty, name, and code).
            if (information.length != 3) {
                event.replyError(errorMessage);
                return;
            }
            if (information[1].isEmpty() || information[2].isEmpty()) {
                event.replyError(errorMessage);
                return;
            }
            manager.addCode(event.getAuthor().getIdLong(), Const.namesToType.get(type), information[1], information[2]);
            event.replySuccess("Added a code for `" + information[1] + "`");
        }
    }

    private class Remove extends Command {
        Remove() {
            this.name = "remove";
            this.help = "Removes wii for the user.";
            this.category = new Command.Category("Wii-related");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        }

        @Override
        protected void execute(CommandEvent event) {
            String type = event.getArgs().split(" ")[0];
            if (!Const.namesToType.containsKey(type)) {
                event.replyError(Const.getCodeTypes());
                return;
            }
            // Begin the parsing.
            String toAdd = event.getArgs().substring(type.length());
            // For example, we might have | Name | code
            // We need to determine the name and code.
            String[] information = toAdd.split(" \\| ");
            String errorMessage = "Hm, I couldn't parse that.\nThe correct format is " +
                    "`" + event.getClient().getPrefix() + "code remove " + type + " | code name`.";
            // The array should have 2 (empty, name).
            if (information.length != 2) {
                event.replyError(errorMessage);
                return;
            }

            if (information[1].isEmpty()) {
                event.replyError(errorMessage);
                return;
            }
            Boolean status = manager.removeCode(event.getAuthor().getIdLong(), Const.namesToType.get(type), information[1]);
            if (status) {
                event.replySuccess("Removed the code for `" + information[1] + "`");
            } else {
                event.replyError("A code for `" + information[1] + "` is not registered.");
            }
        }
    }

    private class Edit extends Command {
        Edit() {
            this.name = "edit";
            this.help = "Edits wii for the user.";
            this.category = new Command.Category("Wii-related");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        }

        @Override
        protected void execute(CommandEvent event) {
            String type = event.getArgs().split(" ")[0];
            if (!Const.namesToType.containsKey(type)) {
                event.replyError(Const.getCodeTypes());
                return;
            }
            // Begin the parsing.
            String toAdd = event.getArgs().substring(type.length());
            // For example, we might have | Name | code
            // We need to determine the name and code.
            String[] information = toAdd.split(" \\| ");
            String errorMessage = "Hm, I couldn't parse that.\nThe correct format is " +
                    "`" + event.getClient().getPrefix() + "code edit " + type + " | code name | new code`.";
            // The array should have 3 (empty, name, and code).
            if (information.length != 3) {
                event.replyError(errorMessage);
                return;
            }

            if (information[1].isEmpty() || information[2].isEmpty()) {
                event.replyError(errorMessage);
                return;
            }
            Boolean status = manager.editCode(event.getAuthor().getIdLong(), Const.namesToType.get(type), information[1], information[2]);
            if (status) {
                event.replySuccess("Edited the code for `" + information[1] + "`");
            } else {
                event.replyError("A code for `" + information[1] + "` is not registered.");
            }
        }
    }

    private class Help extends Command {
        Help() {
            this.name = "help";
            this.help = "Shows help regarding wii.";
            this.category = new Command.Category("Wii-related");
            this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        }

        @Override
        protected void execute(CommandEvent event) {
            String help = "**__Using the bot__**\n\n" +
                    "**Adding wii:**\n" +
                    "`" + event.getClient().getPrefix() + "code add wii | Wii Name Goes here | 1234-5678-9012-3456`\n" +
                    "`" + event.getClient().getPrefix() + "code add game | Game Name | 1234-5678-9012`\n" +
                    "and many more types! Run `" + event.getClient().getPrefix() + "code add` " +
                    "to see all supported code types right now, such as the 3DS and Switch.\n\n" +
                    "**Editing wii**\n" +
                    "`" + event.getClient().getPrefix() + "code edit wii | Wii Name | 1234-5678-9012-3456`\n" +
                    "`" + event.getClient().getPrefix() + "code edit game | Game Name | 1234-5678-9012`\n" +
                    "\n" +
                    "**Removing wii**\n" +
                    "`" + event.getClient().getPrefix() + "code remove wii | Wii Name`\n" +
                    "`" + event.getClient().getPrefix() + "code remove game | Game Name`\n" +
                    "\n" +
                    "**Looking up wii**\n" +
                    "`" + event.getClient().getPrefix() + "code lookup @user`\n" +
                    "\n" +
                    "**Adding a user's Wii**\n" +
                    "`" + event.getClient().getPrefix() + "add @user`\n" +
                    "This will send you their wii, and then DM them your Wii/game wii.";
            event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(help).queue(
                    (success) -> event.reactSuccess(),
                    (failure) -> event.replyError("I couldn't DM you!")
            ));
        }
    }
}
