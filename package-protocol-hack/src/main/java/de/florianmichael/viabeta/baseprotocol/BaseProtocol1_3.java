package de.florianmichael.viabeta.baseprotocol;

import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.ClientboundPackets1_3_1;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.ServerboundPackets1_3_1;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.ClientboundPackets1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.ServerboundPackets1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;

import java.util.logging.Level;

public class BaseProtocol1_3 extends AbstractSimpleProtocol {

    public static final BaseProtocol1_3 INSTANCE = new BaseProtocol1_3();

    private BaseProtocol1_3() {
        this.initialize();
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerClientbound(State.STATUS, ClientboundPackets1_3_1.DISCONNECT.getId(), ClientboundPackets1_4_2.DISCONNECT.getId(), new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    final String reason = wrapper.read(Type1_6_4.STRING); // reason
                    try {
                        final ProtocolInfo info = wrapper.user().getProtocolInfo();
                        final String[] pingParts = reason.split("§");
                        final String out = "§1\0" + (-info.getServerProtocolVersion() >> 2) + "\0" + ProtocolVersion.getProtocol(info.getServerProtocolVersion()).getName() + "\0" + pingParts[0] + "\0" + pingParts[1] + "\0" + pingParts[2];
                        wrapper.write(Type1_6_4.STRING, out);
                    } catch (Throwable e) {
                        ViaBeta.getPlatform().getLogger().log(Level.WARNING, "Could not parse 1.3.1 ping: " + reason, e);
                        wrapper.cancel();
                    }
                });
            }
        });

        this.registerServerbound(State.STATUS, ServerboundPackets1_3_1.SERVER_PING.getId(), ServerboundPackets1_5_2.SERVER_PING.getId(), new PacketHandlers() {
            @Override
            public void register() {
                handler(PacketWrapper::clearPacket);
            }
        });
    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
