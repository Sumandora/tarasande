package de.florianmichael.viacursed.protocol.protocol1_16_2toCombatTest8c;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;

public class Protocol1_16_2toCombatTest8c extends AbstractProtocol<ClientboundPackets1_16_2, ClientboundPackets1_16_2, ServerboundPackets1_16_2, ServerboundPackets1_16_2> {

    public Protocol1_16_2toCombatTest8c() {
        super(ClientboundPackets1_16_2.class, ClientboundPackets1_16_2.class, ServerboundPackets1_16_2.class, ServerboundPackets1_16_2.class);
    }

    @Override
    protected void registerPackets() {
        this.registerServerbound(ServerboundPackets1_16_2.CLIENT_SETTINGS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); //language
                map(Type.BYTE); //viewDistance
                map(Type.VAR_INT); //chatVisibility
                map(Type.BOOLEAN); //chatColors
                map(Type.UNSIGNED_BYTE); //playerModelBitMask
                map(Type.VAR_INT); //mainArm
                handler(wrapper -> {
                    wrapper.write(Type.BOOLEAN, false); //useShieldOnCrouch
                });
            }
        });
    }

}
