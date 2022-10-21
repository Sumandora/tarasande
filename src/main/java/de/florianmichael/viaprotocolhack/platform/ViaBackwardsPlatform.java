package de.florianmichael.viaprotocolhack.platform;

import de.florianmichael.viaprotocolhack.ViaProtocolHack;

import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ViaBackwardsPlatform implements com.viaversion.viabackwards.api.ViaBackwardsPlatform {
    private final File file;

    public ViaBackwardsPlatform() {
        this.init(this.file = new File(ViaProtocolHack.instance().directory(), "ViaBackwards"));
    }

    @Override
    public Logger getLogger() {
        return LogManager.getLogManager().getLogger("ViaBackwards");
    }

    @Override
    public boolean isOutdated() {
        return false;
    }

    @Override
    public void disable() {
    }

    @Override
    public File getDataFolder() {
        return new File(this.file, "viabackwards.yml");
    }
}
