package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.protocol.ProtocolManagerImpl;
import kotlin.Pair;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IProtocolManagerImpl_Protocol;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mixin(ProtocolManagerImpl.class)
public class MixinProtocolManagerImpl implements IProtocolManagerImpl_Protocol {

    @Unique
    private final List<Pair<Integer, Protocol<?, ?, ?, ?>>> protocolhack_protocolTracker = new ArrayList<>();

    @Unique
    private List<Pair<Integer, Protocol<?, ?, ?, ?>>> protocolhack_sortedProtocols;

    @Inject(method = "registerProtocol(Lcom/viaversion/viaversion/api/protocol/Protocol;Ljava/util/List;I)V", at = @At("HEAD"), remap = false)
    public void trackProtocol(Protocol<?, ?, ?, ?> protocol, List<Integer> supportedClientVersion, int serverVersion, CallbackInfo ci) {
        this.protocolhack_protocolTracker.add(new Pair<>(serverVersion, protocol));
    }

    @Override
    public List<Pair<Integer, Protocol<?, ?, ?, ?>>> protocolhack_getProtocols() {
        if (this.protocolhack_sortedProtocols == null) {
            this.protocolhack_sortedProtocols = new ArrayList<>();

            for (Integer protocolID : this.protocolhack_protocolTracker.stream().map(Pair::getFirst).sorted(Collections.reverseOrder()).toList()) {
                for (Pair<Integer, Protocol<?, ?, ?, ?>> pair : this.protocolhack_protocolTracker) {
                    if (Objects.equals(pair.component1(), protocolID)) {
                        this.protocolhack_sortedProtocols.add(pair);
                        break;
                    }
                }
            }
        }
        return protocolhack_sortedProtocols;
    }
}
