package su.mandora.tarasande.mixin.accessor;

import net.minecraft.text.Text;

public interface IHandledScreen {
    int tarasande_getX();

    int tarasande_getY();

    int tarasande_getBackgroundWidth();

    int tarasande_getBackgroundHeight();

    Text tarasande_getTitle();
}
