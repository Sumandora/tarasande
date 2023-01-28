package de.florianmichael.viasnapshot.api;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SnapshotProtocols {

    public static final List<ProtocolVersion> PROTOCOLS = new ArrayList<>();

    public static final ProtocolVersion s20w14infinite = register(709, "20w14infinite");
    public static final ProtocolVersion sCombatTest8C = register(803, "Combat Test 8c");
    public static final ProtocolVersion s3d_shareware = register(1, "3D Shareware");

    public static ProtocolVersion register(final int version, final String name) {
        final ProtocolVersion protocolVersion = new ProtocolVersion(version, name);
        PROTOCOLS.add(protocolVersion);
        return protocolVersion;
    }

    public static List<ProtocolVersion> getProtocols() {
        final List<ProtocolVersion> protocolVersions = new ArrayList<>(PROTOCOLS);
        Collections.reverse(protocolVersions);
        return protocolVersions;
    }
}
