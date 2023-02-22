package net.tarasandedevelopment.tarasande.injection.mixin.event;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.impl.EventPlayerListName;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"))
    public Text hookEventPlayerListName(PlayerListHud instance, PlayerListEntry entry) {
        EventPlayerListName eventPlayerListName = new EventPlayerListName(entry, instance.getPlayerName(entry));
        EventDispatcher.INSTANCE.call(eventPlayerListName);
        return eventPlayerListName.getDisplayName();
    }

}
