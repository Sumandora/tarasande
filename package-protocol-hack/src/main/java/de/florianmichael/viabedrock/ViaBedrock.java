package de.florianmichael.viabedrock;

import de.florianmichael.viabedrock.base.ViaBedrockConfig;
import de.florianmichael.viabedrock.base.ViaBedrockPlatform;

public class ViaBedrock {
    public static final String PREFIX_1_7 = "§c[ViaBedrock]§f ";

    private static ViaBedrockPlatform platform;
    private static ViaBedrockConfig config;

    public static void init(final ViaBedrockPlatform platform, final ViaBedrockConfig config) {
        if (ViaBedrock.platform != null) {
            throw new IllegalStateException("ViaBedrock has already loaded the platform");
        }

        ViaBedrock.platform = platform;
        ViaBedrock.config = config;
    }

    public static ViaBedrockPlatform getPlatform() {
        return platform;
    }

    public static ViaBedrockConfig getConfig() {
        return config;
    }
}
