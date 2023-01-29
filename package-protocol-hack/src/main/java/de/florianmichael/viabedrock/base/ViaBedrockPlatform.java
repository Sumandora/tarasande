package de.florianmichael.viabedrock.base;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viabedrock.ViaBedrock;
import de.florianmichael.viabedrock.ViaBedrockConfigImpl;
import de.florianmichael.viabedrock.api.BedrockProtocols;
import de.florianmichael.viabedrock.protocol.Protocol1_19_3toBedrock1_19_51;
import de.florianmichael.viabedrock.rawdata.FakeDimensionData;

import java.io.File;
import java.util.logging.Logger;

public interface ViaBedrockPlatform {

    default void init() {
        final ViaBedrockConfigImpl config = new ViaBedrockConfigImpl(new File(getDataFolder(), "viabedrock.yml"));
        config.reloadConfig();

        ViaBedrock.init(this, config);
        FakeDimensionData.load();

        Via.getManager().getProtocolManager().registerProtocol(new Protocol1_19_3toBedrock1_19_51(), ProtocolVersion.v1_19_3, BedrockProtocols.VIA_PROTOCOL_VERSION);
    }

    Logger getLogger();
    File getDataFolder();
}
