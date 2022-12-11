package net.lenni0451.mcstructs.text.components;

import net.lenni0451.mcstructs.text.ATextComponent;

import java.util.Objects;
import java.util.function.Function;

public class KeybindComponent extends ATextComponent {

    private final String keybind;
    private Function<String, String> translator = s -> s;

    public KeybindComponent(final String keybind) {
        this.keybind = keybind;
    }

    public String getKeybind() {
        return this.keybind;
    }

    public void setTranslator(Function<String, String> translator) {
        this.translator = translator;
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder(this.translator.apply(this.keybind));
        this.appendSiblings(out);
        return out.toString();
    }

    @Override
    public ATextComponent copy() {
        return this.putMetaCopy(new KeybindComponent(this.keybind));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeybindComponent that = (KeybindComponent) o;
        return Objects.equals(getSiblings(), that.getSiblings()) && Objects.equals(getStyle(), that.getStyle()) && Objects.equals(keybind, that.keybind) && Objects.equals(translator, that.translator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSiblings(), getStyle(), keybind, translator);
    }

    @Override
    public String toString() {
        return "KeybindComponent{" +
                "siblings=" + getSiblings() +
                ", style=" + getStyle() +
                ", keybind='" + keybind + '\'' +
                ", translator=" + translator +
                '}';
    }

}
