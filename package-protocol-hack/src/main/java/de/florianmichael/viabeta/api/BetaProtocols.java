package de.florianmichael.viabeta.api;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BetaProtocols {

    public static final List<ProtocolVersion> PROTOCOLS = new ArrayList<>();

    // Special protocols
    public static final ProtocolVersion c0_30cpe = register(-7 << 2 | 2, "c0.30 CPE");

    // Pre-Netty
    public static final ProtocolVersion c0_0_15a_1 = register(-2 << 2 | 1, "c0.0.15a-1"); // this version has no protocol id
    public static final ProtocolVersion c0_0_16a_02 = register(-3 << 2 | 1, "c0.0.16a-02");
    public static final ProtocolVersion c0_0_18a_02 = register(-4 << 2 | 1, "c0.0.18a-02");
    public static final ProtocolVersion c0_0_19a_06 = register(-5 << 2 | 1, "c0.0.19a-06");
    public static final ProtocolVersion c0_0_20ac0_27 = register(-6 << 2 | 1, "c0.0.20a-c0.27");
    public static final ProtocolVersion c0_28toc0_30 = register(-7 << 2 | 1, "c0.28-c0.30");

    public static final ProtocolVersion a1_0_15 = register(-13 << 2 | 1, "a1.0.15");
    public static final ProtocolVersion a1_0_16toa1_0_16_2 = register(-14 << 2 | 1, "a1.0.16-a1.0.16.2", new VersionRange("a1.0.16", 0, 2));
    public static final ProtocolVersion a1_0_17toa1_0_17_4 = register(-27 << 2, "a1.0.17-a1.0.17.4", new VersionRange("a1.0.17", 0, 4));
    public static final ProtocolVersion a1_1_0toa1_1_2_1 = register(-2 << 2, "a1.1.0-a1.1.2.1", new VersionRange("a1.1", 0, 2));
    public static final ProtocolVersion a1_2_0toa1_2_1_1 = register(-3 << 2, "a1.2.0-a1.2.1.1", new VersionRange("a1.2", 0, 1));
    public static final ProtocolVersion a1_2_2 = register(-4 << 2, "a1.2.2");
    public static final ProtocolVersion a1_2_3toa1_2_3_4 = register(-5 << 2, "a1.2.3-a1.2.3.4", new VersionRange("a1.2.3", 0, 4));
    public static final ProtocolVersion a1_2_3_5toa1_2_6 = register(-6 << 2, "a1.2.3.5-a1.2.6", new VersionRange("a1.2.3", 5, 6));

    public static final ProtocolVersion b1_0tob1_1_1 = register(-7 << 2, "b1.0-b1.1.1", new VersionRange("b1.0", 0, 1));
    public static final ProtocolVersion b1_1_2 = register(-8 << 2 | 1, "b1.1.2"); // yes its id 8 and incompatible with b1.2-b1.2.2. Thanks mojank
    public static final ProtocolVersion b1_2_0tob1_2_2 = register(-8 << 2, "b1.2-b1.2.2", new VersionRange("b1.2", 0, 2));
    public static final ProtocolVersion b1_3tob1_3_1 = register(-9 << 2, "b1.3-b1.3.1", new VersionRange("b1.3", 0, 1));
    public static final ProtocolVersion b1_4tob1_4_1 = register(-10 << 2, "b1.4-b1.4.1", new VersionRange("b1.4", 0, 1));
    public static final ProtocolVersion b1_5tob1_5_2 = register(-11 << 2, "b1.5-b1.5.2", new VersionRange("b1.5", 0, 2));
    public static final ProtocolVersion b1_6tob1_6_6 = register(-13 << 2, "b1.6-b1.6.6", new VersionRange("b1.6", 0, 6));
    public static final ProtocolVersion b1_7tob1_7_3 = register(-14 << 2, "b1.7-b1.7.3", new VersionRange("b1.7", 0, 3));
    public static final ProtocolVersion b1_8tob1_8_1 = register(-17 << 2, "b1.8-b1.8.1", new VersionRange("b1.8", 0, 1));

    public static final ProtocolVersion r1_0_0tor1_0_1 = register(-22 << 2, "1.0.0-1.0.1", new VersionRange("1.0", 0, 1));
    public static final ProtocolVersion r1_1 = register(-23 << 2, "1.1");
    public static final ProtocolVersion r1_2_1tor1_2_3 = register(-28 << 2, "1.2.1-1.2.3", new VersionRange("1.2", 1, 3));
    public static final ProtocolVersion r1_2_4tor1_2_5 = register(-29 << 2, "1.2.4-1.2.5", new VersionRange("1.2", 4, 5));
    public static final ProtocolVersion r1_3_1tor1_3_2 = register(-39 << 2, "1.3.1-1.3.2", new VersionRange("1.3", 1, 2));
    public static final ProtocolVersion r1_4_2 = register(-47 << 2, "1.4.2");
    public static final ProtocolVersion r1_4_4tor1_4_5 = register(-49 << 2, "1.4.4-1.4.5", new VersionRange("1.4", 4, 5));
    public static final ProtocolVersion r1_4_6tor1_4_7 = register(-51 << 2, "1.4.6-1.4.7", new VersionRange("1.4", 6, 7));
    public static final ProtocolVersion r1_5tor1_5_1 = register(-60 << 2, "1.5-1.5.1", new VersionRange("1.5", 0, 1));
    public static final ProtocolVersion r1_5_2 = register(-61 << 2, "1.5.2");
    public static final ProtocolVersion r1_6_1 = register(-73 << 2, "1.6.1");
    public static final ProtocolVersion r1_6_2 = register(-74 << 2, "1.6.2");
    public static final ProtocolVersion r1_6_3_pre = register(-77 << 2, "1.6.3-pre");
    public static final ProtocolVersion r1_6_4 = register(-78 << 2, "1.6.4");


    private static ProtocolVersion register(final int version, final String name) {
        return register(version, name, null);
    }

    private static ProtocolVersion register(final int version, final String name, final VersionRange versionRange) {
        final ProtocolVersion protocolVersion = new ProtocolVersion(version, -1, name, versionRange);
        PROTOCOLS.add(protocolVersion);
        return protocolVersion;
    }

    public static List<ProtocolVersion> getProtocols() {
        final List<ProtocolVersion> protocolVersions = new ArrayList<>(PROTOCOLS);
        Collections.reverse(protocolVersions);
        return protocolVersions;
    }
}
