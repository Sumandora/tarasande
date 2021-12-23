package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.module.render.ModuleESP;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I"))
    public int hookedGetTeamColorValue(Entity entity) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleESP.class).getEnabled())
            return TarasandeMain.Companion.get().getEntityColor().getColor(entity).getRGB();
        return entity.getTeamColorValue();
    }

}
