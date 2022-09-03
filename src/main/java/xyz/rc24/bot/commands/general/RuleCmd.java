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

package xyz.rc24.bot.commands.general;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import xyz.rc24.bot.commands.Categories;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Larsenv
 */

public class RuleCmd extends SlashCommand
{
    public RuleCmd()
    {
        this.name = "rule";
        this.help = "Looks up a rule in the RiiConnect24 Discord server.";
        this.category = Categories.WII;
        this.guildOnly = true;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "num", "The rule number to look up."));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        int ruleNum = Integer.parseInt(event.getOption("num").getAsString());

        if (ruleList.containsKey(ruleNum)) {
            event.reply("**Rule " + event.getOption("num").getAsString() + "**: " + ruleList.get(ruleNum)).queue();
        } else {
            event.reply("Rule not found.").setEphemeral(true).queue();
        }
    }

    private final Map<Integer, String> ruleList = new HashMap<>()
    {{
        put(1, "Staff members will use common sense when acting in an official capacity and may apply sanctions as they see fit, including in cases not explicitly covered by these rules - just be excellent to each other.");
        put(2, "Don’t harass or bother people, this includes repeated @mentions, trolling, and in Direct Messages. Don’t attack users on this server - examples of behavior that we consider to be a violation of this include racism, sexism, ableism, religious discrimination, homophobia/transphobia, or anything someone may find to be offensive - be nice! If someone is harassing you, please tell a staff member and they will take appropriate action.");
        put(3, "Try to avoid extended off-topic discussion - move to a more appropriate channel when convenient and keep spam to random. Also, we ask that you keep memes to the memes channel to avoid getting in the way of other discussions.");
        put(4, "Don’t impersonate others, even if they are a banned member and/or are not on this server except in cases where in which it is an obvious parody (and people actually find it funny!)");
        put(5, "Avoid bothering developers for information about release dates or progress on projects; though asking casually in conversation is usually okay, harassing developers isn’t. It will take as long as it takes!");
        put(6, "“Alt” accounts are permitted, but we ask that you notify staff, who will assign the appropriate role.");
        put(7, "Do not bother staff about if there are any open staff positions. Members may be nominated to be a part of the RiiConnect24 Staff Team if and when necessary and deserved. Doing so will reduce your chances, so don’t.");
        put(8, "NSFW content of any kind is strictly prohibited.");
        put(9, "Don't talk (too much) about politics or religion, as that usually results in fights.");
        put(10, "Advertising or posting any server invite links is allowed only in the self-promotion channel.");
        put(11, "Do not post messages with malicious intent, for example: messages that deliberately trigger bugs/crashes.");
        put(12, "Do not use usernames or nicknames that are untypable (e.g. usernames which are blank or consist of only emojis), we may forcefully change your nickname and/or issue sanctions.");
        put(13, "Sharing copyrighted content or links to such content is expressly prohibited - examples include pirated/copyrighted software and SDK material.");
        put(14, "Ensure that you follow the Discord Terms of Service (https://discordapp.com/terms) when participating in our community as these rules apply to Discord as a whole; in particular, we draw your attention to the prohibiting of using “selfbots” which can lead to suspension or even termination of your Discord account.");
        put(15, "Shitposting is OK to an extent, spam is not. Drama isn't tolerated either.");
        put(34, "( ͡° ͜ʖ ͡°)");
        put(69, "( ͡° ͜ʖ ͡°)");
        put(420, "( ͡° ͜ʖ ͡°)");
    }};
}
