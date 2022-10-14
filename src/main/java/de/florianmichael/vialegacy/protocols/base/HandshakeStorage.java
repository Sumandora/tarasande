package de.florianmichael.vialegacy.protocols.base;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class HandshakeStorage extends StoredObject {

    public String hostname;
    public int port;

    public HandshakeStorage(UserConnection user) {
        super(user);
    }
}
