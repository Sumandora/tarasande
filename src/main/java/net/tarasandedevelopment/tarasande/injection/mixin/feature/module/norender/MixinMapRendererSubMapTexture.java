package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import net.tarasandedevelopment.tarasande.util.dummy.IteratorDummy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.render.MapRenderer$MapTexture")
public class MixinMapRendererSubMapTexture {

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapState;getIcons()Ljava/lang/Iterable;"))
    public Iterable<MapIcon> noRender_draw(MapState instance) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getWorld().getMapMarkers().should()) {
            return IteratorDummy::new;
        }
        return instance.getIcons();
    }
}
