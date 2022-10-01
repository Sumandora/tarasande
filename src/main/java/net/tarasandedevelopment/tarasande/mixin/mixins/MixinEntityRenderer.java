package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventDisplayName;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"))
    public Text hookedGetDisplayName(Entity instance) {
        EventDisplayName eventDisplayName = new EventDisplayName(instance, instance.getDisplayName());
        TarasandeMain.Companion.get().getManagerEvent().call(eventDisplayName);
        return eventDisplayName.getDisplayName();
    }

}
