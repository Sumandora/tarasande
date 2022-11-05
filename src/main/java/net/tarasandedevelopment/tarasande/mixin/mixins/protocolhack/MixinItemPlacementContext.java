package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemPlacementContext.class)
public class MixinItemPlacementContext {

    @Inject(method = "getPlayerLookDirection", at = @At("HEAD"), cancellable = true)
    private void injectGetPlayerLookDirection(CallbackInfoReturnable<Direction> ci) {
        ItemPlacementContext self = (ItemPlacementContext) (Object) this;

        PlayerEntity player = self.getPlayer();
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_12_2) && player != null) {
            BlockPos placementPos = self.getBlockPos();
            // don't center the BlockPos on 1.10 and below
            double blockPosCenterFactor = VersionList.isNewerTo(ProtocolVersion.v1_10) ? 0.5 : 0;

            if (Math.abs(player.getX() - (placementPos.getX() + blockPosCenterFactor)) < 2 && Math.abs(player.getZ() - (placementPos.getZ() + blockPosCenterFactor)) < 2) {
                double eyeY = player.getY() + player.getEyeHeight(player.getPose());

                if (eyeY - placementPos.getY() > 2) {
                    ci.setReturnValue(Direction.DOWN);
                    return;
                }

                if (placementPos.getY() - eyeY > 0) {
                    ci.setReturnValue(Direction.UP);
                    return;
                }
            }

            ci.setReturnValue(player.getHorizontalFacing());
        }
    }
}
