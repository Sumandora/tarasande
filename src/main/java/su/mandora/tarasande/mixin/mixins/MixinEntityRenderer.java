package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.util.player.tagname.TagName;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"))
    public Text hookedGetDisplayName(Entity instance) {
        Text tagName = TagName.INSTANCE.getTagName(instance);
        return tagName == null ? instance.getDisplayName() : tagName;
    }

}
