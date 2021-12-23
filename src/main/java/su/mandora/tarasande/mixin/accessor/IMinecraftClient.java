package su.mandora.tarasande.mixin.accessor;

import net.minecraft.client.util.Session;

public interface IMinecraftClient {
    void setSession(Session session);

    int getCurrentFps();

    int getAttackCooldown();

    void setAttackCooldown(int attackCooldown);

    void invokeDoItemUse();

    void invokeDoAttack();
}
