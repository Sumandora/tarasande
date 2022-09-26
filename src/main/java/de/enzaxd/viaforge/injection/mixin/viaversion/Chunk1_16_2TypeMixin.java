package de.enzaxd.viaforge.injection.mixin.viaversion;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.types.Chunk1_16_2Type;
import de.enzaxd.viaforge.util.FullChunkTracker;
import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Chunk1_16_2Type.class)
public class Chunk1_16_2TypeMixin {

    @Inject(method = "read(Lio/netty/buffer/ByteBuf;)Lcom/viaversion/viaversion/api/minecraft/chunks/Chunk;", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/type/types/VarIntType;readPrimitive(Lio/netty/buffer/ByteBuf;)I", ordinal = 0, remap = false), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    public void trackFullChunk(ByteBuf input, CallbackInfoReturnable<Chunk> cir, int chunkX, int chunkZ, boolean fullChunk) {
        FullChunkTracker.track(chunkX, chunkZ, fullChunk);
    }
}
