package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class DimensionTracker_1_2_4_5 extends StoredObject {

    private int dimensionId = 0;

    public DimensionTracker_1_2_4_5(UserConnection user) {
        super(user);
    }

    public void setDimension(final int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public int getDimensionId() {
        return this.dimensionId;
    }

}
