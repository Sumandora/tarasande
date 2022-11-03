package net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.util.Pair;

import java.util.Deque;

public interface IPacketWrapperImpl_Protocol {

    Deque<Pair<Type, Object>> protocolhack_getReadableObjects();

}
