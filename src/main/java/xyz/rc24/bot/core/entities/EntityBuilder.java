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

package xyz.rc24.bot.core.entities;

import co.aikar.idb.DbRow;
import com.google.gson.Gson;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.core.entities.impl.GuildSettingsImpl;

import java.util.Collections;
import java.util.Set;

/**
 * Builder for common entities
 *
 * @author Artuto
 */

public class EntityBuilder
{
    private final Gson gson = new Gson();

    @SuppressWarnings("unchecked")
    public GuildSettings buildGuildSettings(DbRow row)
    {
        CodeType defAdd = CodeType.fromId(row.getInt("default_add", 5));
        String prefixesRaw = row.getString("prefixes");

        if(prefixesRaw == null)
        {
            String defPrefix = RiiConnect24Bot.getInstance().getConfig().getPrefix();
            prefixesRaw = gson.toJson(new String[]{defPrefix});
        }

        return new GuildSettingsImpl(defAdd,
                row.getLong("birthdays_id", 0L),
                row.getLong("guild_id", 0L),
                row.getLong("modlog_id", 0L),
                row.getLong("serverlog_id", 0L),
                gson.fromJson(prefixesRaw, Set.class));
    }

    public GuildSettings buildDefaultGuildSettings(long id)
    {
        return new GuildSettingsImpl(CodeType.WII, 0L, id, 0L, 0L, Collections.emptySet());
    }
}
