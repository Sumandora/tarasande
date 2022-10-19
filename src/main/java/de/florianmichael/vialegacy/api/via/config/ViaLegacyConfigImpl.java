package de.florianmichael.vialegacy.api.via.config;

import com.viaversion.viaversion.util.Config;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ViaLegacyConfigImpl extends Config implements ViaLegacyConfig {

    public ViaLegacyConfigImpl(File configFile) {
        super(configFile);
    }

    @Override
    public URL getDefaultConfigURL() {
        return getClass().getClassLoader().getResource("florianmichael/vialegacy/config.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> config) {
    }

    @Override
    public List<String> getUnsupportedOptions() {
        return Collections.emptyList();
    }
}
