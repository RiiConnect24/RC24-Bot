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

package xyz.rc24.bot.database;

import co.aikar.idb.DbRow;
import com.google.gson.Gson;
import xyz.rc24.bot.core.entities.CodeType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Artuto
 */

public class CodeDataManager
{
    private final Database db;
    private final Gson gson = new Gson();

    public CodeDataManager(Database db)
    {
        this.db = db;
    }

    @SuppressWarnings("unchecked")
    public Map<CodeType, Map<String, String>> getAllCodes(long user)
    {
        Optional<DbRow> optRow = db.getRow("SELECT * FROM codes WHERE user_id = ?", user);
        if(!(optRow.isPresent()))
            return Collections.emptyMap();

        DbRow row = optRow.get();

        Map<String, String> games = gson.fromJson(row.getString("games", ""), Map.class);
        Map<String, String> nnid = gson.fromJson(row.getString("nnid", ""), Map.class);
        Map<String, String> nswitch = gson.fromJson(row.getString("switch", ""), Map.class);
        Map<String, String> psn = gson.fromJson(row.getString("psn", ""), Map.class);
        Map<String, String> threeds = gson.fromJson(row.getString("threeds", ""), Map.class);
        Map<String, String> wii = gson.fromJson(row.getString("wii", ""), Map.class);

        Map<CodeType, Map<String, String>> map = new HashMap<>();
        map.put(CodeType.GAME, games);
        map.put(CodeType.NNID, nnid);
        map.put(CodeType.PSN, psn);
        map.put(CodeType.SWITCH, nswitch);
        map.put(CodeType.THREEDS, threeds);
        map.put(CodeType.WII, wii);

        return map;
    }
}
