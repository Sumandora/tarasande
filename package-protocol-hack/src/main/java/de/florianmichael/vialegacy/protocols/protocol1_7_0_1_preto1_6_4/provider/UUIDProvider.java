package de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.provider;

import com.viaversion.viaversion.api.platform.providers.Provider;

import java.util.UUID;

public class UUIDProvider implements Provider {

    public UUID getPlayerUuid() {
        return UUID.randomUUID();
    }
}
