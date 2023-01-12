package de.florianmichael.clampclient.injection.mixin.protocolhack.screen;

import de.florianmichael.clampclient.injection.instrumentation_1_12.MouseSensitivity_1_12_2;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnnecessaryUnboxing")
@Mixin(SimpleOption.OptionSliderWidgetImpl.class)
public abstract class MixinSimpleOption_OptionSliderWidgetImpl extends OptionSliderWidget {

    @Shadow @Final private SimpleOption<?> option;

    protected MixinSimpleOption_OptionSliderWidgetImpl(GameOptions options, int x, int y, int width, int height, double value) {
        super(options, x, y, width, height, value);
    }

    @Inject(method = "updateMessage", at = @At("RETURN"))
    public void injectUpdateMessage(CallbackInfo ci) {
        if (this.option == MinecraftClient.getInstance().options.getMouseSensitivity()) {
            float approximation = MouseSensitivity_1_12_2.get1_12SensitivityFor1_19(((Double) this.option.value).doubleValue());
            final Text customText = Text.literal(" (" + VersionListEnum.r1_12_2.getName() + ": " + MouseSensitivity_1_12_2.getPercentage(approximation) + "%)").styled(style -> style.withColor(ClientValues.INSTANCE.getAccentColor().getColor().getRGB()));

            this.setMessage(Text.literal("").append(this.getMessage()).append(customText));
        }
    }
}