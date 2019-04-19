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

package xyz.rc24.bot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Member;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.database.BirthdayDataManager;
import xyz.rc24.bot.utils.SearcherUtil;

/**
 * @author Artuto
 */

public class BirthdayCmd extends Command
{
    private BirthdayDataManager dataManager;

    public BirthdayCmd()
    {
        this.dataManager = RiiConnect24Bot.getInstance().getBirthdayDataManager();
        this.name = "birthday";
        this.help = "View your birthday or someone else's.";
        this.category = Categories.GENERAL;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Member target = SearcherUtil.findMember(event, event.getArgs());
        if(target == null)
            return;

        String date = dataManager.getBirthday(target.getUser().getIdLong());

        if(date == null)
        {
            if(target.equals(event.getMember()))
            {
                event.replyError("You don't have set your birthday!" +
                        " Set it using  `" + event.getClient().getPrefix() + "setbirthday`!");
            }
            else
                event.replyError("**" + target.getEffectiveName() + "** does not have a birthday set!");
        }

        if(target.equals(event.getMember()))
            event.reply("Your birthday is set to **" + date + "**");
        else
            event.reply("**" + target.getEffectiveName() + "**'s birthday is set to **" + date + "**");
    }
}
