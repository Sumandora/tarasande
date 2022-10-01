package net.tarasandedevelopment.tarasande.mixin.accessor;

import java.awt.*;

public interface ITextFieldWidget {
    boolean tarasande_invokeIsEditable();

    void tarasande_setSelecting(boolean selecting);

    void tarasande_eraseOffset(int offset);

    void tarasande_setForceText(String text);

    void tarasande_setColor(Color color);
}
