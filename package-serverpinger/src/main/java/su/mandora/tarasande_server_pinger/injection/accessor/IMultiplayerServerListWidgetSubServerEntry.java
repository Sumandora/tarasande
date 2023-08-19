package su.mandora.tarasande_server_pinger.injection.accessor;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.network.ServerInfo;

public interface IMultiplayerServerListWidgetSubServerEntry {

    void tarasande_setCompletionConsumer(Function1<ServerInfo, Unit> consumer);

}
