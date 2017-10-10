/*
 * The MIT License
 *
 * Copyright 2017 Artu, Spotlight.
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

package xyz.rc24.bot;

import xyz.rc24.bot.mangers.CodeManager;
import xyz.rc24.bot.mangers.LogManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Constants for the bot
 * @author Artu, Spotlight
 */

public class Const {
    public static String VERSION = ("1.0.0");
    public static final String DONE_E = ("✅");
    public static final String WARN_E = ("⚠");
    public static final String FAIL_E = ("❌");
    public static final String GAME_0 = ("Loading...");
    public static final Map<CodeManager.Type, String> typesToReadableName = new HashMap<CodeManager.Type, String>() {{
        put(CodeManager.Type.WII, "<:Wii:259081748007223296> **Wii**");
        put(CodeManager.Type.THREE_DS, "<:New3DSXL:287651327763283968> **3DS**");
        put(CodeManager.Type.NNID, "<:NintendoNetworkID:287655797104836608> **Nintendo Network ID**");
        put(CodeManager.Type.SWITCH, "<:Switch:287652338791874560> **Switch**");
        put(CodeManager.Type.GAME, "🎮 **Games**");
    }};

    public static final Map<String, CodeManager.Type> namesToType = new HashMap<String, CodeManager.Type>() {{
        put("wii", CodeManager.Type.WII);
        put("3ds", CodeManager.Type.THREE_DS);
        put("nnid", CodeManager.Type.NNID);
        put("switch", CodeManager.Type.SWITCH);
        put("game", CodeManager.Type.GAME);
    }};

    public static final Map<String, String> badgeTypes = new HashMap<String, String>() {{
        put("owner", "<:BadgeBotDev:331597705472114688>");
        put("dev", "<:BadgeDeveloper:338399284376633367>");
        put("adm", "<:BadgeAdmin:338398740727726081>");
        put("mod", "<:BadgeModerator:329715070768513024>");
        put("hlp", "<:BadgeHelper:338399338739007488>");
        put("don", "<:BadgeDonator:329712167983251458>");
        put("trn", "<:BadgeTranslator:329723303814234113>");
    }};

    public static final Map<String, LogManager.LogType> channelTypes = new HashMap<String, LogManager.LogType>() {{
        put("mod", LogManager.LogType.MOD);
        put("mod-log", LogManager.LogType.MOD);

        put("srv", LogManager.LogType.SERVER);
        put("server", LogManager.LogType.SERVER);
        put("server-log", LogManager.LogType.SERVER);
    }};

    public static final Map<String, String> channelTypeHelp = new HashMap<String, String>() {{
        put("mod", "Moderation log. Shows bans/unbans.");
        put("srv", "Server log. Shows bans/unbans, along with joins and leaves.");
    }};

    // oops, a function in a constant file
    public static String getCodeTypes() {
        StringBuilder response = new StringBuilder("Invalid type! Valid types:\n");
        for (String type : namesToType.keySet()) {
            response.append("`").append(type).append("`, ");
        }
        // Remove leftover comma + space
        return response.substring(0, response.length() - 2);
    }

    public static String getChannelTypes() {
        StringBuilder response = new StringBuilder("Invalid type! Valid types:\n");
        for (Map.Entry<String, String> type : channelTypeHelp.entrySet()) {
            // `type`: Definition\n
            response.append("`").append(type.getKey())
                    .append("`: ")
                    .append(type.getValue()).append("\n");
        }
        return response.toString();
    }
}
