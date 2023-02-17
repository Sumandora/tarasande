package de.florianmichael.tarasande_protocol_hack.injection.mixin.viaversion;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.TabCompleteTracker;
import de.florianmichael.tarasande_protocol_hack.tarasande.event.EventFinishLegacyCompletions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.event.EventDispatcher;

import java.util.ArrayList;

@Mixin(Protocol1_13To1_12_2.class)
public class MixinProtocol1_13to1_12_2 extends AbstractProtocol<ClientboundPackets1_12_1, ClientboundPackets1_13, ServerboundPackets1_12_1, ServerboundPackets1_13> {

    @Inject(method = "registerPackets", at = @At("RETURN"), remap = false)
    public void injectRegisterPackets(CallbackInfo ci) {
        registerClientbound(ClientboundPackets1_12_1.TAB_COMPLETE, ClientboundPackets1_13.TAB_COMPLETE, wrapper -> {
            wrapper.write(Type.VAR_INT, wrapper.user().get(TabCompleteTracker.class).getTransactionId());

            String input = wrapper.user().get(TabCompleteTracker.class).getInput();
            // Start & End
            int index;
            int length;
            // If no input or new word (then it's the start)
            if (input.endsWith(" ") || input.isEmpty()) {
                index = input.length();
                length = 0;
            } else {
                // Otherwise find the last space (+1 as we include it)
                int lastSpace = input.lastIndexOf(' ') + 1;
                index = lastSpace;
                length = input.length() - lastSpace;
            }
            // Write index + length
            wrapper.write(Type.VAR_INT, index);
            wrapper.write(Type.VAR_INT, length);

            final ArrayList<String> tabCompletions = new ArrayList<>();

            int count = wrapper.passthrough(Type.VAR_INT);
            for (int i = 0; i < count; i++) {
                String suggestion = wrapper.read(Type.STRING);
                // If we're at the start then handle removing slash
                if (suggestion.startsWith("/") && index == 0) {
                    suggestion = suggestion.substring(1);
                }
                tabCompletions.add(suggestion);
                wrapper.write(Type.STRING, suggestion);
                wrapper.write(Type.BOOLEAN, false);
            }

            EventDispatcher.INSTANCE.call(new EventFinishLegacyCompletions(tabCompletions));
        }, true);
    }
}
