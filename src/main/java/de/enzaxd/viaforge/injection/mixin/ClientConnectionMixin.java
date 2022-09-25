package de.enzaxd.viaforge.injection.mixin;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.enzaxd.viaforge.injection.access.IClientConnection_Protocol;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements IClientConnection_Protocol {

    @Unique
    private UserConnection viaConnection;

    @Override
    public void florianMichael_setViaConnection(UserConnection userConnection) {
        this.viaConnection = userConnection;
    }

    @Override
    public UserConnection florianMichael_getViaConnection() {
        return this.viaConnection;
    }
}
