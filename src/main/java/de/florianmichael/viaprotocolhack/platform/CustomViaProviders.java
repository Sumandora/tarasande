package de.florianmichael.viaprotocolhack.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;

public class CustomViaProviders implements ViaPlatformLoader {

    @Override
    public void load() {
        // Now, we can implement custom providers
        ViaProtocolHack.instance().provider().createProviders(Via.getManager().getProviders());
    }

    @Override
    public void unload() {
        // Nothing to do
    }
}
