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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import xyz.rc24.bot.commands.Categories;

/**
 * @author Artuto
 */

public class PingCmd extends SlashCommand
{
    public PingCmd() {
        this.name = "ping";
        this.help = "Ping the bot";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        // Has to be simpler due to interaction weirdness
        slashCommandEvent.reply("Pong!").setEphemeral(true).queue();
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        // Get the timestamp of the ping message
        long time = commandEvent.getMessage().getTimeCreated().toInstant().toEpochMilli();
        // Send a "Checking ping" message and calculate the difference between this message and the %^ping message
        commandEvent.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("Checking ping..").build()).queue((msg) -> {
            EmbedBuilder eb = new EmbedBuilder().setDescription(
                "Ping is " + (msg.getTimeCreated().toInstant().toEpochMilli() - time) + "ms\n" +
                "Gateway Ping is " + commandEvent.getJDA().getGatewayPing() + "ms\n"
            );
            msg.editMessageEmbeds(eb.build()).queue();
        });
    }
}
