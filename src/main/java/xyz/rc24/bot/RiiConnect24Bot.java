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

package xyz.rc24.bot;

import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

/**
 * Bot entry point.
 *
 * @author Artuto
 */

public class RiiConnect24Bot {

    public static final String VERSION = RiiConnect24Bot.class.getPackage().getImplementationVersion() == null ? "DEV" : RiiConnect24Bot.class.getPackage().getImplementationVersion();
    public static final String PATCHING_URL = "http://mtw.rc24.xyz/patch";
    public static final String SUCCESS_E = "✅";
    public static final String WARN_E = "⚠";
    public static final String ERROR_E = "❌";
    public static final EnumSet<GatewayIntent> INTENTS = EnumSet.of(GUILD_MESSAGES, GUILD_MESSAGE_REACTIONS, DIRECT_MESSAGES, DIRECT_MESSAGE_REACTIONS);

    private static Bot instance;
    private static final Logger logger = LoggerFactory.getLogger("RiiConnect24 Bot");

    public static void main(String[] args) {
        // Sentry
        System.setProperty("sentry.stacktrace.app.packages", "xyz.rc24.bot");
        System.setProperty("sentry.release", VERSION);
        logger.info("Starting RiiConnect24 Bot - {}", VERSION);
        instance = new Bot();
        instance.run();
    }

    public static Bot getInstance() {
        if (instance == null) throw new IllegalStateException("The bot is not initialized!");
        return instance;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
