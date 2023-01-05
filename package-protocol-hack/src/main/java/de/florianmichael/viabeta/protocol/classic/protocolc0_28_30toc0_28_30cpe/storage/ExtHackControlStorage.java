package de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.protocol1_2_4_5to1_2_1_3.Protocol1_2_4_5to1_2_1_3;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.ClientboundPackets1_2_4;

public class ExtHackControlStorage extends StoredObject {

    public boolean flying = true;
    public boolean noClip = true;
    public boolean speed = true;
    public boolean respawn = true;
    public boolean thirdPerson = true;
    public float jumpHeight = 1.233F;

    public ExtHackControlStorage(final UserConnection user) {
        super(user);
    }

    public boolean update(final boolean flying, final boolean noClip, final boolean speed, final boolean respawn, final boolean thirdPerson, final short jumpHeight) throws Exception {
        float calculatedJumpHeight = jumpHeight / 32F;
        if (calculatedJumpHeight <= 0) calculatedJumpHeight = 1.233F;

        if (this.flying != flying && this.getUser().getProtocolInfo().getPipeline().contains(Protocol1_2_4_5to1_2_1_3.class)) {
            final PacketWrapper playerAbilities = PacketWrapper.create(ClientboundPackets1_2_4.PLAYER_ABILITIES, this.getUser());
            playerAbilities.write(Type.BOOLEAN, true); // invulnerable
            playerAbilities.write(Type.BOOLEAN, false); // flying
            playerAbilities.write(Type.BOOLEAN, flying); // allow flying
            playerAbilities.write(Type.BOOLEAN, true); // creative mode
            playerAbilities.send(Protocol1_2_4_5to1_2_1_3.class);
        }

        boolean changed = this.flying != flying;
        changed |= this.noClip != noClip;
        changed |= this.speed != speed;
        changed |= this.respawn != respawn;
        changed |= this.thirdPerson != thirdPerson;
        changed |= this.jumpHeight != calculatedJumpHeight;

        this.flying = flying;
        this.noClip = noClip;
        this.speed = speed;
        this.respawn = respawn;
        this.thirdPerson = thirdPerson;
        this.jumpHeight = calculatedJumpHeight;

        return changed;
    }
}
