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

import xyz.rc24.bot.managers.CodeManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Constants for the bot
 *
 * @author Artuto, Spotlight
 */

public class Const
{
    public static final String VERSION = Const.class.getPackage().getImplementationVersion();
    public static final String PATCHING_URL = "http://mtw.rc24.xyz/patch";
    public static final String SUCCESS_E = "✅";
    public static final String WARN_E = "⚠";
    public static final String ERROR_E = "❌";
	
    public static final Map<CodeManager.Type, String> typesToDisplayName = new HashMap<CodeManager.Type, String>()
    {{
        put(CodeManager.Type.WII, "<:Wii:259081748007223296> **Wii**");
        put(CodeManager.Type.THREE_DS, "<:New3DSXL:287651327763283968> **3DS**");
        put(CodeManager.Type.NNID, "<:NintendoNetworkID:287655797104836608> **Nintendo Network ID**");
        put(CodeManager.Type.SWITCH, "<:Switch:287652338791874560> **Switch**");
        put(CodeManager.Type.GAME, "🎮 **Games**");
    }};

    public static final Map<CodeManager.Type, String> typesToProductName = new HashMap<CodeManager.Type, String>()
    {{
        put(CodeManager.Type.WII, "Wii");
        put(CodeManager.Type.THREE_DS, "3DS");
        put(CodeManager.Type.NNID, "Nintendo Network ID");
        put(CodeManager.Type.SWITCH, "Switch");
        put(CodeManager.Type.GAME, "Game");
    }};
}
