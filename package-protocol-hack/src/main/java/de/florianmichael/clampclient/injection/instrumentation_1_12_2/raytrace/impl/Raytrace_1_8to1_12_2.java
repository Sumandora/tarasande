package de.florianmichael.clampclient.injection.instrumentation_1_12_2.raytrace.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import de.florianmichael.clampclient.injection.instrumentation_1_12_2.model.ViaRaytraceResult;
import de.florianmichael.clampclient.injection.instrumentation_1_12_2.raytrace.RaytraceBase;
import de.florianmichael.clampclient.injection.mixininterface.IBox_Protocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.tarasandedevelopment.tarasande.injection.accessor.IGameRenderer;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleNoMiningTrace;

import java.util.List;

public class Raytrace_1_8to1_12_2 extends RaytraceBase {
    public static final Raytrace_1_8to1_12_2 SELF = new Raytrace_1_8to1_12_2();

    @Override
    public Vec3d getPositionEyes(Entity entity, float partialTicks) {
        if (partialTicks == 1.0F) {
            return new Vec3d(entity.getX(), entity.getY() + (double)entity.getStandingEyeHeight(), entity.getZ());
        } else {
            double d0 = entity.prevX + (entity.getX() - entity.prevX) * (double)partialTicks;
            double d1 = entity.prevY + (entity.getY() - entity.prevY) * (double)partialTicks + (double)entity.getStandingEyeHeight();
            double d2 = entity.prevZ + (entity.getZ() - entity.prevZ) * (double)partialTicks;
            return new Vec3d(d0, d1, d2);
        }
    }

    public HitResult raycast(Entity entity, Vec3d polar, double maxDistance, float tickDelta, boolean includeFluids) {
        Vec3d vec3d = entity.getCameraPosVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(polar.x * maxDistance, polar.y * maxDistance, polar.z * maxDistance);
        return entity.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity));
    }

    @Override
    public ViaRaytraceResult raytrace(Entity entity, float prevYaw, float prevPitch, float yaw, float pitch, float partialTicks) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final IGameRenderer gameRendererAccessor = (IGameRenderer) mc.gameRenderer;

        Entity pointedEntity = null;
        HitResult objectMouseOver = null;
        if (entity != null) {
            if (mc.world != null) {
                pointedEntity = null;
                double d0 = (double) Math.max((mc.interactionManager.getCurrentGameMode().isCreative() ? 5.0F : 4.5F), gameRendererAccessor.tarasande_getReach());
                Vec3d vec31 = Vec3d.fromPolar(MathHelper.lerp(partialTicks, prevPitch, pitch), MathHelper.lerpAngleDegrees(partialTicks, prevYaw, yaw));
                objectMouseOver = raycast(entity, vec31, d0, partialTicks, false); // TODO block collisions
                double d1 = d0;
                Vec3d vec3 = entity.getCameraPosVec(partialTicks);
                boolean flag = false;
                int i = 3;

                if (mc.interactionManager.hasExtendedReach() && !gameRendererAccessor.tarasande_isDisableReachExtension()) {
                    d0 = 6.0D;
                    d1 = 6.0D;
                } else {
                    if (d0 > 3.0D) {
                        flag = true;
                    }
                }

                if (objectMouseOver != null) {
                    d1 = objectMouseOver.getPos().distanceTo(vec3);
                }

                Vec3d vec32 = vec3.add(vec31.x * d0, vec31.y * d0, vec31.z * d0);
                pointedEntity = null;
                Vec3d vec33 = null;
                float f = 1.0F;
                List<Entity> list = mc.world.getOtherEntities(entity, entity.getBoundingBox().stretch(vec31.x * d0, vec31.y * d0, vec31.z * d0).expand((double) f, (double) f, (double) f), Predicates.and(e -> !e.isSpectator(), new Predicate<Entity>() {
                    public boolean apply(Entity p_apply_1_) {
                        return p_apply_1_.canHit();
                    }
                }));
                double d2 = d1;

                for (int j = 0; j < list.size(); ++j) {
                    Entity entity1 = (Entity) list.get(j);
                    float f1 = entity1.getTargetingMargin();
                    Box axisalignedbb = entity1.getBoundingBox().expand((double) f1, (double) f1, (double) f1);
                    HitResult movingobjectposition = ((IBox_Protocol) axisalignedbb).protocolhack_calculateIntercept(vec3, vec32);

                    if (axisalignedbb.contains(vec3)) {
                        if (d2 >= 0.0D) {
                            pointedEntity = entity1;
                            vec33 = movingobjectposition == null ? vec3 : movingobjectposition.getPos();
                            d2 = 0.0D;
                        }
                    } else if (movingobjectposition != null) {
                        double d3 = vec3.distanceTo(movingobjectposition.getPos());

                        if (d3 < d2 || d2 == 0.0D) {
                            if (entity1 == entity.getVehicle()) {
                                if (d2 == 0.0D) {
                                    pointedEntity = entity1;
                                    vec33 = movingobjectposition.getPos();
                                }
                            } else {
                                pointedEntity = entity1;
                                vec33 = movingobjectposition.getPos();
                                d2 = d3;
                            }
                        }
                    }
                }

                if (!ModuleNoMiningTrace.Companion.shouldCancel()) {
                    if (pointedEntity != null && flag && vec3.distanceTo(vec33) > gameRendererAccessor.tarasande_getReach()) {
                        pointedEntity = null;
                        objectMouseOver = BlockHitResult.createMissed(vec33, (Direction) null, new BlockPos(vec33));
                    }

                    if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                        objectMouseOver = new EntityHitResult(pointedEntity, vec33);

                        if (pointedEntity instanceof LivingEntity || pointedEntity instanceof ItemFrameEntity) {
                            pointedEntity = pointedEntity;
                        }
                    }
                }
            }
        }
        return new ViaRaytraceResult(pointedEntity, objectMouseOver);
    }
}
