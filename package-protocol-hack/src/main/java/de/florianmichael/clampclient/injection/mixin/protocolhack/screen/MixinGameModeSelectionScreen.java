package de.florianmichael.clampclient.injection.mixin.protocolhack.screen;

import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.tarasandedevelopment.tarasande_protocol_hack.TarasandeProtocolHack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameModeSelectionScreen.class)
public class MixinGameModeSelectionScreen {

    @Redirect(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;VALUES:[Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;"))
    public GameModeSelectionScreen.GameModeSelection[] removeNewerGameModes() {
        return TarasandeProtocolHack.Companion.unwrapGameModes(GameModeSelectionScreen.GameModeSelection.values());
    }
}
