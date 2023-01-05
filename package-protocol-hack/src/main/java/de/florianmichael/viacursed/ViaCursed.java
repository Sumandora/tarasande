package de.florianmichael.viacursed;

import de.florianmichael.viacursed.base.ViaCursedPlatform;

public class ViaCursed {

    private static ViaCursedPlatform platform;

    public static void init(final ViaCursedPlatform platform) {
        if (ViaCursed.platform != null) {
            throw new IllegalStateException("ViaCursed has already loaded the platform");
        }

        ViaCursed.platform = platform;
    }

    public static ViaCursedPlatform getPlatform() {
        return ViaCursed.platform;
    }
}
