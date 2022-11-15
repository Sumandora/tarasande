package de.florianmichael.vialegacy.api;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import de.florianmichael.vialegacy.protocol.splitter.IPacketSplitter;

public interface LegacyClientboundPacketType extends ClientboundPacketType {

    IPacketSplitter getSplitter();
}
