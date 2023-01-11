package de.florianmichael.viacursed;

import de.florianmichael.viacursed.base.ViaCursedConfig;
import de.florianmichael.viacursed.base.ViaCursedPlatform;

public class ViaCursed {
    public static final String PREFIX_1_7 = "§c[ViaCursed]§f ";

    private static ViaCursedPlatform platform;
    private static ViaCursedConfig config;

    public static void init(final ViaCursedPlatform platform, final ViaCursedConfig config) {
        if (ViaCursed.platform != null) {
            throw new IllegalStateException("ViaCursed has already loaded the platform");
        }

        ViaCursed.platform = platform;
        ViaCursed.config = config;
    }

    public static ViaCursedPlatform getPlatform() {
        return ViaCursed.platform;
    }

    public static ViaCursedConfig getConfig() {
        return config;
    }
}
