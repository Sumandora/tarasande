package de.florianmichael.tarasande_custom_minecraft.injection.mixin.detailedconnectionstatus;

import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.debug.ConnectionState;
import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.debug.DetailedConnectionStatus;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLoginNetworkHandler.class)
public class MixinClientLoginNetworkHandler {

    @Inject(method = "joinServerSession", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/minecraft/MinecraftSessionService;joinServer(Lcom/mojang/authlib/GameProfile;Ljava/lang/String;Ljava/lang/String;)V"))
    public void verifyingSession(String serverId, CallbackInfoReturnable<Text> cir) {
        DetailedConnectionStatus.INSTANCE.setConnectionState(ConnectionState.VERIFYING_SESSION);
    }

    @Inject(method = "onSuccess", at = @At(value = "HEAD"))
    public void success(LoginSuccessS2CPacket packet, CallbackInfo ci) {
        DetailedConnectionStatus.INSTANCE.setConnectionState(ConnectionState.SUCCESS);
    }
}
