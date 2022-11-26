package net.tarasandedevelopment.tarasande.injection.accessor;

import net.minecraft.client.network.ServerInfo;

import java.util.function.Consumer;

public interface IMultiplayerServerListWidgetSubServerEntry {

    void tarasande_setCompletionConsumer(Consumer<ServerInfo> consumer);

}
