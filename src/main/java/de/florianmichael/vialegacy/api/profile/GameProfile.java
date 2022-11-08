package de.florianmichael.vialegacy.api.profile;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialegacy.api.profile.property.PropertyMap;

import java.util.UUID;

public class GameProfile extends StoredObject {

    private final String name;
    private UUID uuid;
    private final PropertyMap skinProperties;

    public GameProfile(UserConnection userConnection, String name, UUID uuid, PropertyMap skinProperties) {
        super(userConnection);
        this.name = name;
        this.uuid = uuid;
        this.skinProperties = skinProperties;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public PropertyMap getSkinProperties() {
        return skinProperties;
    }
}
