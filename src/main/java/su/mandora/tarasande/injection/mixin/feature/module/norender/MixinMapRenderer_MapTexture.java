package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import su.mandora.tarasande.util.dummy.IteratorDummy;

@Mixin(targets = "net.minecraft.client.render.MapRenderer$MapTexture")
public class MixinMapRenderer_MapTexture {

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/map/MapState;getIcons()Ljava/lang/Iterable;"))
    public Iterable<MapIcon> noRender_draw(MapState instance) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getWorld().getMapMarkers().should()) {
            return IteratorDummy::new;
        }
        return instance.getIcons();
    }
}
