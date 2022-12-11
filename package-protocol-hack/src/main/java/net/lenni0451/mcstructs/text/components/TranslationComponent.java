package net.lenni0451.mcstructs.text.components;

import net.lenni0451.mcstructs.text.ATextComponent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationComponent extends ATextComponent {

    private static final Pattern ARG_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    private final String key;
    private final Object[] args;
    private Function<String, String> translator = s -> s;

    public TranslationComponent(final String key, final List<Object> args) {
        this.key = key;
        this.args = args.toArray();
    }

    public TranslationComponent(final String key, final Object... args) {
        this.key = key;
        this.args = args;
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public void setTranslator(final Function<String, String> translator) {
        this.translator = translator;
    }

    @Override
    public String asString() {
        StringBuilder out = new StringBuilder();

        String translated = this.translator.apply(this.key);
        Matcher matcher = ARG_PATTERN.matcher(translated);
        int argIndex = 0;
        int start = 0;
        while (matcher.find(start)) {
            int matchStart = matcher.start();
            int matchEnd = matcher.end();
            if (matchStart > start) out.append(String.format(translated.substring(start, matchStart)));

            String argType = matcher.group(2);
            String match = translated.substring(matchStart, matchEnd);
            if (argType.equals("%") && match.equals("%%")) {
                out.append("%");
            } else {
                if (!argType.equals("s")) throw new IllegalStateException("Unsupported format: '" + match + "'");
                String rawIndex = matcher.group(1);
                int index;
                if (rawIndex == null) index = argIndex++;
                else index = Integer.parseInt(rawIndex);
                if (index < this.args.length) {
                    Object arg = this.args[index];
                    if (arg instanceof ATextComponent) out.append(((ATextComponent) arg).asString());
                    else if (arg == null) out.append("null");
                    else out.append(arg.toString());
                }
            }
        }
        if (start < translated.length()) out.append(String.format(translated.substring(start)));
        this.appendSiblings(out);
        return out.toString();
    }

    @Override
    public ATextComponent copy() {
        Object[] copyArgs = new Object[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            Object arg = this.args[i];
            if (arg instanceof ATextComponent) copyArgs[i] = ((ATextComponent) arg).copy();
            else copyArgs[i] = arg;
        }
        return this.putMetaCopy(new TranslationComponent(this.key, copyArgs));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationComponent that = (TranslationComponent) o;
        return Objects.equals(getSiblings(), that.getSiblings()) && Objects.equals(getStyle(), that.getStyle()) && Objects.equals(key, that.key) && Arrays.equals(args, that.args) && Objects.equals(translator, that.translator);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getSiblings(), getStyle(), key, translator);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    @Override
    public String toString() {
        return "TranslationComponent{" +
                "siblings=" + getSiblings() +
                ", style=" + getStyle() +
                ", key='" + key + '\'' +
                ", args=" + Arrays.toString(args) +
                ", translator=" + translator +
                '}';
    }

}
