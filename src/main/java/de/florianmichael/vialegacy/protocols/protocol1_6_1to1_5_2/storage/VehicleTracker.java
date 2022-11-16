package de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class VehicleTracker extends StoredObject {

    public int vehicleId;

    public VehicleTracker(UserConnection user) {
        super(user);
    }
}
