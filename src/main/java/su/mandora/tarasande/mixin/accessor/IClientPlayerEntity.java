package su.mandora.tarasande.mixin.accessor;

public interface IClientPlayerEntity {

    float tarasande_getLastYaw();

    float tarasande_getLastPitch();

    boolean tarasande_getBypassChat();
    void tarasande_setBypassChat(boolean bypassChat);

}
