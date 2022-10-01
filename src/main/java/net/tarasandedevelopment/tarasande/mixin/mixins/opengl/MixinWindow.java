package net.tarasandedevelopment.tarasande.mixin.mixins.opengl;

import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Window.class)
public class MixinWindow {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", remap = false))
    public void hookedGlfwWindowHint(int hint, int value) {
        if (hint == 139267 && value == 2) {
            GLFW.glfwWindowHint(hint, 1);
            return;
        }
        if (hint == 139272 && value == 204801)
            return;
        if (hint == 139270 && value == 1)
            return;
        GLFW.glfwWindowHint(hint, value);
    }
}
