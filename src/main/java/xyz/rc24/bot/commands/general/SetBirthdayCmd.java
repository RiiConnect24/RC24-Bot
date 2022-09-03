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
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Artuto
 */

public class SetBirthdayCmd extends SlashCommand
{
    private Bot bot;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM[/yyyy]");

    public SetBirthdayCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "setbirthday";
        this.help = "Sets your birthday. Please note that this command uses the DD/MM date format.";
        this.category = Categories.GENERAL;
        this.guildOnly = false;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "date", "The birthday to set to in DD/MM format.").setRequired(true));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        long id = event.getMember().getIdLong();
        LocalDate dateTime = parseDate(event.getOption("date").getAsString());

        if(dateTime == null)
        {
            event.reply("I couldn't parse your date.\n" +
                    "Try something like: `" + bot.getPrefix(event.getGuild()) + "setbirthday 25/12` (date format: DD/MM).").setEphemeral(true).queue();
            return;
        }

        boolean success = bot.getBirthdayDataManager().setBirthday(id, dateTime.getDayOfMonth() + "/" + dateTime.getMonthValue());

        if(success)
            event.reply("Updated successfully!").setEphemeral(true).queue();
        else
            event.reply("There was an error updating your birthday! Please contact a developer.").setEphemeral(true).queue();
    }

    private LocalDate parseDate(String args)
    {
        try
        {
            return args.endsWith("/\\d{4}/") ? LocalDate.parse(args, formatter) : LocalDate.parse(args + "/2019", formatter);
        }
        catch(DateTimeParseException ignored) {}

        return null;
    }
}
