package de.florianmichael.clampclient.injection.instrumentation_1_12_2.raytrace;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.clampclient.injection.instrumentation_1_12_2.raytrace.impl.Raytrace_1_8to1_12_2;
import de.florianmichael.vialoadingbase.api.version.ComparableProtocolVersion;

public class RaytraceDefinition {
    private static RaytraceBase CLASS_WRAPPER = null;

    public static void reload(final ComparableProtocolVersion version) {
        CLASS_WRAPPER = null;

        if (version.isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            CLASS_WRAPPER = Raytrace_1_8to1_12_2.SELF;
        }
    }

    public static RaytraceBase getClassWrapper() {
        return CLASS_WRAPPER;
    }
}
