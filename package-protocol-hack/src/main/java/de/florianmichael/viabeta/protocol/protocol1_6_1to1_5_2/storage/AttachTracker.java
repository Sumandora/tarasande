package de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class AttachTracker extends StoredObject {

    public int vehicleEntityId = -1;
    public boolean lastSneakState = false;

    public AttachTracker(UserConnection user) {
        super(user);
    }

}
