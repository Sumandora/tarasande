package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.module.misc.ModuleFurnaceProgress;
import de.florianmichael.tarasande.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.screen.menu.panel.Panel;
import su.mandora.tarasande.screen.widget.panel.ClickableWidgetPanel;

@Mixin(AbstractFurnaceScreen.class)
public abstract class MixinAbstractFurnaceScreen<T extends AbstractFurnaceScreenHandler> extends HandledScreen<T> {

    ClickableWidgetPanel clickableWidgetPanel;

    public MixinAbstractFurnaceScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void hookCustomWidget(CallbackInfo ci) {
        if (!TarasandeMain.Companion.get().getManagerModule().get(ModuleFurnaceProgress.class).getEnabled()) return;
        final var font = MinecraftClient.getInstance().textRenderer;

        this.addDrawableChild(clickableWidgetPanel = new ClickableWidgetPanel(new Panel("Furnace Progress", 0, 0, 0, 0, null, null, true) {
            @Override
            public void init() {
                super.init();
                final var maxHeight = (2.0 + 1.0) /* Element Size + Bar Height as Double because Kotlin */ * (font.fontHeight + 2);

                this.setX(5);
                this.setY(height / 2F - maxHeight / 2F);

                this.setPanelWidth(100);
                this.setPanelHeight(maxHeight);
            }

            @Override
            public void renderContent(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                matrices.push();
                matrices.translate(this.getX() + 2, this.getY() + font.fontHeight + 4, 0);
                RenderUtil.INSTANCE.useMyStack(matrices);

                if (handler.isBurning()) {
                    // 23 = max
                    var progress = 23 - handler.getCookProgress();

                    var width = RenderUtil.INSTANCE.text("Item smelting finished in: " + ((progress / 2) + 1) + " seconds", 0, 0);
                    this.setPanelWidth(width + 2);

                    RenderUtil.INSTANCE.text("Fuel Power ends in: " + (handler.getFuelProgress() + 1) + " seconds", 0, font.fontHeight + 2);
                } else {
                    RenderUtil.INSTANCE.text("Waiting...", 0, 0);
                    this.setPanelWidth(100);
                }

                RenderUtil.INSTANCE.ourStack();
                matrices.pop();
            }
        }));
    }

    @Inject(method = "handledScreenTick", at = @At("HEAD"))
    public void injectHandledScreenTick(CallbackInfo ci) {
        if (this.clickableWidgetPanel == null) return;

        this.clickableWidgetPanel.tick();
    }
}
