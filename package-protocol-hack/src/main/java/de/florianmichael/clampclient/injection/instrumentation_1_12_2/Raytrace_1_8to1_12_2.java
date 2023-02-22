package de.florianmichael.clampclient.injection.instrumentation_1_12_2;

import com.google.common.base.Predicates;
import de.florianmichael.clampclient.injection.instrumentation_1_12_2.model.ViaRaytraceResult;
import de.florianmichael.clampclient.injection.instrumentation_1_8.definition.MathHelper_1_8;
import de.florianmichael.clampclient.injection.mixininterface.IBox_Protocol;
import de.florianmichael.rmath.mathtable.MathTableRegistry;
import de.florianmichael.tarasande_protocol_hack.tarasande.values.ProtocolHackValues;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.tarasandedevelopment.tarasande.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.impl.EventBoundingBoxOverride;
import net.tarasandedevelopment.tarasande.injection.accessor.IGameRenderer;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleNoMiningTrace;
import net.tarasandedevelopment.tarasande.util.extension.minecraft.HitResultKt;

import java.util.List;

public class Raytrace_1_8to1_12_2 {
    public static final Raytrace_1_8to1_12_2 CLASS_WRAPPER = new Raytrace_1_8to1_12_2();

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

    public ViaRaytraceResult raytrace(Entity entity, float prevYaw, float prevPitch, float yaw, float pitch, float partialTicks) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final IGameRenderer gameRendererAccessor = (IGameRenderer) mc.gameRenderer;

        Entity pointedEntity = null;
        HitResult objectMouseOver = null;
        if (entity != null) {
            if (mc.world != null) {
                double d0 = (double) Math.max((mc.interactionManager.getCurrentGameMode().isCreative() ? 5.0F : 4.5F), gameRendererAccessor.tarasande_getReach());
                Vec3d vec31 = Vec3d.fromPolar(MathHelper.lerp(partialTicks, prevPitch, pitch), MathHelper.lerpAngleDegrees(partialTicks, prevYaw, yaw));
                if(!gameRendererAccessor.tarasande_isAllowThroughWalls())
                    objectMouseOver = raycast(entity, vec31, d0, partialTicks, false); // TODO block collisions
                double d1 = d0;
                Vec3d vec3 = entity.getCameraPosVec(partialTicks);
                boolean flag = false;

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
                Vec3d vec33 = null;
                float f = 1.0F;
                List<Entity> list = mc.world.getOtherEntities(entity, entity.getBoundingBox().stretch(vec31.x * d0, vec31.y * d0, vec31.z * d0).expand((double) f, (double) f, (double) f), Predicates.and(e -> !e.isSpectator(), Entity::canHit));
                double d2 = d1;

                for (Entity entity1 : list) {
                    float f1 = entity1.getTargetingMargin();

                    EventBoundingBoxOverride eventBoundingBoxOverride = new EventBoundingBoxOverride(entity1, entity1.getBoundingBox());
                    EventDispatcher.INSTANCE.call(eventBoundingBoxOverride);

                    Box axisalignedbb = eventBoundingBoxOverride.getBoundingBox().expand((double) f1, (double) f1, (double) f1);
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
                if (!ManagerModule.INSTANCE.get(ModuleNoMiningTrace.class).shouldCancel()) {
                    if (pointedEntity != null && flag && vec3.distanceTo(vec33) > gameRendererAccessor.tarasande_getReach()) {
                        pointedEntity = null;
                        objectMouseOver = BlockHitResult.createMissed(vec33, null, new BlockPos(vec33));
                    }

                    if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                        objectMouseOver = new EntityHitResult(pointedEntity, vec33);
                    }
                }
            }
        }
        return new ViaRaytraceResult(pointedEntity, objectMouseOver);
    }

    public ViaRaytraceResult bruteforceRaytrace(Entity entity, float prevYaw, float prevPitch, float yaw, float pitch, float partialTicks) {
        final MathTableRegistry originalMathTable = MathHelper_1_8.mathTable;

        ViaRaytraceResult bestRaytrace = null;

        MathTableRegistry[] values = MathTableRegistry.values();
        for (int i = 0; i < values.length; i++) {
            MathTableRegistry mathTable = values[i];
            if (!ProtocolHackValues.INSTANCE.getBruteforceRaytraceFastMathTables().isSelected(i))
                continue;

            MathHelper_1_8.mathTable = mathTable;
            ViaRaytraceResult newRaytrace = raytrace(entity, prevYaw, prevPitch, yaw, pitch, partialTicks);

            if(bestRaytrace == null)
                bestRaytrace = newRaytrace;
            else if(!HitResultKt.isEntityHitResult(bestRaytrace.target()) && HitResultKt.isEntityHitResult(newRaytrace.target()))
                bestRaytrace = newRaytrace;
            else if(HitResultKt.isMissHitResult(bestRaytrace.target()) && !HitResultKt.isMissHitResult(newRaytrace.target()))
                bestRaytrace = newRaytrace;
        }

        MathHelper_1_8.mathTable = originalMathTable;

        return bestRaytrace;
    }
}
