/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.api.sound;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.type.Types1_6_4;

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
                map(Types1_6_4.STRING, Type.STRING); // Sound name
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
                    final String sound = rewrite(wrapper.read(Types1_6_4.STRING));
                    if (sound == null || sound.isEmpty()) {
                        wrapper.cancel();
                    }
                    wrapper.write(Types1_6_4.STRING, sound);
                });
            }
        });
    }

    public String rewrite(final String tag) {
        return tag;
    }
}
