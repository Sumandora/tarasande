package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class PlayerStorage extends StoredObject {

    public long runtimeEntityId;
    public long uniqueEntityId;

    public PlayerStorage(UserConnection user) {
        super(user);
    }
}
