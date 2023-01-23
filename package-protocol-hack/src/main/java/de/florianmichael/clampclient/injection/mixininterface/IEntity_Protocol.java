package de.florianmichael.clampclient.injection.mixininterface;

import net.minecraft.util.math.Vec3i;

public interface IEntity_Protocol {

    boolean protocolhack_isOutsideBorder();

    void protocolhack_setOutsideBorder(final boolean outsideBorder);

    boolean protocolhack_isInWeb();

    void protocolhack_setInWeb(final boolean inWeb);

    boolean protocolhack_isInWater();

    void protocolhack_setInWater(final boolean inWater);

    void protocolhack_setAngles(final float yaw, final float pitch);

    Vec3i protocolhack_getServerPos();
}
