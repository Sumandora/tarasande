package su.mandora.tarasande.injection.mixin.core;

import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Window.class)
public class MixinWindow {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", remap = false))
    public void disableForwardCompatibility(int hint, int value) { // This makes glLineWidth unavailable and blazes line width is broken
        if(hint == GLFW.GLFW_OPENGL_FORWARD_COMPAT)
            value = GLFW.GLFW_FALSE;
        GLFW.glfwWindowHint(hint, value);
    }

}
