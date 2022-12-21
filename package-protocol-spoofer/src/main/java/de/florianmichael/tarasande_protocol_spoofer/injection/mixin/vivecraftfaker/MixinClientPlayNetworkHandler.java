package de.florianmichael.tarasande_protocol_spoofer.injection.mixin.vivecraftfaker;

import de.florianmichael.tarasande_protocol_spoofer.spoofer.EntrySidebarPanelToggleableVivecraftFaker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    public void sendVersionInfoOnJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        final EntrySidebarPanelToggleableVivecraftFaker vivecraftFaker = TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableVivecraftFaker.class);
        if (vivecraftFaker.getState().getValue()) {
            vivecraftFaker.sendVersionInfo();
        }
    }

    @Inject(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 0, shift = At.Shift.AFTER))
    public void sendVersionInfoOnRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        final EntrySidebarPanelToggleableVivecraftFaker vivecraftFaker = TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableVivecraftFaker.class);
        if (vivecraftFaker.getState().getValue()) {
            vivecraftFaker.sendVersionInfo();
        }
    }

}
