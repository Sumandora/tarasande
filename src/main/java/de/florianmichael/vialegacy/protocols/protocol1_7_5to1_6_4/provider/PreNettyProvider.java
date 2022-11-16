package de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.provider;

import com.viaversion.viaversion.api.platform.providers.Provider;
import de.florianmichael.vialegacy.exception.ViaLegacyException;
import io.netty.channel.ChannelHandler;

public class PreNettyProvider implements Provider {

    public String encryptKey() {
        return "encrypt";
    }
    public String decryptKey() {
        return "decrypt";
    }
    public String splitterKey() {
        return "splitter";
    }
    public String prependerKey() {
        return "prepender";
    }

    public ChannelHandler encryptor() {
        throw new ViaLegacyException("PreNettyProvider not implemented");
    }
    public ChannelHandler decryptor() {
        throw new ViaLegacyException("PreNettyProvider not implemented");
    }
}
