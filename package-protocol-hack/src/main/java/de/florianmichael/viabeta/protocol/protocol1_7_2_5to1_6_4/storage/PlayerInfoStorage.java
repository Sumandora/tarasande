package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class PlayerInfoStorage extends StoredObject {

    public int entityId = -1;

    public double posX = 8;
    public double posY = 64;
    public double posZ = 8;

    public float yaw = -180;
    public float pitch = 0;

    public boolean onGround = false;

    public PlayerInfoStorage(final UserConnection userConnection) {
        super(userConnection);
    }
}
