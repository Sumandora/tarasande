package de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class PlayerHealthTracker extends StoredObject {

    private short health = 20;

    public PlayerHealthTracker(UserConnection user) {
        super(user);
    }

    public void setHealth(final short health) {
        this.health = health;
    }

    public short getHealth() {
        return this.health;
    }

}
