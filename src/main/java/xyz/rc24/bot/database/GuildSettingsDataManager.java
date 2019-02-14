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
import xyz.rc24.bot.core.entities.EntityBuilder;
import xyz.rc24.bot.core.entities.GuildSettings;

import java.util.Optional;

/**
 * Data manager for Guild settings
 *
 * @author Artuto
 */

public class GuildSettingsDataManager
{
    private final EntityBuilder entityBuilder;
    private final Database db;

    public GuildSettingsDataManager(Database db, EntityBuilder entityBuilder)
    {
        this.db = db;
        this.entityBuilder = entityBuilder;
    }

    public GuildSettings getGuildSettings(long id)
    {
        Optional<DbRow> optRow = db.getRow("SELECT * FROM settings WHERE guild_id = ?", id);

        return optRow.map(entityBuilder::buildGuildSettings).orElse(entityBuilder.buildDefaultGuildSettings(id));
    }
}
