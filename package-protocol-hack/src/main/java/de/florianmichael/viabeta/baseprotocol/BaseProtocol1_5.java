package de.florianmichael.viabeta.baseprotocol;

import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.ServerboundPackets1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.ServerboundPackets1_6_4;

public class BaseProtocol1_5 extends AbstractSimpleProtocol {

    public static final BaseProtocol1_5 INSTANCE = new BaseProtocol1_5();

    private BaseProtocol1_5() {
        this.initialize();
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerServerbound(State.STATUS, ServerboundPackets1_5_2.SERVER_PING.getId(), ServerboundPackets1_6_4.SERVER_PING.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.clearPacket();
                    wrapper.write(Type.BYTE, (byte) 1); // readSuccessfully
                });
            }
        });
    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
