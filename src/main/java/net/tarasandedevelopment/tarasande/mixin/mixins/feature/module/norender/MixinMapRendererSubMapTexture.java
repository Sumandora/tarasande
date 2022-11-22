package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.norender;

import net.minecraft.client.render.MapRenderer;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleNoRender;
import net.tarasandedevelopment.tarasande.util.dummy.IteratorDummy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapRenderer.MapTexture.class)
public class MixinMapRendererSubMapTexture {

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapState;getIcons()Ljava/lang/Iterable;"))
    public Iterable<MapIcon> noRender_draw(MapState instance) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getWorld().getMapMarkers().should()) {
            return IteratorDummy::new;
        }
        return instance.getIcons();
    }
}
