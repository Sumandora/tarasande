package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.api.model.ChunkCoord;

public class ClassicPositionTracker extends StoredObject {

    public double posX;
    public double stance;
    public double posZ;
    public float yaw;
    public float pitch;

    public boolean spawned;

    public ClassicPositionTracker(final UserConnection user) {
        super(user);
    }

    public void writeToPacket(final PacketWrapper wrapper) {
        final int x = (int) (this.posX * 32.0F);
        final int y = (int) (this.stance * 32.0F);
        final int z = (int) (this.posZ * 32.0F);
        final int yaw = (int) (this.yaw * 256.0F / 360.0F) & 255;
        final int pitch = (int) (this.pitch * 256.0F / 360.0F) & 255;

        wrapper.write(Type.BYTE, (byte) -1); // entity id
        wrapper.write(Type.SHORT, (short) x); // x
        wrapper.write(Type.SHORT, (short) y); // y
        wrapper.write(Type.SHORT, (short) z); // z
        wrapper.write(Type.BYTE, (byte) (yaw - 128)); // yaw
        wrapper.write(Type.BYTE, (byte) pitch); // pitch
    }

    public Position getBlockPosition() {
        return new Position(floor(this.posX), floor(this.stance), floor(this.posZ));
    }

    public ChunkCoord getChunkPosition() {
        final Position pos = this.getBlockPosition();
        return new ChunkCoord(pos.x() >> 4, pos.z() >> 4);
    }

    private static int floor(double f) {
        int i = (int) f;
        return f < (double) i ? i - 1 : i;
    }

}
