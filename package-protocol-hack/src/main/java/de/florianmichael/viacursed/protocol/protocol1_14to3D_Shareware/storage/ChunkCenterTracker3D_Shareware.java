package de.florianmichael.viacursed.protocol.protocol1_14to3D_Shareware.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class ChunkCenterTracker3D_Shareware extends StoredObject {

    private boolean forceSendCenterChunk = true;
    private int chunkCenterX, chunkCenterZ;

    public ChunkCenterTracker3D_Shareware(UserConnection user) {
        super(user);
    }

    public boolean isForceSendCenterChunk() {
        return forceSendCenterChunk;
    }

    public void setForceSendCenterChunk(boolean forceSendCenterChunk) {
        this.forceSendCenterChunk = forceSendCenterChunk;
    }

    public int getChunkCenterX() {
        return chunkCenterX;
    }

    public void setChunkCenterX(int chunkCenterX) {
        this.chunkCenterX = chunkCenterX;
    }

    public int getChunkCenterZ() {
        return chunkCenterZ;
    }

    public void setChunkCenterZ(int chunkCenterZ) {
        this.chunkCenterZ = chunkCenterZ;
    }

}
