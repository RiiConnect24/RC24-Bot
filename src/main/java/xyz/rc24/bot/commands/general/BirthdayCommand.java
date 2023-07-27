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

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Command;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author Artuto, Gamebuster
 */

public class BirthdayCommand implements Command {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Logger logger = LoggerFactory.getLogger(BirthdayCommand.class);

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {

        switch (event.getSubcommandName()) {

            case "set" -> {

                String date = event.getOption("date").getAsString();
                MonthDay dateTime;

                try {
                    // HACK -- Using 1970 because Java needs a year to parse a LocalDate
                    // However, since we only need the month and day, we can ignore it this way.
                    dateTime = MonthDay.from(LocalDate.parse(date + "/1970", formatter));
                } catch (DateTimeParseException e) {
                    event.reply("Could not parse the date! Please use following format `dd/MM`").setEphemeral(true).queue();
                    logger.error("Could not format date '{}'", date, e);
                    return;
                }

                long authorId = event.getUser().getIdLong();
                boolean success = RiiConnect24Bot.getInstance().getBirthdayDataManager().setBirthday(authorId, dateTime.getDayOfMonth() + "/" + dateTime.getMonthValue());
                event.reply(success ? "Set birthday successfully" : "There was an error updating your birthday! Please contact a developer.").setEphemeral(true).queue();
            }

            case "get" -> {

                User user = event.getOption("user").getAsUser();
                String date = RiiConnect24Bot.getInstance().getBirthdayDataManager().getBirthday(user.getIdLong());

                if (date == null) {
                    event.reply("This user hasn't set their birthday!").setEphemeral(true).queue();
                    return;
                }

                event.reply("<a:birthdaycake:576200303662071808> " + user.getAsMention() + "s birthday is on **" + date + "**").queue();
            }

            default -> event.reply("Please select a subcommand!").setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("birthday", "View the birthday of yourself or someone else")
                .addSubcommands(new SubcommandData("set", "Set your birthday")
                        .addOption(OptionType.STRING, "date", "Date of your birthday (DD/MM)", true))
                .addSubcommands(new SubcommandData("get", "Get the birthday of a user")
                        .addOption(OptionType.USER, "user", "User you want to see the birthday of", true));
    }
}
