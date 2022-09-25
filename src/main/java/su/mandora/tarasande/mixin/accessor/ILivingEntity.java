package su.mandora.tarasande.mixin.accessor;

import net.minecraft.item.ItemStack;

public interface ILivingEntity {
    double tarasande_getServerX();

    double tarasande_getServerY();

    double tarasande_getServerZ();

    double tarasande_getServerYaw();

    double tarasande_getServerPitch();

    int tarasande_getBodyTrackingIncrements();

    void tarasande_setBodyTrackingIncrements(int value);

    int tarasande_getLastAttackedTicks();

    void tarasande_setLastAttackedTicks(int lastAttackedTicks);

    void tarasande_invokeTickItemStackUsage(ItemStack itemStack);
}
