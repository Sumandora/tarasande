package de.florianmichael.tarasande_protocol_spoofer.mixin.teslaclientfaker;

import de.florianmichael.tarasande_protocol_spoofer.spoofer.EntrySidebarPanelToggleableTeslaClientFaker;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LoginHelloC2SPacket.class)
public class MixinLoginHelloC2SPacket {

    @ModifyConstant(method = "write", constant = @Constant(intValue = 16))
    public int increaseMaxNameLength(int constant) {
        final EntrySidebarPanelToggleableTeslaClientFaker teslaClientFaker = TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableTeslaClientFaker.class);
        if (teslaClientFaker.getState().getValue()) {
            return 32767;
        }
        return constant;
    }

}
