package de.florianmichael.clampclient.injection.instrumentation_1_12_2.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

public record ViaRaytraceResult(Entity pointed, HitResult target) {
}
