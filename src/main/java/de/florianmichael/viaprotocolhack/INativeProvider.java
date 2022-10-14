package de.florianmichael.viaprotocolhack;

import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viaversion.libs.gson.JsonObject;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface INativeProvider {

    boolean isSinglePlayer();
    int clientsideVersion();
    int realClientsideVersion();
    String[] nettyOrder(); // 0 = decompress, 1 = compress
    File run();
    JsonObject createDump();
    void createProviders(final ViaProviders providers);
    Optional<ViaCommandHandler> commandHandler();
    List<ProtocolVersion> optionalVersions();

}
