package de.enzaxd.viaforge.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import de.enzaxd.viaforge.util.FabricHandItemProvider;
import su.mandora.tarasande.TarasandeMain;

public class ProviderLoader implements ViaPlatformLoader {

    @Override
    public void load() {
        Via.getManager().getProviders().use(VersionProvider.class, new BaseVersionProvider() {
            @Override
            public int getClosestServerProtocol(UserConnection connection) throws Exception {
                if (connection.isClientSide())
                    return TarasandeMain.Companion.get().getProtocolHack().getVersion();
                return super.getClosestServerProtocol(connection);
            }
        });

        Via.getManager().getProviders().use(HandItemProvider.class, new FabricHandItemProvider());
    }

    @Override
    public void unload() {
    }
}