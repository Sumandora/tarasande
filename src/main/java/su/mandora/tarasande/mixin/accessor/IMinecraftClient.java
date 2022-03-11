package su.mandora.tarasande.mixin.accessor;

import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Session;

public interface IMinecraftClient {
    void setSession(Session session);

    int getAttackCooldown();

    void setAttackCooldown(int attackCooldown);

    void invokeDoItemUse();

    void invokeDoAttack();

    RenderTickCounter getRenderTickCounter();

    void invokeHandleBlockBreaking(boolean bl);

    int getCurrentFPS();
}
