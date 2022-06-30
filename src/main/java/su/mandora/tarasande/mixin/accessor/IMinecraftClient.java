package su.mandora.tarasande.mixin.accessor;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.Session;
import net.minecraft.network.encryption.SignatureVerifier;

public interface IMinecraftClient {
    void tarasande_setSession(Session session);

    int tarasande_getAttackCooldown();

    void tarasande_setAttackCooldown(int attackCooldown);

    void tarasande_invokeDoItemUse();

    void tarasande_invokeDoAttack();

    RenderTickCounter tarasande_getRenderTickCounter();

    void tarasande_invokeHandleBlockBreaking(boolean bl);

    int tarasande_getCurrentFPS();

    void tarasande_setAuthenticationService(YggdrasilAuthenticationService authenticationService);

    void tarasande_setSessionService(MinecraftSessionService sessionService);

    void tarasande_setUserApiService(UserApiService userApiService);

    void tarasande_setServicesSignatureVerifier(SignatureVerifier signatureVerifier);

    void tarasande_setSocialInteractionsManager(SocialInteractionsManager socialInteractionsManager);

    UserApiService tarasande_getUserApiService();

    void tarasande_setProfileKeys(ProfileKeys profileKeys);
}
