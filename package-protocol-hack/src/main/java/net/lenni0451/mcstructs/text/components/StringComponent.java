package net.lenni0451.mcstructs.text.components;

import net.lenni0451.mcstructs.text.ATextComponent;

import java.util.Objects;

public class StringComponent extends ATextComponent {

    private final String text;

    public StringComponent(final String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder(this.text);
        this.appendSiblings(out);
        return out.toString();
    }

    @Override
    public ATextComponent copy() {
        return this.putMetaCopy(new StringComponent(this.text));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringComponent that = (StringComponent) o;
        return Objects.equals(getSiblings(), that.getSiblings()) && Objects.equals(getStyle(), that.getStyle()) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSiblings(), getStyle(), text);
    }

    @Override
    public String toString() {
        return "StringComponent{" +
                "siblings=" + getSiblings() +
                ", style=" + getStyle() +
                ", text='" + text + '\'' +
                '}';
    }

}
