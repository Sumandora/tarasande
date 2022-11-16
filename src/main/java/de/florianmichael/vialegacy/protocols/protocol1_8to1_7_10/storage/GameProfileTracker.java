package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.model.SkinProperty;

import java.util.ArrayList;
import java.util.List;

public class GameProfileTracker extends StoredObject {

    private final List<SkinProperty> skinProperties = new ArrayList<>();
    private String name;
    private String uuid;

    public GameProfileTracker(UserConnection userConnection) {
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

    public List<SkinProperty> getSkinProperties() {
        return skinProperties;
    }
}
