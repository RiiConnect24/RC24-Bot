package xyz.rc24.bot.mangers;


import com.google.cloud.datastore.*;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
public class LogManager {
    /**
     * Redis for configuration use.
     */
    private Datastore datastore;
    private KeyFactory keyFactory;

    public LogManager(Datastore datastore) {
        this.datastore = datastore;
        keyFactory = datastore.newKeyFactory().setKind("logs");
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
        Key taskKey = keyFactory.newKey(serverID);
        Entity retrieved = datastore.get(taskKey);
        try {
            return retrieved.isNull(type.name().toLowerCase());
        } catch (NullPointerException e) {
            return false;
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
        Key taskKey = keyFactory.newKey(serverID);
        Entity retrieved = datastore.get(taskKey);
        // We consider a log being null "disabled".
        try {
            return retrieved.getLong(type.name().toLowerCase());
        } catch (DatastoreException e) {
            return 0L;
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
        Key taskKey = keyFactory.newKey(serverID);
        Entity.Builder toStore = Entity.newBuilder(taskKey);
        toStore.set(type.name().toLowerCase(), channelID);
        datastore.put(toStore.build());
    }

    /**
     * "Disables" a log type for a server.
     *
     * @param type     Type of log to associate
     * @param serverID Server ID to associate with
     */
    public void disableLog(LogType type, Long serverID) {
        Key taskKey = keyFactory.newKey(serverID);
        Entity.Builder toStore = Entity.newBuilder(taskKey);
        // We consider a log being null "disabled".
        toStore.remove(type.name().toLowerCase());
        datastore.put(toStore.build());
    }
}
