package xyz.rc24.bot.mangers;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
public class LogManager {
    /**
     * Redis for configuration use.
     */
    private JedisPool pool;
    private Gson gson;

    public LogManager(JedisPool pool) {
        this.pool = pool;
        this.gson = new Gson();
    }

    public enum LogType {
        MOD,
        SERVER
    }

    public class StorageFormat {
        @SerializedName("mod")
        public Long modLog;
        @SerializedName("server")
        public Long serverLog;
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
            String storedJSON = conn.hget("logs", "" + serverID);
            StorageFormat format;
            if (storedJSON == null || storedJSON.isEmpty()) {
                // I guess no config was created previously.
                format = new StorageFormat();
            } else {
                format = gson.fromJson(storedJSON, StorageFormat.class);
            }
            switch (type) {
                case MOD:
                    return !(format.modLog == null);
                case SERVER:
                    return !(format.serverLog == null);
                default:
                    // Other types we don't (yet) know of
                    return false;
            }
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
            String storedJSON = conn.hget("logs", "" + serverID);
            StorageFormat format;
            if (storedJSON == null || storedJSON.isEmpty()) {
                // I guess no config was created previously.
                format = new StorageFormat();
            } else {
                format = gson.fromJson(storedJSON, StorageFormat.class);
            }
            switch (type) {
                case MOD:
                    return format.modLog;
                case SERVER:
                    return format.serverLog;
                default:
                    // ????
                    return 0L;
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
            String storedJSON = conn.hget("logs", "" + serverID);
            StorageFormat format;
            if (storedJSON == null || storedJSON.isEmpty()) {
                // I guess no config was created previously.
                format = new StorageFormat();
            } else {
                format = gson.fromJson(storedJSON, StorageFormat.class);
            }
            switch (type) {
                case MOD:
                    format.modLog = channelID;
                    break;
                case SERVER:
                    format.serverLog = channelID;
                    break;
            }
            String JSONtoStore = gson.toJson(format);
            conn.hset("logs", "" + serverID, JSONtoStore);
        }
    }

    /**
     * "Disables" a log type for a server.
     *
     * @param type      Type of log to associate
     * @param serverID  Server ID to associate with
     */
    public void disableLog(LogType type, Long serverID) {
        try (Jedis conn = pool.getResource()) {
            String storedJSON = conn.hget("logs", "" + serverID);
            StorageFormat format;
            if (storedJSON == null || storedJSON.isEmpty()) {
                // I guess no config was created previously.
                format = new StorageFormat();
            } else {
                format = gson.fromJson(storedJSON, StorageFormat.class);
            }
            switch (type) {
                case MOD:
                    format.modLog = null;
                    break;
                case SERVER:
                    format.serverLog = null;
                    break;
            }
            String JSONtoStore = gson.toJson(format);
            conn.hset("logs", "" + serverID, JSONtoStore);
        }
    }
}
