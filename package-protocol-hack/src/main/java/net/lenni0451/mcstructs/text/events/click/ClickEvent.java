package net.lenni0451.mcstructs.text.events.click;

import java.util.Objects;

public class ClickEvent {

    private final ClickEventAction action;
    private final String value;

    public ClickEvent(final ClickEventAction action, final String value) {
        this.action = action;
        this.value = value;
    }

    public ClickEventAction getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClickEvent that = (ClickEvent) o;
        return action == that.action && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, value);
    }

    @Override
    public String toString() {
        return "ClickEvent{action=" + action + ", value='" + value + "'}";
    }

}
