package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;

@Mixin(Window.class)
public class MixinWindow {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwCreateWindow(IILjava/lang/CharSequence;JJ)J"))
    public long hookedGlfwCreateWindow(int width, int height, CharSequence title, long monitor, long share) {
        return GLFW.glfwCreateWindow(width, height, TarasandeMain.Companion.get().getName() + " | " + title, monitor, share);
    }

    @Redirect(method = "setTitle", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowTitle(JLjava/lang/CharSequence;)V"))
    public void hookedGlfwSetWindowTitle(long window, CharSequence title) {
        GLFW.glfwSetWindowTitle(window, TarasandeMain.Companion.get().getName() + " | " + title);
    }

    @Inject(method = "logGlError", at = @At("TAIL"))
    public void injectLogGlError(int error, long description, CallbackInfo ci) {
        new IllegalStateException().printStackTrace();
    }
}
