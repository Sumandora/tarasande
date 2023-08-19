package su.mandora.tarasande.injection.mixin.core.connection.playerlistentry;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import su.mandora.tarasande.injection.accessor.playerlistentry.IPlayerListEntry;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry implements IPlayerListEntry {

    @Unique
    private final Set<OtherClientPlayerEntity> tarasande_owners = Collections.newSetFromMap(new WeakHashMap<>());


    @Override
    public Set<OtherClientPlayerEntity> tarasande_getOwners() {
        return tarasande_owners;
    }

    @Override
    public void tarasande_addOwners(OtherClientPlayerEntity player) {
        tarasande_owners.add(player);
    }

    @Unique
    private final Set<PlayerListEntry> tarasande_duplicates = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public Set<PlayerListEntry> tarasande_getDuplicates() {
        return tarasande_duplicates;
    }

    @Override
    public void tarasande_addDuplicate(PlayerListEntry entry) {
        tarasande_duplicates.add(entry);
    }

    @Unique
    private boolean tarasande_listed;

    @Override
    public boolean tarasande_isListed() {
        return tarasande_listed;
    }

    @Override
    public void tarasande_setListed(boolean listed) {
        tarasande_listed = listed;
    }

    @Unique
    private boolean tarasande_removed = false;

    @Override
    public boolean tarasande_isRemoved() {
        return tarasande_removed;
    }

    @Override
    public void tarasande_setRemoved(boolean removed) {
        tarasande_removed = removed;
    }
}
