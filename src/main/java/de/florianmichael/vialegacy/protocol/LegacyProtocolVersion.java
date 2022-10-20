package de.florianmichael.vialegacy.protocol;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialegacy.protocol.splitter.IPacketSplitterLogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LegacyProtocolVersion extends ProtocolVersion {

    public static final Map<Integer, Map<Integer, IPacketSplitterLogic>> SPLITTER_TRACKER = new HashMap<>();
    public static final List<ProtocolVersion> PROTOCOL_VERSIONS = new ArrayList<>();
    public static final List<ProtocolVersion> PRE_NETTY_VERSIONS = new ArrayList<>();

    public static final ProtocolVersion R1_7_10 = new ProtocolVersion(5, "1.7.6-1.7.10") {
        { PROTOCOL_VERSIONS.add(this); }
    };
    public static final ProtocolVersion R1_7_5 = new ProtocolVersion(4, "1.7-1.7.5") {
        { PROTOCOL_VERSIONS.add(this); }
    };

    public static final LegacyProtocolVersion R1_6_4 = new LegacyProtocolVersion(78, "1.6.4");
    public static final LegacyProtocolVersion R1_6_3_PRE = new LegacyProtocolVersion(77, "1.6.3-pre");
    public static final LegacyProtocolVersion R1_6_2 = new LegacyProtocolVersion(74, "1.6.2");
    public static final LegacyProtocolVersion R1_6_1 = new LegacyProtocolVersion(73, "1.6.1");

    public static final LegacyProtocolVersion R1_5_2 = new LegacyProtocolVersion(61, "1.5.2");
    public static final LegacyProtocolVersion R1_5_1 = new LegacyProtocolVersion(60, "1.5.1");

    public static final LegacyProtocolVersion R1_4_7 = new LegacyProtocolVersion(51, "1.4.6-1.4.7");
    public static final LegacyProtocolVersion R1_4_5 = new LegacyProtocolVersion(49, "1.4.4-1.4.5");
    public static final LegacyProtocolVersion R1_4_3_PRE = new LegacyProtocolVersion(48, "1.4.3-pre");
    public static final LegacyProtocolVersion R1_4_2 = new LegacyProtocolVersion(47, "1.4-1.4.2");

    public static final LegacyProtocolVersion R1_3_2 = new LegacyProtocolVersion(39, "1.3.1-1.3.2");

    public static final LegacyProtocolVersion R1_2_5 = new LegacyProtocolVersion(29, "1.2.4-1.2.5");
    public static final LegacyProtocolVersion R1_2_3 = new LegacyProtocolVersion(28, "1.2.1-1.2.3");

    public static final LegacyProtocolVersion R1_1 = new LegacyProtocolVersion(23, "1.1");

    public static final LegacyProtocolVersion R1_0 = new LegacyProtocolVersion(22, "1.0");

    public LegacyProtocolVersion(int version, String name) {
        super(-version, name);

        PROTOCOL_VERSIONS.add(this);
        PRE_NETTY_VERSIONS.add(this);
    }
}
