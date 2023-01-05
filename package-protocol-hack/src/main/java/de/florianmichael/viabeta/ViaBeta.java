package de.florianmichael.viabeta;

import de.florianmichael.viabeta.base.ViaBetaConfig;
import de.florianmichael.viabeta.base.ViaBetaPlatform;

public class ViaBeta {

    private static ViaBetaPlatform platform;
    private static ViaBetaConfig config;

    public static void init(final ViaBetaPlatform platform, final ViaBetaConfig config) {
        if (ViaBeta.platform != null || ViaBeta.config != null) {
            throw new IllegalStateException("ViaBeta has already loaded the platform");
        }
        ViaBeta.platform = platform;
        ViaBeta.config = config;
    }

    public static ViaBetaPlatform getPlatform() {
        return ViaBeta.platform;
    }

    public static ViaBetaConfig getConfig() {
        return ViaBeta.config;
    }
}
