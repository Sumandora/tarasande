/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;

import java.util.HashMap;
import java.util.Map;

public class EntityTracker extends StoredObject {

    private final Map<Integer, Entity1_10Types.EntityType> entities = new HashMap<>();
    private final Map<Integer, Boolean> objectiveEntities = new HashMap<>();
    public int ownEntityId;

    public EntityTracker(UserConnection user) {
        super(user);
    }

    public void track(final int entityId, final Entity1_10Types.EntityType entityType, boolean isObject) {
        entities.putIfAbsent(entityId, entityType);
        objectiveEntities.putIfAbsent(entityId, isObject);
    }

    public Entity1_10Types.EntityType get(final int entityId) {
        if (entities.containsKey(entityId)) {
            return entities.get(entityId);
        }

        return null;
    }

    public boolean isObjective(final int entityId) {
        return objectiveEntities.containsKey(entityId);
    }

    public void remove(final int entityId) {
        if (entities.containsKey(entityId)) {
            entities.remove(entityId);
        }
        if (objectiveEntities.containsKey(entityId)) {
            objectiveEntities.remove(entityId);
        }
    }
}