package xyz.rc24.bot.mangers;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

/**
 * Manages a single Redis instance, available across classes.
 * Now that's intuitive.™
 */
public class MorpherManager {
    private Datastore datastore;
    private KeyFactory keyFactory;
    private String keyName;

    public MorpherManager(String keyName, Datastore datastore) {
        this.datastore = datastore;
        this.keyFactory = datastore.newKeyFactory().setKind("morpher");
        this.keyName = keyName;
    }

    public void setAssociation(Long rootMessageID, Long mirroredMessageID) {
        Key taskKey = keyFactory.newKey(keyName);
        Entity entity = Entity.newBuilder(taskKey)
                .set("" + rootMessageID, mirroredMessageID)
                .build();
        datastore.put(entity);
    }

    public void removeAssociation(Long rootMessageID) {
        Key taskKey = keyFactory.newKey(keyName);
        Entity entity = datastore.get(taskKey);
        if (!entity.isNull(rootMessageID + "")) {
            Entity builder = Entity.newBuilder(taskKey)
                    .remove(rootMessageID + "")
                    .build();
            datastore.put(builder);
        }
    }

    public void deleteAllAssociations(Long serverID) {
        Key taskKey = keyFactory.newKey(keyName);
        datastore.delete(taskKey);
    }

    public Long getAssociation(Long rootMessageID) {
        Key taskKey = keyFactory.newKey(keyName);
        Entity entity = datastore.get(taskKey);
        return entity.getLong("" + rootMessageID);
    }
}
