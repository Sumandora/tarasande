package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"))
    public Text hookedGetDisplayName(Entity instance) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            Text text = TarasandeMain.Companion.get().getTagName().getTagName(instance);
            if(text != null)
                return text;
        }
        return instance.getDisplayName();
    }

}
