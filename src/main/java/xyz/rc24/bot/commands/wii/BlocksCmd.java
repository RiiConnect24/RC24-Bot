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

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.Permission;
import xyz.rc24.bot.commands.Categories;

import java.util.ArrayList;
import java.util.List;

public class BlocksCmd extends SlashCommand
{
    public BlocksCmd()
    {
        this.name = "blocks";
        this.help = "Convert between Nintendo blocks and MBs.";
        this.category = Categories.WII;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "num", "The number of blocks."));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        if(event.getOption("num").getAsString().isEmpty())
        {
            event.reply("\u2139 1 block is 128kb\n" +
                    "8 blocks are 1MB").queue();
            return;
        }

        double blocks = parseNumber(event.getOption("num").getAsString());
        if(blocks < 1)
        {
            event.reply("Invalid number!").setEphemeral(true).queue();
            return;
        }

        double mb = blocks * 128 / 1024;
        event.reply("\u2139 " + blocks + " block(s) are " + mb + "MB(s)").queue();
    }

    private double parseNumber(String args)
    {
        try {return Double.parseDouble(args);}
        catch(NumberFormatException e) {return -1.0;}
    }
}
