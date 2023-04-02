package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.client.gui.screen.ConfirmScreen;
import su.mandora.tarasande.injection.accessor.IConfirmScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

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
