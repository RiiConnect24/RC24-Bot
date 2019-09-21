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

package xyz.rc24.bot.commands.botadm;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;

import javax.script.ScriptEngine;
import java.util.Arrays;
import java.util.List;

/**
 * @author Artu, Spotlight
 */

public class Eval extends Command
{
    private ScriptEngine engine;
    private List<String> imports;
    private Bot bot;

    public Eval(Bot bot)
    {
        this.bot = bot;
        this.name = "eval";
        this.help = "Executes Groovy code";
        this.category = Categories.ADMIN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
        this.guildOnly = false;

        engine = new GroovyScriptEngineFactory().getScriptEngine();
        imports = Arrays.asList("com.jagrosh.jdautilities",
                "com.jagrosh.jdautilities.commandclient",
                "com.jagrosh.jdautilities.commandclient.impl",
                "com.jagrosh.jdautilities.entities",
                "com.jagrosh.jdautilities.utils",
                "java.io", "java.lang", "java.util",
                "net.dv8tion.jda.bot",
                "net.dv8tion.jda.bot.entities",
                "net.dv8tion.jda.bot.entities.impl",
                "net.dv8tion.jda.api",
                "net.dv8tion.jda.api.entities",
                "net.dv8tion.jda.api.entities.impl",
                "net.dv8tion.jda.api.managers",
                "net.dv8tion.jda.api.managers.impl",
                "net.dv8tion.jda.api.utils",
                "net.dv8tion.jda.webhook",
                "xyz.rc24.bot", "xyz.rc24.bot.listeners", "xyz.rc24.bot.loader",
                "xyz.rc24.bot.managers", "xyz.rc24.bot.utils");
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String importString = "";
        String eval;

        try
        {
            engine.put("event", event);
            engine.put("jda", event.getJDA());
            engine.put("channel", event.getChannel());
            engine.put("message", event.getMessage());
            engine.put("bot", event.getSelfUser());
            engine.put("client", event.getClient());
            engine.put("author", event.getAuthor());
            engine.put("bot", bot);
            if(event.isFromType(ChannelType.TEXT))
            {
                engine.put("member", event.getMember());
                engine.put("guild", event.getGuild());
                engine.put("tc", event.getTextChannel());
                engine.put("selfmember", event.getGuild().getSelfMember());
            }

            for(final String s : imports)
                importString += "import " + s + ".*;";

            eval = event.getArgs().replaceAll("getToken", "getSelfUser");
            Object out = engine.eval(importString + eval);

            if(out == null || String.valueOf(out).isEmpty())
                event.reactSuccess();
            else
                event.replySuccess("Done! Output:\n```java\n" + out.toString().replaceAll(event.getJDA().getToken(), "Nice try.") + " ```");
        }
        catch(Exception e2)
        {
            event.replyError("Error! Output:\n```java\n" + e2 + " ```");
        }
    }
}