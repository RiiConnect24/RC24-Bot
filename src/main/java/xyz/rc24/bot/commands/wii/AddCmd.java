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

package xyz.rc24.bot.commands.wii;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.mojang.brigadier.CommandDispatcher;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.commands.CommandContext;
import xyz.rc24.bot.commands.Commands;
import xyz.rc24.bot.commands.argument.DiscordUserArgumentType;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.user.ConsoleUser;
import xyz.rc24.bot.utils.FormatUtil;
import xyz.rc24.bot.utils.SearcherUtil;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Artuto
 */

public class AddCmd {


    public AddCmd(Bot bot)
    {
        this.core = bot.getCore();
        this.name = "add";
        this.help = "Sends your friend code to another user.";
        this.category = Categories.WII;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.USER, "user", "The user you want to add."));
        this.options = data;
    }
    
    private static void register(CommandDispatcher<CommandContext> dispatcher) {
    	dispatcher.register(Commands.global("add")
    		.then(Commands.argument("friend", new DiscordUserArgumentType())
    			.executes((context) -> {
    				execute(context.getSource(), context.getArgument("friend", User.class));
    				return 1;
    			})	
    		)
    	);
    }

    private static void execute(CommandContext context, User friend) {
            Member member = event.getOption("user").getAsMember();
            if(member == null)
                return;

            GuildSettings gs = getClient().getSettingsFor(event.getGuild());
            CodeType defaultAddType = gs.getDefaultAddType();

            if(friend instanceof ConsoleUser || friend.isSystem()) { 
            	context.queueMessage("what the fuck", true, false);
            	return;
            }
            if(friend.equals(context.getAuthor())) {
                context.queueMessage("You can't add yourself!", true, false);
                return;
            }
            if(member.getUser().isBot()) {
                context.queueMessage("You can't add bots!", true, false);
                return;
            }

            Map<String, String> authorTypeCodes = core.getCodesForType(defaultAddType, event.getGuild() == null ? null : event.getGuild().getSelfMember().getIdLong());
            if(authorTypeCodes.isEmpty())
            {
                event.reply("**" + event.getMember().getEffectiveName() + "** has not added any friend codes!").setEphemeral(true).queue();
                return;
            }

            Map<String, String> targetTypeCodes = core.getCodesForType(defaultAddType, member.getUser().getIdLong());
            if(targetTypeCodes.isEmpty())
            {
                event.reply("**" + member.getEffectiveName() + "** has not added any friend codes!").setEphemeral(true).queue();
                return;
            }

            // Send target's code to author
            event.reply(getAddMessageHeader(defaultAddType, event.getMember(),
                    false) + "\n\n" + FormatUtil.getCodeLayout(authorTypeCodes))
                    .queue(null, (failure) -> event.reply("Hey, " + member.getAsMention() +
                    ": I couldn't DM you. Make sure your DMs are enabled.").setEphemeral(true));

            // Send author's code to target
            member.getUser().openPrivateChannel().queue(pc ->
                    pc.sendMessage(getAddMessageHeader(defaultAddType, event.getMember(),
                            false) + "\n\n" + FormatUtil.getCodeLayout(authorTypeCodes))
                            .queue(null, (failure) -> event.reply("Hey, " + member.getAsMention() +
                            ": I couldn't DM you. Make sure your DMs are enabled.").setEphemeral(true)));
    }

    private String getAddMessageHeader(CodeType type, Member member, boolean isCommandRunner)
    {
        if(!(isCommandRunner))
            return "**" + member.getEffectiveName() + "** has requested to add your " + type.getDisplayName() + " friend code(s)!";
        else
            return "You have requested to add **" + member.getEffectiveName() + "**'s " + type.getDisplayName() + " friend code(s).";
    }
}
