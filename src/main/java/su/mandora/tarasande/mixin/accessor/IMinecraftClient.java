package su.mandora.tarasande.mixin.accessor;

import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Session;

public interface IMinecraftClient {
    void tarasande_setSession(Session session);

    int tarasande_getAttackCooldown();

    void tarasande_setAttackCooldown(int attackCooldown);

    void tarasande_invokeDoItemUse();

    void tarasande_invokeDoAttack();

    RenderTickCounter tarasande_getRenderTickCounter();

    void tarasande_invokeHandleBlockBreaking(boolean bl);

    int tarasande_getCurrentFPS();
}
