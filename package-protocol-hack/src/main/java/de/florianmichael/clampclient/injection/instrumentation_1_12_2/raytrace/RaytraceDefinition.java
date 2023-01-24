package de.florianmichael.clampclient.injection.instrumentation_1_12_2.raytrace;

import de.florianmichael.clampclient.injection.instrumentation_1_12_2.raytrace.impl.Raytrace_1_8to1_12_2;
import de.florianmichael.vialoadingbase.util.VersionListEnum;

public class RaytraceDefinition {
    private static RaytraceBase CLASS_WRAPPER = null;

    public static void reload(final VersionListEnum version) {
        CLASS_WRAPPER = null;

        if (version.isOlderThanOrEqualTo(VersionListEnum.r1_12_2)) {
            CLASS_WRAPPER = Raytrace_1_8to1_12_2.SELF;
        }
    }

    public static RaytraceBase getClassWrapper() {
        return CLASS_WRAPPER;
    }
}
