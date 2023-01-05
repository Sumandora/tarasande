package de.florianmichael.viabeta.api;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionRange;

import java.util.*;

public enum LegacyVersionEnum {

    c0_0_15a_1(-2 << 2 | 1, "c0.0.15a-1"), // this version has no id
    c0_0_16a_02(-3 << 2 | 1, "c0.0.16a-02"),
    c0_0_18a_02(-4 << 2 | 1, "c0.0.18a-02"),
    c0_0_19a_06(-5 << 2 | 1, "c0.0.19a-06"),
    c0_0_20ac0_27(-6 << 2 | 1, "c0.0.20a-c0.27"),
    c0_30cpe(-7 << 2 | 2, "c0.30 CPE"),
    c0_28toc0_30(-7 << 2 | 1, "c0.28-c0.30"),
    a1_0_15(-13 << 2 | 1, "a1.0.15"),
    a1_0_16toa1_0_16_2(-14 << 2 | 1, "a1.0.16-a1.0.16.2"),
    a1_0_17toa1_0_17_4(-27 << 2, "a1.0.17-a1.0.17.4"),
    a1_1_0toa1_1_2_1(-2 << 2, "a1.1.0-a1.1.2.1"),
    a1_2_0toa1_2_1_1(-3 << 2, "a1.2.0-a1.2.1.1"),
    a1_2_2(-4 << 2, "a1.2.2"),
    a1_2_3toa1_2_3_4(-5 << 2, "a1.2.3-a1.2.3.4"),
    a1_2_3_5toa1_2_6(-6 << 2, "a1.2.3.5-a1.2.6"),
    b1_0tob1_1_1(-7 << 2, "b1.0-b1.1.1"),
    b1_1_2(-8 << 2 | 1, "b1.1.2"), // yes its 100% id 8 and incompatible with b1.2-b1.2.2
    b1_2_0tob1_2_2(-8 << 2, "b1.2-b1.2.2"),
    b1_3tob1_3_1(-9 << 2, "b1.3-b1.3.1"),
    b1_4tob1_4_1(-10 << 2, "b1.4-b1.4.1"),
    b1_5tob1_5_2(-11 << 2, "b1.5-b1.5.2"),
    b1_6tob1_6_6(-13 << 2, "b1.6-b1.6.6"),
    b1_7tob1_7_3(-14 << 2, "b1.7-b1.7.3"),
    b1_8tob1_8_1(-17 << 2, "b1.8-b1.8.1"),
    r1_0_0tor1_0_1(-22 << 2, "1.0.0-1.0.1", new VersionRange("1.0", 0, 1)),
    r1_1(-23 << 2, "1.1"),
    r1_2_1tor1_2_3(-28 << 2, "1.2.1-1.2.3", new VersionRange("1.2", 1, 3)),
    r1_2_4tor1_2_5(-29 << 2, "1.2.4-1.2.5", new VersionRange("1.2", 4, 5)),
    r1_3_1tor1_3_2(-39 << 2, "1.3.1-1.3.2", new VersionRange("1.3", 1, 2)),
    r1_4_2(-47 << 2, "1.4.2"),
    r1_4_4tor1_4_5(-49 << 2, "1.4.4-1.4.5", new VersionRange("1.4", 4, 5)),
    r1_4_6tor1_4_7(-51 << 2, "1.4.6-1.4.7", new VersionRange("1.4", 6, 7)),
    r1_5tor1_5_1(-60 << 2, "1.5-1.5.1"),
    r1_5_2(-61 << 2, "1.5.2"),
    r1_6_1(-73 << 2, "1.6.1"),
    r1_6_2(-74 << 2, "1.6.2"),
    r1_6_3(-77 << 2, "1.6.3"),
    r1_6_4(-78 << 2, "1.6.4"),

    //
    UNKNOWN(ProtocolVersion.unknown), // Not in Registry
    ;


    private static final Map<ProtocolVersion, LegacyVersionEnum> VERSION_REGISTRY = new HashMap<>();

    public static LegacyVersionEnum fromProtocolVersion(final ProtocolVersion protocolVersion) {
        if (!protocolVersion.isKnown()) return UNKNOWN;
        return VERSION_REGISTRY.getOrDefault(protocolVersion, UNKNOWN);
    }

    public static LegacyVersionEnum fromProtocolId(final int protocolId) {
        return fromProtocolVersion(ProtocolVersion.getProtocol(protocolId));
    }

    public static LegacyVersionEnum fromUserConnection(final UserConnection userConnection) {
        return fromUserConnection(userConnection, true);
    }

    public static LegacyVersionEnum fromUserConnection(final UserConnection userConnection, final boolean serverProtocol) {
        return fromProtocolId(serverProtocol ? userConnection.getProtocolInfo().getServerProtocolVersion() : userConnection.getProtocolInfo().getProtocolVersion());
    }

    private final ProtocolVersion protocolVersion;

    LegacyVersionEnum(final int version, final String name) {
        this(ProtocolVersion.register(version, name));
    }

    LegacyVersionEnum(final int version, final String name, final VersionRange versionRange) {
        this(ProtocolVersion.register(version, name, versionRange));
    }

    LegacyVersionEnum(final ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public ProtocolVersion getProtocol() {
        return this.protocolVersion;
    }

    public String getName() {
        return this.protocolVersion.getName();
    }

    public int getVersion() {
        return this.protocolVersion.getVersion();
    }

    public boolean isOlderThanOrEqualTo(final LegacyVersionEnum other) {
        return this.ordinal() <= other.ordinal();
    }

    public boolean isNewerThanOrEqualTo(final LegacyVersionEnum other) {
        return this.ordinal() >= other.ordinal();
    }
}
