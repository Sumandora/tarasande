package su.mandora.tarasande.mixin.mixins.backport;

import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Window.class)
public class MixinWindow {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V"))
    public void hookedGlfwWindowHint(int hint, int value) {
        if (hint == 139265 && value == 196609)
            GLFW.glfwWindowHint(139265, 196609);
        if (hint == 139275 && value == 221185)
            GLFW.glfwWindowHint(139275, 221185);
        if (hint == 139266 && value == 3)
            GLFW.glfwWindowHint(139266, 2);
        if (hint == 139267 && value == 2)
            GLFW.glfwWindowHint(139267, 0);
        if (hint == 139272 && value == 204801)
            GLFW.glfwWindowHint(139272, 0);
        if (hint == 139270 && value == 1)
            return; // already done
    }
}
