package su.mandora.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.viafabricplus.definition.v1_12_2.SyncInputExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventScreenInput;
import su.mandora.tarasande_viafabricplus.injection.accessor.IEventScreenInput;

import java.util.concurrent.ConcurrentLinkedDeque;

@Mixin(value = SyncInputExecutor.class, remap = false)
public class MixinSyncInputExecutor {

    @Shadow @Final private static ConcurrentLinkedDeque<Runnable> mouseInteractions;

    @Shadow @Final private static ConcurrentLinkedDeque<Runnable> keyboardInteractions;

    /**
     * @author Johannes
     * @reason hook EventScreenInput
     */
    @Overwrite
    public static void callback() {
        while (!mouseInteractions.isEmpty()) {
            mouseInteractions.poll().run();
        }

        EventScreenInput eventScreenInput = new EventScreenInput(false);
        ((IEventScreenInput) (Object) eventScreenInput).tarasande_setOriginal(false);
        EventDispatcher.INSTANCE.call(eventScreenInput);

        while (!keyboardInteractions.isEmpty()) {
            keyboardInteractions.poll().run();
        }
    }
}
