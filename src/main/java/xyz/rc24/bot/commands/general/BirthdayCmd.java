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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.utils.SearcherUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Artuto
 */

public class BirthdayCmd extends SlashCommand
{
    private final Bot bot;

    public BirthdayCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "birthday";
        this.help = "View the birthday of you or someone else.";
        this.category = Categories.GENERAL;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.USER, "user", "The user to look up for the birthday.").setRequired(true));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
            Member target;

            try {
                target = event.getOption("user").getAsMember();
            } catch (Exception e) {
                target = event.getMember();
            }

            String date = bot.getBirthdayDataManager().getBirthday(target.getUser().getIdLong());

            if(date == null)
            {
                if(target.equals(event.getMember()))
                {
                    event.reply("You haven't have set your birthday!" +
                            " Set it using  `/setbirthday`!").setEphemeral(true).queue();
                }
                else
                    event.reply("This user hasn't set their birthday!").setEphemeral(true).queue();

                return;
            }

            if(target.equals(event.getMember()))
                event.reply("<a:birthdaycake:576200303662071808> Your birthday is set to **" + date + "** (date format: DD/MM)").queue();
            else
                event.reply("<a:birthdaycake:576200303662071808> **" + target.getEffectiveName() + "**'s birthday is set to **" + date + "** (date format: DD/MM)").queue();
    }
}
