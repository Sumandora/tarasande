package de.florianmichael.viasnapshot;

import de.florianmichael.viasnapshot.base.ViaSnapshotPlatform;

public class ViaSnapshot {
    private static ViaSnapshotPlatform platform;

    public static void init(final ViaSnapshotPlatform platform) {
        if (ViaSnapshot.platform != null) {
            throw new IllegalStateException("ViaSnapshot has already loaded the platform");
        }

        ViaSnapshot.platform = platform;
    }

    public static ViaSnapshotPlatform getPlatform() {
        return ViaSnapshot.platform;
    }
}
