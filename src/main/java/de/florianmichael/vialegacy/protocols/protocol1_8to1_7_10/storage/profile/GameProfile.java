package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.storage.profile;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.util.ArrayList;
import java.util.List;

public class GameProfile extends StoredObject {

    private final List<Property> skinProperties = new ArrayList<>();
    private String name;
    private String uuid;

    public GameProfile(UserConnection userConnection) {
        super(userConnection);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<Property> getSkinProperties() {
        return skinProperties;
    }
}
