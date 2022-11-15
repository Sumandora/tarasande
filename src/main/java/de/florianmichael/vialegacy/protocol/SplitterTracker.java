package de.florianmichael.vialegacy.protocol;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialegacy.protocol.splitter.IPacketSplitter;
import de.florianmichael.vialegacy.api.LegacyClientboundPacketType;

import java.util.HashMap;
import java.util.Map;

public class SplitterTracker extends StoredObject {

    public final Map<Integer, IPacketSplitter> splitter = new HashMap<>();

    public SplitterTracker(UserConnection user, final LegacyClientboundPacketType[]... legacyClientboundPacketTypes) {
        super(user);

        for (LegacyClientboundPacketType[] legacyClientboundPacketType : legacyClientboundPacketTypes) {
            for (LegacyClientboundPacketType clientboundPacketType : legacyClientboundPacketType) {
                splitter.put(clientboundPacketType.getId(), clientboundPacketType.getSplitter());
            }
        }
    }

    public IPacketSplitter getSplitter(final int packetId) {
        return splitter.get(packetId);
    }
}
