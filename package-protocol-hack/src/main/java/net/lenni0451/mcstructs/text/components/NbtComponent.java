package net.lenni0451.mcstructs.text.components;

import net.lenni0451.mcstructs.text.ATextComponent;

public abstract class NbtComponent extends ATextComponent {

    private final String component;
    private final boolean resolve;
    private final ATextComponent separator;

    public NbtComponent(final String component, final boolean resolve, final ATextComponent separator) {
        this.component = component;
        this.resolve = resolve;
        this.separator = separator;
    }

    public String getComponent() {
        return this.component;
    }

    public boolean isResolve() {
        return this.resolve;
    }

    public ATextComponent getSeparator() {
        return this.separator;
    }

    @Override
    public String asString() {
        return "";
    }

}
