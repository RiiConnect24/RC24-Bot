package xyz.rc24.bot.mangers;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
public class CodeManager {
    private Datastore datastore;
    private KeyFactory keyFactory;

    /**
     * Each int corresponds to the sorted level on Redis.
     */
    public enum Type {
        WII,
        THREE_DS,
        NNID,
        SWITCH,
        GAME
    }

    private String getKeyName(Long userID, Type codeType) {
        return userID.toString() + ":" + codeType.toString();
    }

    public CodeManager(Datastore datastore) {
        this.datastore = datastore;
        keyFactory = datastore.newKeyFactory().setKind("logs");
    }

    /**
     * Add a code for a user.
     *
     * @param userID   User ID to associate with the code
     * @param codeType Type to associate with the code
     * @param codeName Name of the code
     * @param code     Value of the code
     */
    public void addCode(Long userID, Type codeType, String codeName, String code) {
        Key taskKey = keyFactory.newKey(getKeyName(userID, codeType));
        Entity task = Entity.newBuilder(taskKey)
                .set(codeName, code)
                .build();
        datastore.put(task);
    }

    /**
     * Edits the code for a user.
     *
     * @param userID   User ID to associate with the code
     * @param codeType Type to associate with the code
     * @param codeName Name of the code
     * @param newCode  Value to edit
     * @return Boolean, true means success, false means code doesn't exist
     */
    public Boolean editCode(Long userID, Type codeType, String codeName, String newCode) {
        Key taskKey = keyFactory.newKey(getKeyName(userID, codeType));
        Entity existing = datastore.get(taskKey);
        if (existing.isNull(codeName)) {
            return false;
        } else {
            Entity task = Entity.newBuilder(taskKey)
                    .set(codeName, newCode)
                    .build();
            datastore.put(task);
            return true;
        }
    }

    public Boolean removeCode(Long userID, Type codeType, String codeName) {
        Key taskKey = keyFactory.newKey(getKeyName(userID, codeType));
        Entity existing = datastore.get(taskKey);
        // Make sure there's something to delete.
        if (existing.isNull(codeName)) {
            return false;
        } else {
            Entity task = Entity.newBuilder(taskKey)
                    .remove(codeName)
                    .build();
            datastore.put(task);
            return true;
        }
    }

    public Map<Type, Map<String, String>> getAllCodes(Long userID) {
        Map<Type, Map<String, String>> codes = new HashMap<>();
        for (Type codeType : Type.values()) {
            Key taskKey = keyFactory.newKey(getKeyName(userID, codeType));
            Entity existing = datastore.get(taskKey);
            Map<String, String> typeSpecificCodes = new HashMap<>();
            try {
                for (String name : existing.getNames()) {
                    // Get value associated with name.
                    typeSpecificCodes.put(name, existing.getString(name));
                }
            } catch (NullPointerException e) {
                // Guess there weren't any codes for that type.
                continue;
            }
            codes.put(codeType, typeSpecificCodes);
        }
        return codes;
    }
}
