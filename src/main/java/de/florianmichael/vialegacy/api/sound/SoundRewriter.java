package de.florianmichael.vialegacy.api.sound;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.type.TypeRegistry_1_6_4;

@SuppressWarnings("rawtypes")
public class SoundRewriter<T extends EnZaProtocol> extends RewriterBase<T> {

    public SoundRewriter(T protocol) {
        super(protocol);
    }

    public void register1_7_5NamedSound(final ClientboundPacketType packetType) {
        //noinspection unchecked
        protocol.registerClientbound(packetType, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(TypeRegistry_1_6_4.STRING, Type.STRING); // Sound name
                handler(wrapper -> wrapper.set(Type.STRING, 0, rewrite(wrapper.get(Type.STRING, 0))));
            }
        });
    }
    public void registerNamedSound(final ClientboundPacketType packetType) {
        //noinspection unchecked
        protocol.registerClientbound(packetType, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String sound = wrapper.read(TypeRegistry_1_6_4.STRING);

                    wrapper.write(TypeRegistry_1_6_4.STRING, rewrite(sound));
                });
            }
        });
    }

    public String rewrite(final String tag) {
        return tag;
    }
}
