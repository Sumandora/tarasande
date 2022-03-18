package su.mandora.tarasande.mixin.accessor;

import net.minecraft.client.util.InputUtil;

public interface IKeyBinding {
    void setTimesPressed(int timesPressed);
    boolean forceIsPressed();
    InputUtil.Key getBoundKey();
}
