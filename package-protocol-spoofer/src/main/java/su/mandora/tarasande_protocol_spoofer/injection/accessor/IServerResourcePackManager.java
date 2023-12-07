package su.mandora.tarasande_protocol_spoofer.injection.accessor;

import net.minecraft.client.resource.server.ServerResourcePackManager;

import java.util.function.Consumer;

public interface IServerResourcePackManager {
    void tarasande_setResourcePackConsumer(Consumer<ServerResourcePackManager.PackEntry> resourcePackConsumer);
}
