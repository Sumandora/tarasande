package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class ClassicOpLevelStorage extends StoredObject {

    private byte opLevel;

    public ClassicOpLevelStorage(final UserConnection user) {
        super(user);
    }

    public void setOpLevel(final byte opLevel) {
        this.opLevel = opLevel;
    }

    public byte getOpLevel() {
        return this.opLevel;
    }

}
