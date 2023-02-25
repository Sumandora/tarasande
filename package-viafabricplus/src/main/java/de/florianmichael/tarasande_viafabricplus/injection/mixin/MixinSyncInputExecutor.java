package de.florianmichael.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.tarasande_viafabricplus.injection.accessor.IEventScreenInput;
import de.florianmichael.viafabricplus.definition.v1_12_2.SyncInputExecutor;
import net.tarasandedevelopment.tarasande.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.impl.EventScreenInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.ConcurrentLinkedDeque;

@Mixin(value = SyncInputExecutor.class, remap = false)
public class MixinSyncInputExecutor {

    @Shadow @Final private static ConcurrentLinkedDeque<Runnable> mouseInteractions;

    @Shadow @Final private static ConcurrentLinkedDeque<Runnable> keyboardInteractions;

    /**
     * @author FlorianMichael
     * @reason hook EventScreenInput
     */
    @Overwrite
    public static void callback() {
        while (!mouseInteractions.isEmpty()) {
            mouseInteractions.poll().run();
        }

        EventScreenInput eventScreenInput = new EventScreenInput(false);
        ((IEventScreenInput) (Object) eventScreenInput).setOriginal(false);
        EventDispatcher.INSTANCE.call(eventScreenInput);

        while (!keyboardInteractions.isEmpty()) {
            keyboardInteractions.poll().run();
        }
    }
}
