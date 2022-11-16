package de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5.provider;

import com.viaversion.viaversion.api.platform.providers.Provider;
import de.florianmichael.vialegacy.exception.ViaLegacyException;

public class OldAuthProvider implements Provider {

    public void sendJoinServer(final String serverId) {
        throw new ViaLegacyException("OldAuthProvider not implemented");
    }
}
