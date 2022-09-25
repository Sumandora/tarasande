package de.enzaxd.viaforge.injection.mixin;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.enzaxd.viaforge.ViaForge;
import de.enzaxd.viaforge.equals.VersionList;
import de.enzaxd.viaforge.gui.DropboxWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Collectors;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {

    @Unique
    private DropboxWidget protocolSelector;

    public MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void hookProtocolSelectorInit(CallbackInfo ci) {
        int i = 0;
        for (ProtocolVersion protocol : VersionList.getProtocols()) {
            if (protocol.getVersion() == ViaForge.CURRENT_VERSION)
                break;
            i++;
        }

        this.protocolSelector = new DropboxWidget(5, 5, 98, 10, 10, i, VersionList.getProtocols().stream().map(ProtocolVersion::getName).collect(Collectors.toList()));
        this.protocolSelector.setClickAction(() -> ViaForge.CURRENT_VERSION = VersionList.getProtocols().get(this.protocolSelector.selected).getVersion());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.protocolSelector.mouseClicked(mouseX, mouseY);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void renderProtocolSelector(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.protocolSelector != null)
            this.protocolSelector.render(matrices);
    }
}
