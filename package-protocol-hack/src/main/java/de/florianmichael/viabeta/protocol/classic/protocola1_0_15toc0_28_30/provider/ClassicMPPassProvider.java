package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.Provider;

public class ClassicMPPassProvider implements Provider {

    public String getMpPass(final UserConnection user) {
        return "0";
    }

}
