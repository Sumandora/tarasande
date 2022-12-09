package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.storage.ReceivedMessagesStorage;
import de.florianmichael.clampclient.injection.mixininterface.IReceivedMessagesStorage_Protocol;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ReceivedMessagesStorage.class)
public class MixinReceivedMessagesStorage implements IReceivedMessagesStorage_Protocol {
    @Shadow private PlayerMessageSignature lastSignature;

    @Override
    public PlayerMessageSignature protocolhack_getLastSignature() {
        return this.lastSignature;
    }
}
