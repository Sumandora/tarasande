package de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.Protocolb1_3_0_1tob1_2_0_2;

public class BlockDigStorage extends StoredObject {

    public int tick = 1;
    private final Position position;
    private final short facing;

    public BlockDigStorage(UserConnection user, final Position position, final short facing) {
        super(user);
        this.position = position;
        this.facing = facing;
    }

    public void tick() {
        try {
            if (tick >= 5) {
                Protocolb1_3_0_1tob1_2_0_2.sendBlockDigPacket(this.getUser(), (byte) 0, position, facing);
                tick = 0;
            } else {
                tick++;
            }
            Protocolb1_3_0_1tob1_2_0_2.sendBlockDigPacket(this.getUser(), (byte) 1, position, facing);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
