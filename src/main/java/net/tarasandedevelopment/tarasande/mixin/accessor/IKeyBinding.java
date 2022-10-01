package net.tarasandedevelopment.tarasande.mixin.accessor;

import net.minecraft.client.util.InputUtil;

public interface IKeyBinding {
    void tarasande_setTimesPressed(int timesPressed);

    boolean tarasande_forceIsPressed();

    InputUtil.Key tarasande_getBoundKey();

    void tarasande_increaseTimesPressed();
}
