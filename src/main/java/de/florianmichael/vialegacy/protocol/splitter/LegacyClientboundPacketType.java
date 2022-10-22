package de.florianmichael.vialegacy.protocol.splitter;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;

import java.util.HashMap;
import java.util.Map;

public interface LegacyClientboundPacketType extends ClientboundPacketType {

    IPacketSplitterLogic getSplitter();

    default void registerSplitter(final LegacyProtocolVersion version) {
        final Map<Integer, IPacketSplitterLogic> splitterMap = LegacyProtocolVersion.SPLITTER_TRACKER.computeIfAbsent(version.getVersion(), integer -> new HashMap<>());

        splitterMap.put(this.getId(), this.getSplitter());
    }
}
