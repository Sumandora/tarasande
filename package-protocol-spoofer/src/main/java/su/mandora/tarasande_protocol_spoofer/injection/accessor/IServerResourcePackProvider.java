package su.mandora.tarasande_protocol_spoofer.injection.accessor;

import net.minecraft.resource.ResourcePackProfile;

import java.util.function.Consumer;

public interface IServerResourcePackProvider {
    void tarasande_setResourcePackConsumer(Consumer<ResourcePackProfile> resourcePackConsumer);
}
