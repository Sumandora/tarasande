package de.florianmichael.vialegacy.api;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.vialegacy.protocols.protocol1_1to1_0_0_1.Protocol1_1to1_0_0_1;
import de.florianmichael.vialegacy.protocols.protocol1_2_1_3to1_1.Protocol1_2_1_3to1_1;
import de.florianmichael.vialegacy.protocols.protocol1_2_4_5to1_2_1_3.Protocol1_2_4_5to1_2_1_3;
import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.Protocol1_3_1_2to1_2_4_5;
import de.florianmichael.vialegacy.protocols.protocol1_4_0_2to1_3_1_2.Protocol1_4_0_2to1_3_1_2;
import de.florianmichael.vialegacy.protocols.protocol1_4_3_preto1_4_0_2.Protocol1_4_3_preto1_4_0_2;
import de.florianmichael.vialegacy.protocols.protocol1_4_4_5to1_4_3_pre.Protocol1_4_5to1_4_3_pre;
import de.florianmichael.vialegacy.protocols.protocol1_4_6_7to1_4_4_5.Protocol1_4_6_7to1_4_4_5;
import de.florianmichael.vialegacy.protocols.protocol1_5_1to1_4_6_7.Protocol1_5_1to1_4_6_7;
import de.florianmichael.vialegacy.protocols.protocol1_5_2to1_5_1.Protocol1_5_2to1_5_1;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.Protocol1_6_1to1_5_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.Protocol1_6_2to1_6_1;
import de.florianmichael.vialegacy.protocols.protocol1_6_3to1_6_2.Protocol1_6_3_preto1_6_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_4.Protocol1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_6_4to1_6_3pre.Protocol1_6_4to1_6_3_pre;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.Protocol1_7_0_1_preto1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_2_5to1_7_0_1_pre.Protocol1_7_2_5to1_7_0_1_pre;
import de.florianmichael.vialegacy.protocols.protocol1_7_6_10to1_7_2_5.Protocol1_7_6_10to1_7_2_5;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.Protocol1_8_0_9to1_7_6_10;

import java.util.logging.Logger;

public interface ViaLegacyPlatform {

    default void init() {
        Via.getManager().getSubPlatforms().add("git-ViaLegacy-Florian-Michael:b4eab843d136d7ad3504fc0ed29e5d02517cf407");
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_8_0_9to1_7_6_10(), ProtocolVersion.v1_8, LegacyProtocolVersion.r1_7_6_10);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_7_6_10to1_7_2_5(), LegacyProtocolVersion.r1_7_6_10, LegacyProtocolVersion.r1_7_2_5);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_7_2_5to1_7_0_1_pre(), LegacyProtocolVersion.r1_7_2_5, LegacyProtocolVersion.r1_7_0_1_pre);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_7_0_1_preto1_6_4(), LegacyProtocolVersion.r1_7_0_1_pre, LegacyProtocolVersion.r1_6_4);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_6_4to1_6_3_pre(), LegacyProtocolVersion.r1_6_4, LegacyProtocolVersion.r1_6_3_pre);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_6_3_preto1_6_2(), LegacyProtocolVersion.r1_6_3_pre, LegacyProtocolVersion.r1_6_2);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_6_2to1_6_1(), LegacyProtocolVersion.r1_6_2, LegacyProtocolVersion.r1_6_1);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_6_1to1_5_2(), LegacyProtocolVersion.r1_6_1, LegacyProtocolVersion.r1_5_2);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_5_2to1_5_1(), LegacyProtocolVersion.r1_5_2, LegacyProtocolVersion.r1_5_1);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_5_1to1_4_6_7(), LegacyProtocolVersion.r1_5_1, LegacyProtocolVersion.r1_4_6_7);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_4_6_7to1_4_4_5(), LegacyProtocolVersion.r1_4_6_7, LegacyProtocolVersion.r1_4_4_5);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_4_5to1_4_3_pre(), LegacyProtocolVersion.r1_4_4_5, LegacyProtocolVersion.r1_4_3_pre);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_4_3_preto1_4_0_2(), LegacyProtocolVersion.r1_4_3_pre, LegacyProtocolVersion.r1_4_0_2);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_4_0_2to1_3_1_2(), LegacyProtocolVersion.r1_4_0_2, LegacyProtocolVersion.r1_3_1_2);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_3_1_2to1_2_4_5(), LegacyProtocolVersion.r1_3_1_2, LegacyProtocolVersion.r1_2_4_5);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_2_4_5to1_2_1_3(), LegacyProtocolVersion.r1_2_4_5, LegacyProtocolVersion.r1_2_1_3);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_2_1_3to1_1(), LegacyProtocolVersion.r1_2_1_3, LegacyProtocolVersion.r1_1);
        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_1to1_0_0_1(), LegacyProtocolVersion.r1_1, LegacyProtocolVersion.r1_0_0_1);

        Via.getManager().getProtocolManager().registerBaseProtocol(Protocol1_6_4.INSTANCE, Range.range(LegacyProtocolVersion.r1_6_4.getVersion(), BoundType.OPEN, LegacyProtocolVersion.r1_0_0_1.getVersion(), BoundType.CLOSED));
    }

    Logger getLogger();
}
