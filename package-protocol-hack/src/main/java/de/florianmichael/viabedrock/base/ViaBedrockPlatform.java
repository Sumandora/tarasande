package de.florianmichael.viabedrock.base;

import de.florianmichael.viabedrock.ViaBedrock;
import de.florianmichael.viabedrock.ViaBedrockConfigImpl;
import de.florianmichael.viabedrock.rawdata.FakeDimensionData;

import java.io.File;
import java.util.logging.Logger;

public interface ViaBedrockPlatform {

    default void init() {
        final ViaBedrockConfigImpl config = new ViaBedrockConfigImpl(new File(getDataFolder(), "viabedrock.yml"));
        config.reloadConfig();

        ViaBedrock.init(this, config);
        FakeDimensionData.load();
    }

    Logger getLogger();
    File getDataFolder();
}
