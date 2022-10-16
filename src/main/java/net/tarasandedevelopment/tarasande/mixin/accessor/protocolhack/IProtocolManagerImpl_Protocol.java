package net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack;

import com.viaversion.viaversion.api.protocol.Protocol;
import kotlin.Pair;

import java.util.List;

public interface IProtocolManagerImpl_Protocol {

    List<Pair<Integer, Protocol<?, ?, ?, ?>>> getProtocols();

}
