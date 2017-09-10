package xyz.rc24.bot.commands.tools;

/*
 * The MIT License
 *
 * Copyright 2017 Artu.
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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.utils.SimpleLog;
import xyz.rc24.bot.utils.CodeManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Looks up errors using the Wiimmfi API.
 * @author Artu
 */

public class ErrorInfo extends Command {

    public ErrorInfo() {
        this.name = "error";
        this.help = "Looks up errors using the Wiimmfi API.";
        this.category = new Command.Category("Wii-related");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = false;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        SimpleLog.getLog("Errors").info(event.getArgs());
        Matcher channelCheck = Pattern.compile("(NEWS|FORE)0{4}\\d{2}").matcher(event.getArgs());
        // Check for Fore/News
        if (channelCheck.find()) {
            // First match will be the type, then second our actual code.
            Integer code;
            try {
                // Make sure the code's actually a code.
                Matcher codeCheck = Pattern.compile("0{4}\\d{2}").matcher(channelCheck.group());
                if (!codeCheck.find()) {
                    throw new NumberFormatException();
                }

                code = Integer.parseInt(codeCheck.group());
                if (channelErrors.get(code) == null) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                event.replyError("Could not find the specified app error code.");
                return;
            }
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Here's information about your error:");
            builder.setDescription(channelErrors.get(code));
            builder.setColor(Color.decode("#D32F2F"));
            builder.setFooter("All information provided by RC24 Developers.", null);
            event.reply(builder.build());
        } else {

        }
//        String method = "t="
    }

    private Map<Integer, String> channelErrors = new HashMap<Integer, String>() {{
        put(1, "Can't open the VFF");
        put(2, "WiiConnect24 file problem");
        put(3, "VFF file corrupted");
        put(4, "Unknown (it probably doesn't exist)");
        put(5, "VFF processing error");
        put(6, "Invalid data");
        put(99, "Other error");
    }};
}
