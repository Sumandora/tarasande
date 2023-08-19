package su.mandora.tarasande.injection.accessor.playerlistentry;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Set;

public interface IPlayerListEntry {

    Set<OtherClientPlayerEntity> tarasande_getOwners();
    void tarasande_addOwners(OtherClientPlayerEntity player);

    Set<PlayerListEntry> tarasande_getDuplicates();
    void tarasande_addDuplicate(PlayerListEntry entry);

    boolean tarasande_isListed();
    void tarasande_setListed(boolean listed);

    boolean tarasande_isRemoved();
    void tarasande_setRemoved(boolean removed);

}
