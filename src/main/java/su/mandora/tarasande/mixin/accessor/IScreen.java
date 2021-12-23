package su.mandora.tarasande.mixin.accessor;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Selectable;

import java.util.List;

public interface IScreen {
    List<Drawable> getDrawables();

    List<Selectable> getSelectables();
}
