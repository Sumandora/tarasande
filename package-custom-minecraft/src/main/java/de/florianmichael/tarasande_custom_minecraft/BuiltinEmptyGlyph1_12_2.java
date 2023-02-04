package de.florianmichael.tarasande_custom_minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;

import java.util.function.Function;
import java.util.function.Supplier;

@Environment(value = EnvType.CLIENT)
public enum BuiltinEmptyGlyph1_12_2 implements Glyph {

    VERY_MISSING(() -> BuiltinEmptyGlyph1_12_2.createRectImage((x, y) -> {
        boolean bl = x == 0 || x + 1 == 5 || y == 0 || y + 1 == 8;
        return bl ? -1 : 0;
    }));

    final NativeImage image;

    BuiltinEmptyGlyph1_12_2(Supplier<NativeImage> imageSupplier) {
        this.image = imageSupplier.get();
    }

    private static NativeImage createRectImage(ColorSupplier colorSupplier) {
        final NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, 5, 8, false);

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 5; ++j) {
                nativeImage.setColor(j, i, colorSupplier.getColor(j, i));
            }
        }
        nativeImage.untrack();
        return nativeImage;
    }

    @Override
    public float getAdvance() {
        return this.image.getWidth() + 1;
    }

    @Override
    public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
        return function.apply(new RenderableGlyph(){

            @Override
            public int getWidth() {
                return BuiltinEmptyGlyph1_12_2.this.image.getWidth();
            }

            @Override
            public int getHeight() {
                return BuiltinEmptyGlyph1_12_2.this.image.getHeight();
            }

            @Override
            public float getOversample() {
                return 1.0f;
            }

            @Override
            public void upload(int x, int y) {
                BuiltinEmptyGlyph1_12_2.this.image.upload(0, x, y, false);
            }

            @Override
            public boolean hasColor() {
                return true;
            }
        });
    }


    @FunctionalInterface
    @Environment(value = EnvType.CLIENT)
    interface ColorSupplier {
        int getColor(int var1, int var2);
    }
}
