package de.florianmichael.viacursed;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.util.Config;
import de.florianmichael.viacursed.base.ViaCursedConfig;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ViaCursedConfigImpl extends Config implements ViaCursedConfig {

    private boolean sendNativeVersionInPing;
    private int nativeVersion;

    public ViaCursedConfigImpl(final File configFile) {
        super(configFile);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.loadFields();
    }

    private void loadFields() {
        this.sendNativeVersionInPing = this.getBoolean("send-native-version-in-ping", true);
        this.nativeVersion = this.getInt("native-version", ProtocolVersion.v1_19_3.getOriginalVersion());
    }

    @Override
    public URL getDefaultConfigURL() {
        return this.getClass().getClassLoader().getResource("assets/viacursed/viacursed.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> map) {
    }

    @Override
    public List<String> getUnsupportedOptions() {
        return Collections.emptyList();
    }


    @Override
    public boolean isSendNativeVersionInPing() {
        return this.sendNativeVersionInPing;
    }

    @Override
    public int getNativeVersion() {
        return this.nativeVersion;
    }
}
