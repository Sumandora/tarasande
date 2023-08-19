package su.mandora.tarasande.injection.mixin.core.connection.playerlistentry;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import su.mandora.tarasande.injection.accessor.playerlistentry.IOtherClientPlayerEntity;

import java.lang.ref.WeakReference;

@Mixin(OtherClientPlayerEntity.class)
public class MixinOtherClientPlayerEntity implements IOtherClientPlayerEntity {

    @Unique
    private WeakReference<PlayerListEntry> tarasande_playerListEntry;

    @Override
    public WeakReference<PlayerListEntry> tarasande_getPlayerListEntry() {
        return tarasande_playerListEntry;
    }

    @Override
    public void tarasande_setPlayerListEntry(WeakReference<PlayerListEntry> entry) {
        tarasande_playerListEntry = entry;
    }
}
