package de.florianmichael.viabeta;

import com.viaversion.viaversion.util.Config;
import de.florianmichael.viabeta.base.ViaBetaConfig;

import java.io.File;
import java.net.URL;
import java.util.*;

public class ViaBetaConfigImpl extends Config implements ViaBetaConfig {

    private boolean dynamicOnground;
    private boolean ignoreLongChannelNames;
    private boolean legacySkullLoading;
    private boolean legacySkinLoading;
    private boolean soundEmulation;
    private boolean oldBiomes;
    private boolean remapBasedOnColor;
    private int classicChunkRange;
    private int chunksPerTick;

    public ViaBetaConfigImpl(final File configFile) {
        super(configFile);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.loadFields();
    }

    private void loadFields() {
        this.dynamicOnground = this.getBoolean("dynamic-onground", true);
        this.ignoreLongChannelNames = this.getBoolean("ignore-long-1_8-channel-names", true);
        this.legacySkullLoading = this.getBoolean("legacy-skull-loading", true);
        this.legacySkinLoading = this.getBoolean("legacy-skin-loading", false);
        this.soundEmulation = this.getBoolean("sound-emulation", true);
        this.oldBiomes = this.getBoolean("old-biomes", true);
        this.remapBasedOnColor = this.getBoolean("remap-based-on-color", true);
        this.classicChunkRange = this.getInt("classic-chunk-range", 10);
        this.chunksPerTick = this.getInt("chunks-per-tick", -1);
    }

    @Override
    public URL getDefaultConfigURL() {
        return this.getClass().getClassLoader().getResource("assets/viabeta/viabeta.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> map) {
    }

    @Override
    public List<String> getUnsupportedOptions() {
        return Collections.emptyList();
    }

    @Override
    public boolean isDynamicOnground() {
        return this.dynamicOnground;
    }

    @Override
    public boolean isIgnoreLong1_8ChannelNames() {
        return this.ignoreLongChannelNames;
    }

    @Override
    public boolean isLegacySkullLoading() {
        return this.legacySkullLoading;
    }

    @Override
    public boolean isLegacySkinLoading() {
        return this.legacySkinLoading;
    }

    @Override
    public boolean isSoundEmulation() {
        return this.soundEmulation;
    }

    @Override
    public boolean isOldBiomes() {
        return this.oldBiomes;
    }

    @Override
    public boolean isRemapBasedOnColor() {
        return this.remapBasedOnColor;
    }

    @Override
    public int getClassicChunkRange() {
        return this.classicChunkRange;
    }

    @Override
    public int getChunksPerTick() {
        return this.chunksPerTick;
    }

}
