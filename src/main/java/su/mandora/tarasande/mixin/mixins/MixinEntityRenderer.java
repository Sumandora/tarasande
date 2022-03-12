package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventTagName;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"))
    public Text hookedGetDisplayName(Entity instance) {
        EventTagName eventTagName = new EventTagName(instance, instance.getDisplayName());
        TarasandeMain.Companion.get().getManagerEvent().call(eventTagName);
        return eventTagName.getDisplayName();
    }

}
