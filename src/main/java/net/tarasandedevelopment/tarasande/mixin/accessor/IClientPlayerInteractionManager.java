package net.tarasandedevelopment.tarasande.mixin.accessor;

import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;

public interface IClientPlayerInteractionManager {

    float tarasande_getCurrentBreakingProgress();

    void tarasande_setCurrentBreakingProgress(float currentBreakingProgress);

    void tarasande_invokeSendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);
}
