package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class DimensionTracker_1_1 extends StoredObject {

    private int dimensionId = 0;

    public DimensionTracker_1_1(UserConnection user) {
        super(user);
    }

    public void setDimension(final int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public int getDimensionId() {
        return this.dimensionId;
    }

}
