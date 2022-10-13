package net.tarasandedevelopment.tarasande.mixin.accessor;

public interface IClientPlayerEntity {

    float tarasande_getLastYaw();

    float tarasande_getLastPitch();

    boolean tarasande_getBypassChat();

    void tarasande_setBypassChat(boolean bypassChat);

    void tarasande_setMountJumpStrength(float jumpPower);

    void tarasande_setField_3938(int jumpPowerCounter);
}
