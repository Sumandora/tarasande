package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.baseprotocol;

import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ServerboundHandshakePackets;
import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.BedrockSessionStorage;

import java.net.InetSocketAddress;
import java.util.logging.Level;

public class BedrockSessionBaseProtocol extends AbstractSimpleProtocol {

    public static final BedrockSessionBaseProtocol INSTANCE = new BedrockSessionBaseProtocol();

    public BedrockSessionBaseProtocol() {
        this.initialize();
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerServerbound(State.HANDSHAKE, ServerboundHandshakePackets.CLIENT_INTENTION.getId(), ServerboundHandshakePackets.CLIENT_INTENTION.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    wrapper.read(Type.VAR_INT); // protocolVersion
                    final String hostname = wrapper.read(Type.STRING); // hostName
                    final int port = wrapper.read(Type.UNSIGNED_SHORT); // port

                    wrapper.user().put(new BedrockSessionStorage(wrapper.user(), new InetSocketAddress(hostname, port)));
                    ViaCursed.getPlatform().getLogger().log(Level.INFO, "The adventure begins now!");
                });
            }
        });
    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
