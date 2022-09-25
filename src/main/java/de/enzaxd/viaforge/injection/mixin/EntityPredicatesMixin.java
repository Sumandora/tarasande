package de.enzaxd.viaforge.injection.mixin;

import de.enzaxd.viaforge.equals.ProtocolEquals;
import de.enzaxd.viaforge.equals.VersionList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPredicates.class)
public class EntityPredicatesMixin {

    @SuppressWarnings("target")
    @Redirect(method = "method_5915(Lnet/minecraft/entity/Entity;Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/scoreboard/AbstractTeam$CollisionRule;Lnet/minecraft/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isMainPlayer()Z"))
    private static boolean makeMainPlayerUnpushable(PlayerEntity player) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_8))
            return false;
        return player.isMainPlayer();
    }
}
