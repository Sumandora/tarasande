package de.florianmichael.viabeta.baseprotocol;

import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.ClientboundPacketsb1_8;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.ServerboundPacketsb1_8;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.Protocolb1_8_0_1tob1_7_0_3;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;

public class BaseProtocolb1_7 extends AbstractSimpleProtocol {

    public static final BaseProtocolb1_7 INSTANCE = new BaseProtocolb1_7();

    private BaseProtocolb1_7() {
        this.initialize();
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerServerbound(State.STATUS, -1, ServerboundPacketsb1_8.SERVER_PING.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final PacketWrapper pingResponse = PacketWrapper.create(ClientboundPacketsb1_8.DISCONNECT, wrapper.user());
                    pingResponse.write(Type1_6_4.STRING, "[ViaBeta] The server seems to be running!\nWait 5 seconds between each connection§0§1");
                    pingResponse.send(BaseProtocolb1_7.class);
                });
            }
        });
    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
