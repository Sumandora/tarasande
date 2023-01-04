package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets.WorldPackets;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.WorldHeightInjection_C_0_30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldPackets.class, remap = false)
public abstract class MixinWorldPackets1_17 {

    @Redirect(method = "register", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/protocol1_17to1_16_4/Protocol1_17To1_16_4;registerClientbound(Lcom/viaversion/viaversion/api/protocol/packet/ClientboundPacketType;Lcom/viaversion/viaversion/api/protocol/remapper/PacketRemapper;)V"))
    private static void handleClassicWorldHeight(Protocol1_17To1_16_4 instance, ClientboundPacketType packetType, PacketRemapper packetRemapper) {
        if (packetType == ClientboundPackets1_16_2.CHUNK_DATA) {
            packetRemapper = WorldHeightInjection_C_0_30.INSTANCE.handleChunkData(packetRemapper);
        }
        if (packetType == ClientboundPackets1_16_2.UPDATE_LIGHT) {
            packetRemapper = WorldHeightInjection_C_0_30.INSTANCE.handleUpdateLight(packetRemapper);
        }

        ((Protocol) instance).registerClientbound(packetType, packetRemapper);
    }
}
