package su.mandora.tarasande.mixin.accessor;

import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;

public interface IClientPlayerInteractionManager {
    boolean tarasande_getOnlyPackets();

    void tarasande_setOnlyPackets(boolean onlyPackets);

    float tarasande_getCurrentBreakingProgress();

    void tarasande_setCurrentBreakingProgress(float currentBreakingProgress);

    void tarasande_invokeSendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);
}
