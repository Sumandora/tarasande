package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.util.Pair;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IPacketWrapperImpl_Protocol;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Deque;

@Mixin(value = PacketWrapperImpl.class, remap = false)
public class MixinPacketWrapperImpl implements IPacketWrapperImpl_Protocol {
    @Shadow
    @Final
    private Deque<Pair<Type, Object>> readableObjects;

    @Override
    public Deque<Pair<Type, Object>> protocolhack_getReadableObjects() {
        return readableObjects;
    }
}
