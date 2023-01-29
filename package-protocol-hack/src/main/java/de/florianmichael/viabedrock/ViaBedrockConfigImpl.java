package de.florianmichael.viabedrock;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.util.Config;
import de.florianmichael.viabedrock.base.ViaBedrockConfig;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ViaBedrockConfigImpl extends Config implements ViaBedrockConfig {

    private boolean sendNativeVersionInPing;
    private int nativeVersion;

    public ViaBedrockConfigImpl(File configFile) {
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
        return this.getClass().getClassLoader().getResource("assets/viabedrock/viabedrock.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> config) {
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
