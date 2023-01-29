package de.florianmichael.viasnapshot.api;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SnapshotProtocols {

    public static final List<ProtocolVersion> PROTOCOLS = new ArrayList<>();

    public static final ProtocolVersion s20w14infinite = register(709, "20w14infinite");
    public static final ProtocolVersion sCombatTest8C = register(803, "Combat Test 8c");
    public static final ProtocolVersion s3d_shareware = register(1, "3D Shareware v1.34");

    public static ProtocolVersion register(final int version, final String name) {
        final ProtocolVersion protocolVersion = new ProtocolVersion(version, name);
        PROTOCOLS.add(protocolVersion);
        return protocolVersion;
    }

    public static void addProtocols(List<ProtocolVersion> origin) {
        final int v1_14Index = origin.indexOf(ProtocolVersion.v1_14);
        final int v1_16Index = origin.indexOf(ProtocolVersion.v1_16);
        final int v1_16_2Index = origin.indexOf(ProtocolVersion.v1_16_2);

        origin.add(v1_14Index - 1, s3d_shareware);
        origin.add(v1_16Index - 1, s20w14infinite);
        origin.add(v1_16_2Index - 1, sCombatTest8C);
    }

    public static List<ProtocolVersion> getProtocols() {
        final List<ProtocolVersion> protocolVersions = new ArrayList<>(PROTOCOLS);
        Collections.reverse(protocolVersions);
        return protocolVersions;
    }
}
