package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues;

@Mixin(HandledScreen.class)
public class MixinHandledScreen extends Screen {

    protected MixinHandledScreen(Text title) {
        super(title);
    }

    @Inject(method = "drawSlot", at = @At("TAIL"))
    public void visualizeId(DrawContext context, Slot slot, CallbackInfo ci) {
        if (DebugValues.INSTANCE.getVisualizeSlotIds().getValue()) {
            context.drawText(textRenderer, String.valueOf(slot.id), slot.x, slot.y, -1, false);
            context.drawText(textRenderer, String.valueOf(slot.getIndex()), slot.x, slot.y + textRenderer.fontHeight, -1, false);
        }
    }

}
