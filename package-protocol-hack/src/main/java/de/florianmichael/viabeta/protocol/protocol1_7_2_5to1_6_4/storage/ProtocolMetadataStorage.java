package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class ProtocolMetadataStorage extends StoredObject {

    private boolean authenticate;
    private boolean skipEncryption;

    public ProtocolMetadataStorage(UserConnection user) {
        super(user);
    }

    public boolean isAuthenticated() {
        return authenticate;
    }

    public void authenticate(boolean authenticate) {
        this.authenticate = authenticate;
    }

    public boolean isSkippingEncryption() {
        return skipEncryption;
    }

    public void skipEncryption(boolean skipEncryption) {
        this.skipEncryption = skipEncryption;
    }
}
