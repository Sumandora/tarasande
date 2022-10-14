package de.florianmichael.vialegacy.api.profile;

import de.florianmichael.vialegacy.api.profile.property.PropertyMap;

import java.util.UUID;

public class GameProfile {

    private final String name;
    private final UUID uuid;
    private final PropertyMap skinProperties;

    public GameProfile(String name, UUID uuid, PropertyMap skinProperties) {
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

    public PropertyMap getSkinProperties() {
        return skinProperties;
    }
}
