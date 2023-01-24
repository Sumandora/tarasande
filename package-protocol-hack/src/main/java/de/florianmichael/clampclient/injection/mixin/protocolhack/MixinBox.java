package de.florianmichael.clampclient.injection.mixin.protocolhack;

import de.florianmichael.clampclient.injection.instrumentation_1_8.definition.MathHelper_1_8;
import de.florianmichael.clampclient.injection.mixininterface.IBox_Protocol;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "SimplifiableConditionalExpression", "DuplicatedCode"})
@Mixin(Box.class)
public class MixinBox implements IBox_Protocol {

    @Shadow @Final public double minX;

    @Shadow @Final public double maxX;

    @Shadow @Final public double minY;

    @Shadow @Final public double maxY;

    @Shadow @Final public double minZ;

    @Shadow @Final public double maxZ;

    @Override
    public HitResult protocolhack_calculateIntercept(Vec3d vecA, Vec3d vecB) {
        Vec3d vec3 = MathHelper_1_8.getIntermediateWithXValue(vecA, vecB, this.minX);
        Vec3d vec31 = MathHelper_1_8.getIntermediateWithXValue(vecA, vecB, this.maxX);
        Vec3d vec32 = MathHelper_1_8.getIntermediateWithYValue(vecA, vecB, this.minY);
        Vec3d vec33 = MathHelper_1_8.getIntermediateWithYValue(vecA, vecB, this.maxY);
        Vec3d vec34 = MathHelper_1_8.getIntermediateWithZValue(vecA, vecB, this.minZ);
        Vec3d vec35 = MathHelper_1_8.getIntermediateWithZValue(vecA, vecB, this.maxZ);

        if (!this.protocolhack_isVecInYZ(vec3)) vec3 = null;
        if (!this.protocolhack_isVecInYZ(vec31)) vec31 = null;
        if (!this.protocolhack_isVecInXZ(vec32)) vec32 = null;
        if (!this.protocolhack_isVecInXZ(vec33)) vec33 = null;
        if (!this.protocolhack_isVecInXY(vec34)) vec34 = null;
        if (!this.protocolhack_isVecInXY(vec35)) vec35 = null;

        Vec3d vec36 = null;

        if (vec3 != null) vec36 = vec3;


        if (vec31 != null && (vec36 == null || vecA.squaredDistanceTo(vec31) < vecA.squaredDistanceTo(vec36))) vec36 = vec31;
        if (vec32 != null && (vec36 == null || vecA.squaredDistanceTo(vec32) < vecA.squaredDistanceTo(vec36))) vec36 = vec32;
        if (vec33 != null && (vec36 == null || vecA.squaredDistanceTo(vec33) < vecA.squaredDistanceTo(vec36))) vec36 = vec33;
        if (vec34 != null && (vec36 == null || vecA.squaredDistanceTo(vec34) < vecA.squaredDistanceTo(vec36))) vec36 = vec34;
        if (vec35 != null && (vec36 == null || vecA.squaredDistanceTo(vec35) < vecA.squaredDistanceTo(vec36))) vec36 = vec35;


        if (vec36 == null) {
            return null;
        } else {
            Direction enumfacing = null;

            if (vec36 == vec3) {
                enumfacing = Direction.WEST;
            } else if (vec36 == vec31) {
                enumfacing = Direction.EAST;
            } else if (vec36 == vec32) {
                enumfacing = Direction.DOWN;
            } else if (vec36 == vec33) {
                enumfacing = Direction.UP;
            } else if (vec36 == vec34) {
                enumfacing = Direction.NORTH;
            } else {
                enumfacing = Direction.SOUTH;
            }

            return new BlockHitResult(vec36, enumfacing, BlockPos.ORIGIN, false);
        }
    }

    @Unique
    private boolean protocolhack_isVecInYZ(Vec3d vec) {
        return vec == null ? false : vec.y >= this.minY && vec.y <= this.maxY && vec.z >= this.minZ && vec.z <= this.maxZ;
    }

    @Unique
    private boolean protocolhack_isVecInXZ(Vec3d vec) {
        return vec == null ? false : vec.x >= this.minX && vec.x <= this.maxX && vec.z >= this.minZ && vec.z <= this.maxZ;
    }

    @Unique
    private boolean protocolhack_isVecInXY(Vec3d vec) {
        return vec == null ? false : vec.x >= this.minX && vec.x <= this.maxX && vec.y >= this.minY && vec.y <= this.maxY;
    }
}
