package xyz.rc24.bot.managers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
public class LogManager {
    /**
     * Redis for configuration use.
     */
    private JedisPool pool;

    public LogManager() {
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
}
