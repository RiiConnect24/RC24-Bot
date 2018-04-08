package xyz.rc24.bot.managers;

/*
 * The MIT License
 *
 * Copyright 2017 RiiConnect24 and its contributors.
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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
public class ServerConfigManager {
    /**
     * Redis for configuration use.
     */
    private JedisPool pool;

    public ServerConfigManager() {
        this.pool = new JedisPool(new JedisPoolConfig(), URI.create("redis://localhost:6379/1"));
    }

    public enum LogType {
        MOD,
        SERVER
    }

    /**
     * Gets the ID of the channel by type.
     *
     * @param serverID Server ID to look up with
     * @param type     Type of log to look for
     * @return Long with ID of server-log
     */
    public Long getLog(LogType type, Long serverID) {
        try (Jedis conn = pool.getResource()) {
            String logID = conn.hget(serverID + "", type.toString());
            if (logID == null || logID.isEmpty()) {
                return null;
            } else {
                return Long.parseLong(logID);
            }
        }
    }

    /**
     * Sets the ID of the channel by type.
     *
     * @param serverID  Server ID to associate with
     * @param type      Type of log to associate
     * @param channelID Channel ID to set
     */
    public void setLog(Long serverID, LogType type, Long channelID) {
        try (Jedis conn = pool.getResource()) {
            conn.hset(serverID + "", type.toString(), channelID.toString());
        }
    }

    /**
     * "Disables" a log type for a server.
     *
     * @param type     Type of log to associate
     * @param serverID Server ID to associate with
     */
    public void disableLog(LogType type, Long serverID) {
        try (Jedis conn = pool.getResource()) {
            conn.hdel(serverID + "", type.toString());
        }
    }

    /**
     * Sets the default `add` command type for a server
     *
     * @param serverID Server ID to look up with
     * @param setType Type to default `add` command to
     */
    public void setDefaultAddType(Long serverID, CodeManager.Type setType) {
        try (Jedis conn = pool.getResource()) {
            conn.hset(serverID + "", "addType", setType.toString());
        }
    }

    /**
     * Gets the default `add` command type for a server
     *
     * @param serverID Server ID to associate with
     * @return Type of code to default `add` command with
     */
    public CodeManager.Type getDefaultAddType(Long serverID) {
        try (Jedis conn = pool.getResource()) {
            try {
                return CodeManager.Type.valueOf(conn.hget(serverID + "", "addType"));
            } catch (NullPointerException unused) {
                // Default to Wii
                return CodeManager.Type.WII;
            }
        }
    }
}
