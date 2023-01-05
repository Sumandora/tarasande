package de.florianmichael.viabeta.protocol.protocol1_6_4.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class HandshakeStorage extends StoredObject {

    private final String hostname;
    private final int port;

    public HandshakeStorage(final UserConnection user, final String hostName, final int port) {
        super(user);
        this.hostname = hostName;
        this.port = port;
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

}
