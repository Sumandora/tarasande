package su.mandora.tarasande_mod_fixes.injection.mixin.modmenu;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Pseudo
@Mixin(targets = "com.terraformersmc.modmenu.event.ModMenuEventHandler", remap = false)
public class MixinModMenuEventHandler {

    @Redirect(method = "afterGameMenuScreenInit", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0))
    private static Object improveGettingCode(List<ClickableWidget> instance, int i) {
        for (Object o : instance) {
            if (o instanceof GridWidget) {
                return o;
            }
        }
        return instance.get(i);
    }
}
