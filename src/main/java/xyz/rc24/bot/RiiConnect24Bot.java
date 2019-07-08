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

package xyz.rc24.bot;

import ch.qos.logback.classic.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.requests.RestAction;
import org.slf4j.LoggerFactory;

/**
 * Bot entry point.
 *
 * @author Artuto
 */

public class RiiConnect24Bot
{
    private static Bot instance;

    private static final Logger logger = (Logger) LoggerFactory.getLogger("RiiConnect24 Bot");

    public static void main(String[] args) throws LoginException
    {
		// Sentry
        System.setProperty("sentry.stacktrace.app.packages", "xyz.rc24.bot");
        System.setProperty("sentry.release", Const.VERSION);
		
		// JDA
        RestAction.setPassContext(true); // enable context by default
        RestAction.DEFAULT_FAILURE = Throwable::printStackTrace;

        getLogger().info("Starting RiiConnect24 Bot - {}", Const.VERSION);

        new Bot().run();
    }

    public static Bot getInstance()
    {
        if(instance == null)
            throw new IllegalStateException("The bot is not initialized!");

        return instance;
    }

    public static Logger getLogger()
    {
        return logger;
    }

    public static Logger getLogger(Class clazz)
    {
        return (Logger) LoggerFactory.getLogger(clazz);
    }

    static void setInstance(Bot instance)
    {
        RiiConnect24Bot.instance = instance;
    }
}
