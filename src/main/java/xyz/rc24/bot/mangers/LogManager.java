package xyz.rc24.bot.mangers;

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
     * Checks if a channel is enabled.
     *
     * @param type     Type of log to look for
     * @param serverID Server ID to check with
     * @return Boolean of state
     */
    public Boolean isLogEnabled(LogType type, Long serverID) {
        try (Jedis conn = pool.getResource()) {
            String logID = conn.hget(serverID + "", type.toString());
            return logID == null || logID.isEmpty();
        }
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
            return Long.decode(conn.hget(serverID + "", type.toString()));
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
