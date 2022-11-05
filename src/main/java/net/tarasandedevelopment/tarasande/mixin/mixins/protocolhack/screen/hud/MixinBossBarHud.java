package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.screen.hud;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.gui.hud.BossBarHud$1")
public class MixinBossBarHud {

    @Redirect(method = "updateProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ClientBossBar;setPercent(F)V"))
    public void nullSafety(ClientBossBar instance, float percent) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
            if (instance != null) {
                instance.setPercent(percent);
            }
        }
    }
}
