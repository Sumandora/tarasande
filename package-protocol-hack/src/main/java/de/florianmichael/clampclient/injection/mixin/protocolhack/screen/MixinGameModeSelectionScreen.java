package de.florianmichael.clampclient.injection.mixin.protocolhack.screen;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.tarasandedevelopment.tarasande_protocol_hack.TarasandeProtocolHack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameModeSelectionScreen.class)
public class MixinGameModeSelectionScreen {

    @Mutable
    @Shadow @Final private static int UI_WIDTH;

    @Unique
    private GameModeSelectionScreen.GameModeSelection[] protocolhack_unwrappedGameModes;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fixUIWidth(CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThan(VersionListEnum.r1_8)) {
            protocolhack_unwrappedGameModes = TarasandeProtocolHack.Companion.unwrapGameModes(GameModeSelectionScreen.GameModeSelection.values());
            UI_WIDTH = protocolhack_unwrappedGameModes.length * 31 - 5;
        }
    }

    @Redirect(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;VALUES:[Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;"))
    public GameModeSelectionScreen.GameModeSelection[] removeNewerGameModes() {
        if (ViaLoadingBase.getTargetVersion().isOlderThan(VersionListEnum.r1_8)) {
            return protocolhack_unwrappedGameModes;
        }
        return GameModeSelectionScreen.GameModeSelection.values();
    }
}
