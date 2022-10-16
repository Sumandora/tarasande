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

import java.util.*;

@Mixin(ProtocolManagerImpl.class)
public class MixinProtocolManagerImpl implements IProtocolManagerImpl_Protocol {

    @Unique
    private final List<Pair<Integer, Protocol<?, ?, ?, ?>>> protocolTracker = new ArrayList<>();

    @Unique
    private List<Pair<Integer, Protocol<?, ?, ?, ?>>> sortedProtocols;

    @Inject(method = "registerProtocol(Lcom/viaversion/viaversion/api/protocol/Protocol;Ljava/util/List;I)V", at = @At("HEAD"), remap = false)
    public void trackProtocol(Protocol<?, ?, ?, ?> protocol, List<Integer> supportedClientVersion, int serverVersion, CallbackInfo ci) {
        this.protocolTracker.add(new Pair<>(serverVersion, protocol));
    }

    @Override
    public List<Pair<Integer, Protocol<?, ?, ?, ?>>> getProtocols() {
        if (this.sortedProtocols == null) {
            this.sortedProtocols = new ArrayList<>();

            for (Integer protocolID : this.protocolTracker.stream().map(Pair::getFirst).sorted(Collections.reverseOrder()).toList()) {
                for (Pair<Integer, Protocol<?, ?, ?, ?>> pair : this.protocolTracker) {
                    if (Objects.equals(pair.component1(), protocolID)) {
                        this.sortedProtocols.add(pair);
                        break;
                    }
                }
            }
        }
        return sortedProtocols;
    }
}
