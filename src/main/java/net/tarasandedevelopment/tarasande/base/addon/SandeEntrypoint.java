package net.tarasandedevelopment.tarasande.base.addon;

import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.base.Manager;
import net.tarasandedevelopment.tarasande.base.event.Event;
import net.tarasandedevelopment.tarasande.event.EventLoadManager;

import java.util.List;
import java.util.function.Consumer;

public abstract class SandeEntrypoint {

    public final Consumer<Event> managerConsumer = event -> {
        if (event instanceof EventLoadManager)
            this.onLoadManager(((EventLoadManager) event).getManager());
    };

    public String modId;
    public List<String> modAuthors;
    public String modVersion;

    public abstract void create(final TarasandeMain tarasandeMain);
    public abstract void onLoadManager(final Manager<?> manager);

    public Consumer<Event> defaultEventConsumer() {
        return null;
    }
}
