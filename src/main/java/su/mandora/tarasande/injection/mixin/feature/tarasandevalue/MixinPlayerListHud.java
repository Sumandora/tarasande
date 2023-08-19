package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues;

import java.util.Collection;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Redirect(method = "collectPlayerEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;getListedPlayerListEntries()Ljava/util/Collection;"))
    public Collection<PlayerListEntry> getAllPlayerEntries(ClientPlayNetworkHandler instance) {
        if (DebugValues.INSTANCE.getShowAllPlayerEntries().getValue())
            return instance.getPlayerList();
        return instance.getListedPlayerListEntries();
    }

}
