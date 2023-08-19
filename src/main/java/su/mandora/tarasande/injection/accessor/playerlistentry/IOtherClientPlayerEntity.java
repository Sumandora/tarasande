package su.mandora.tarasande.injection.accessor.playerlistentry;

import net.minecraft.client.network.PlayerListEntry;

import java.lang.ref.WeakReference;

public interface IOtherClientPlayerEntity {

    WeakReference<PlayerListEntry> tarasande_getPlayerListEntry();
    void tarasande_setPlayerListEntry(WeakReference<PlayerListEntry> entry);

}
