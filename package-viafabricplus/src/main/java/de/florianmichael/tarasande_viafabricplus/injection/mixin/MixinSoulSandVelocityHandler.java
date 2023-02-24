package de.florianmichael.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.viafabricplus.definition.v1_14_4.SoulSandVelocityHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.impl.EventVelocityMultiplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SoulSandVelocityHandler.class)
public class MixinSoulSandVelocityHandler {

    /**
     * @author FlorianMichael
     * @reason hook tarasande event
     */
    @Overwrite
    public static void handleVelocity(final Entity entity) {
        final Vec3d velocity = entity.getVelocity();

        double multiplier = 0.4D;

        EventVelocityMultiplier eventVelocityMultiplier = new EventVelocityMultiplier(Blocks.SOUL_SAND, multiplier);
        EventDispatcher.INSTANCE.call(eventVelocityMultiplier);
        if (eventVelocityMultiplier.getDirty()) {
            multiplier = eventVelocityMultiplier.getVelocityMultiplier();
        }

        entity.setVelocity(velocity.getX() * multiplier, velocity.getY(), velocity.getZ() * multiplier);
    }
}
