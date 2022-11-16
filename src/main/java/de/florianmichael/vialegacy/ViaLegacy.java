package de.florianmichael.vialegacy;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialegacy.exception.ViaLegacyException;
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.vialegacy.protocols.base.BaseProtocol1_6;
import de.florianmichael.vialegacy.protocols.protocol1_2_5to1_2_3.Protocol1_2_5to1_2_3;
import de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5.Protocol1_3_2to1_2_5;
import de.florianmichael.vialegacy.protocols.protocol1_4_2to1_3_2.Protocol1_4_2to1_3_2;
import de.florianmichael.vialegacy.protocols.protocol1_4_3_preto1_4_2.Protocol1_4_3_preto1_4_2;
import de.florianmichael.vialegacy.protocols.protocol1_4_5to1_4_3_pre.Protocol1_4_5to1_4_3_pre;
import de.florianmichael.vialegacy.protocols.protocol1_4_7to1_4_5.Protocol1_4_7to1_4_5;
import de.florianmichael.vialegacy.protocols.protocol1_5_1to1_4_7.Protocol1_5_1to1_4_7;
import de.florianmichael.vialegacy.protocols.protocol1_5_2to1_5_1.Protocol1_5_2to1_5_1;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.Protocol1_6_1to1_5_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.Protocol1_6_2to1_6_1;
import de.florianmichael.vialegacy.protocols.protocol1_6_3to1_6_2.Protocol1_6_3_preto1_6_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_4to1_6_3pre.Protocol1_6_4to1_6_3_pre;
import de.florianmichael.vialegacy.protocols.protocol1_7_10to1_7_5.Protocol1_7_10to1_7_5;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.Protocol1_7_5to1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.Protocol1_8to1_7_10;

import java.util.logging.Logger;

public class ViaLegacy {

    private static Logger logger;

    public static void init(final Logger logger) {
        if (ViaLegacy.logger != null) {
            throw new ViaLegacyException("ViaLegacy is already loaded!");
        }

        ViaLegacy.logger = logger;

        registerProtocol(ProtocolVersion.v1_8, LegacyProtocolVersion.R1_7_10, new Protocol1_8to1_7_10());
        registerProtocol(LegacyProtocolVersion.R1_7_10, LegacyProtocolVersion.R1_7_5, new Protocol1_7_10to1_7_5());

        registerProtocol(LegacyProtocolVersion.R1_7_5, LegacyProtocolVersion.R1_6_4, new Protocol1_7_5to1_6_4());
        registerProtocol(LegacyProtocolVersion.R1_6_4, LegacyProtocolVersion.R1_6_3_PRE, new Protocol1_6_4to1_6_3_pre());
        registerProtocol(LegacyProtocolVersion.R1_6_3_PRE, LegacyProtocolVersion.R1_6_2, new Protocol1_6_3_preto1_6_2());
        registerProtocol(LegacyProtocolVersion.R1_6_2, LegacyProtocolVersion.R1_6_1, new Protocol1_6_2to1_6_1());

        registerProtocol(LegacyProtocolVersion.R1_6_1, LegacyProtocolVersion.R1_5_2, new Protocol1_6_1to1_5_2());
        registerProtocol(LegacyProtocolVersion.R1_5_2, LegacyProtocolVersion.R1_5_1, new Protocol1_5_2to1_5_1());

        registerProtocol(LegacyProtocolVersion.R1_5_1, LegacyProtocolVersion.R1_4_7, new Protocol1_5_1to1_4_7());
        registerProtocol(LegacyProtocolVersion.R1_4_7, LegacyProtocolVersion.R1_4_5, new Protocol1_4_7to1_4_5());
        registerProtocol(LegacyProtocolVersion.R1_4_5, LegacyProtocolVersion.R1_4_3_PRE, new Protocol1_4_5to1_4_3_pre());
        registerProtocol(LegacyProtocolVersion.R1_4_3_PRE, LegacyProtocolVersion.R1_4_2, new Protocol1_4_3_preto1_4_2());

        registerProtocol(LegacyProtocolVersion.R1_4_2, LegacyProtocolVersion.R1_3_2, new Protocol1_4_2to1_3_2());

        registerProtocol(LegacyProtocolVersion.R1_3_2, LegacyProtocolVersion.R1_2_5, new Protocol1_3_2to1_2_5());
        registerProtocol(LegacyProtocolVersion.R1_2_5, LegacyProtocolVersion.R1_2_3, new Protocol1_2_5to1_2_3());

        final int newestVersion = LegacyProtocolVersion.PROTOCOL_VERSIONS.get(2).getVersion();
        final int lastVersion = LegacyProtocolVersion.PROTOCOL_VERSIONS.get(LegacyProtocolVersion.PROTOCOL_VERSIONS.size() - 1).getVersion();

        getLogger().info("Base Protocol from " + newestVersion + " to " + lastVersion + "!");

        Via.getManager().getProtocolManager().registerBaseProtocol(BaseProtocol1_6.INSTANCE, Range.range(newestVersion, BoundType.OPEN, lastVersion, BoundType.CLOSED));
    }

    private static void registerProtocol(final ProtocolVersion from, final ProtocolVersion to, final Protocol<?, ?, ?, ?> protocol) {
        try {
            Via.getManager().getProtocolManager().registerProtocol(protocol, from, to);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLogger().info("Loading " + from.getName() + " -> " + to.getName() + " mappings...");
    }

    public static Logger getLogger() {
        return logger;
    }
}
