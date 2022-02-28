package su.mandora.tarasande.mixin.accessor;

public interface ILivingEntity {
    double getServerX();

    double getServerY();

    double getServerZ();

    double getServerYaw();

    double getServerPitch();

    int getBodyTrackingIncrements();

    void setBodyTrackingIncrements(int value);

    int getLastAttackedTicks();

    void setLastAttackedTicks(int lastAttackedTicks);
}
