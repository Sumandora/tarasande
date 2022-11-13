package net.tarasandedevelopment.tarasande.endme;

import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.meta.ValueSpacer;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ADGADGADGADG {
    public static ValueSpacer provide(Object owner, String name) {
        return new ValueSpacer(owner, name, 1.0F, true /* java */) {
            boolean abc = false;

            @Override
            public void onChange() {
                System.out.println("Clicked");
                abc = !abc;
            }

            @Nullable
            @Override
            public Color getColor() {
                if(abc)
                    return Color.red;
                return super.getColor();
            }
        };
    }
}
