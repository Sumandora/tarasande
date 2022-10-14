package de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class LevelDataStorage extends StoredObject {

    public String levelType;
    public int worldHeight;
    public int gameMode;

    public LevelDataStorage(UserConnection user) {
        super(user);
    }
}
