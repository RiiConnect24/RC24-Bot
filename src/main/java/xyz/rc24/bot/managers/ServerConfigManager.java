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

package xyz.rc24.bot.managers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 *
 * @author Artuto
 */

@Deprecated
public class ServerConfigManager
{
    /**
     * Redis for configuration use.
     */
    private JedisPool pool;

    public ServerConfigManager(JedisPool pool)
    {
        this.pool = pool;
    }

    /**
     * Gets the ID of the channel by type.
     *
     * @param serverId Server ID to look up with
     * @param type     Type of log to look for
     * @return Long with ID of server-log
     */
    public long getLog(LogType type, long serverId)
    {
        try(Jedis conn = pool.getResource())
        {
            String logID = conn.hget(serverId + "", type.toString());
            if(logID == null || logID.isEmpty()) return 0L;
            else return Long.parseLong(logID);
        }
    }

    /**
     * "Disables" a log type for a server.
     *
     * @param type     Type of log to associate
     * @param serverID Server ID to associate with
     */
    public void disableLog(LogType type, Long serverID)
    {
        try(Jedis conn = pool.getResource())
        {
            conn.hdel(serverID + "", type.toString());
        }
    }

    /**
     * Gets the default `add` command type for a server
     *
     * @param serverID Server ID to associate with
     * @return Type of code to default `add` command with
     */
    public CodeManager.Type getDefaultAddType(Long serverID)
    {
        try(Jedis conn = pool.getResource())
        {
            try
            {
                return CodeManager.Type.valueOf(conn.hget(serverID + "", "addType"));
            }
            catch(NullPointerException unused)
            {
                // Default to Wii
                return CodeManager.Type.WII;
            }
        }
    }

    public enum LogType
    {
        MOD, SERVER
    }
}
