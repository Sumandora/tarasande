package su.mandora.tarasande_protocol_spoofer.injection.mixin.resourcepackspoofer;

import net.minecraft.client.gui.screen.ConfirmScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IConfirmScreen;

@Mixin(ConfirmScreen.class)
public class MixinConfirmScreen implements IConfirmScreen {

    @Unique
    private boolean tarasande_resourcePacksScreen = false;

    @Override
    public void tarasande_markAsResourcePacksScreen() {
        this.tarasande_resourcePacksScreen = true;
    }

    @Override
    public boolean tarasande_isResourcePacksScreen() {
        return this.tarasande_resourcePacksScreen;
    }
}
