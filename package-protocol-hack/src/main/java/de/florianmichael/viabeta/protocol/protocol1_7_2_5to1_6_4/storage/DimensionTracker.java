package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class DimensionTracker extends StoredObject {

    private int dimensionId = 0;

    public DimensionTracker(UserConnection user) {
        super(user);
    }

    public void setDimension(final int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public int getDimensionId() {
        return this.dimensionId;
    }
}
