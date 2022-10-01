package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventPlayerListName;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"))
    public Text hookedGetPlayerName(PlayerListHud instance, PlayerListEntry entry) {
        EventPlayerListName eventPlayerListName = new EventPlayerListName(entry, instance.getPlayerName(entry));
        TarasandeMain.Companion.get().getManagerEvent().call(eventPlayerListName);
        return eventPlayerListName.getDisplayName();
    }

}
