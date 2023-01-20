package de.florianmichael.clampclient.injection.instrumentation_1_12_2;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.GlfwUtil;

@SuppressWarnings({"DataFlowIssue", "UnnecessaryUnboxing"})
public class MouseEmulation_1_12_2 {

    private final Mouse original;

    public MouseEmulation_1_12_2(final Mouse original) {
        this.original = original;
    }

    private final MouseFilter_1_12_2 mouseFilterXAxis = new MouseFilter_1_12_2();
    private final MouseFilter_1_12_2 mouseFilterYAxis = new MouseFilter_1_12_2();

    private float smoothCamFilterX;
    private float smoothCamYaw;
    private float smoothCamPitch;
    private float smoothCamPartialTicks;
    private float smoothCamFilterY;

    public void updateMouse() {
        float tickDelta = MinecraftClient.getInstance().getTickDelta();
        double d = GlfwUtil.getTime();
        double e = d - original.lastMouseUpdateTime;
        original.lastMouseUpdateTime = d;
        if (!original.isCursorLocked() || !MinecraftClient.getInstance().isWindowFocused()) {
            original.cursorDeltaX = 0.0;
            original.cursorDeltaY = 0.0;
            return;
        }
        float f = MouseSensitivity_1_12_2.get1_12SensitivityFor1_19(MinecraftClient.getInstance().options.getMouseSensitivity().getValue().doubleValue()) * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;
        float f2 = ((int) original.cursorDeltaX) * f1;
        float f3 = ((int) original.cursorDeltaY) * f1;

        original.cursorDeltaX = 0.0;
        original.cursorDeltaY = 0.0;

        byte b0 = -1;
        if (MinecraftClient.getInstance().options.getInvertYMouse().getValue())
            b0 = 1;
        if (MinecraftClient.getInstance().options.smoothCameraEnabled) {
            this.smoothCamYaw += f2;
            this.smoothCamPitch += f3;
            float f4 = tickDelta - this.smoothCamPartialTicks;
            this.smoothCamPartialTicks = tickDelta;
            f2 = this.smoothCamFilterX * f4;
            f3 = this.smoothCamFilterY * f4;

            UnboxedEntity.setAngles(MinecraftClient.getInstance().player, f2, f3 * b0);
        } else {
            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;

            UnboxedEntity.setAngles(MinecraftClient.getInstance().player, f2, f3 * b0);
        }
    }

    public void tickFilter() {
        if (MinecraftClient.getInstance().options.smoothCameraEnabled) {
            float f = MouseSensitivity_1_12_2.get1_12SensitivityFor1_19(MinecraftClient.getInstance().options.getMouseSensitivity().getValue().doubleValue()) * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f1);
            this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f1);
            this.smoothCamPartialTicks = 0.0F;
            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;
        } else {
            this.smoothCamFilterX = 0.0F;
            this.smoothCamFilterY = 0.0F;

            this.mouseFilterXAxis.reset();
            this.mouseFilterYAxis.reset();
        }
    }
}
