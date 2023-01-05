package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.model;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import de.florianmichael.viabeta.api.model.Location;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.model.base.AbstractTrackedEntity;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.sound.SoundEmulation;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.sound.SoundType;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.storage.EntityTracker_1_2_4_5;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.metadata.MetaIndex1_6_1to1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.metadata.MetaIndex1_8to1_7_6;

import java.util.List;

public class TrackedLivingEntity extends AbstractTrackedEntity {

    private int soundTime;

    // ENTITY_AGEABLE
    public int growingAge;

    // ENTITY_TAMEABLE_ANIMAL
    public boolean isTamed;

    // WOLF
    public int wolfHealth;
    public boolean wolfIsAngry;

    public TrackedLivingEntity(int entityId, Location location, Entity1_10Types.EntityType entityType) {
        super(entityId, location, entityType);
    }

    public void tick(EntityTracker_1_2_4_5 tracker) {
        if (tracker.RND.nextInt(1000) < this.soundTime++) {
            this.soundTime = SoundEmulation.getSoundDelayTime(this.getEntityType());

            tracker.playSound(this.getEntityId(), SoundType.IDLE);
        }

        if (this.getEntityType().isOrHasParent(Entity1_10Types.EntityType.ENTITY_AGEABLE)) {
            if (this.growingAge < 0) {
                this.growingAge++;
            } else if (this.growingAge > 0) {
                this.growingAge--;
            }
        }
    }

    public void updateMetadata(List<Metadata> metadataList) {
        for (Metadata metadata : metadataList) {
            final MetaIndex1_6_1to1_5_2 index = MetaIndex1_6_1to1_5_2.searchIndex(this.getEntityType(), metadata.id());
            final MetaIndex1_8to1_7_6 index2 = MetaIndex1_8to1_7_6.searchIndex(this.getEntityType(), metadata.id());

            if (index == MetaIndex1_6_1to1_5_2.WOLF_HEALTH) {
                this.wolfHealth = metadata.<Integer>value();
            } else if (index != null) {
                continue;
            }
            if (index2 == MetaIndex1_8to1_7_6.ENTITY_AGEABLE_AGE) {
                this.growingAge = metadata.value();
            } else if (index2 == MetaIndex1_8to1_7_6.TAMEABLE_FLAGS) {
                this.isTamed = (metadata.<Byte>value() & 4) != 0;
                this.wolfIsAngry = (metadata.<Byte>value() & 2) != 0;
            }
        }
    }

    public void applyPitch(EntityTracker_1_2_4_5 tracker, ConfiguredSound sound) {
        float pitch;

        if (this.getEntityType().isOrHasParent(Entity1_10Types.EntityType.ENTITY_AGEABLE) && this.growingAge < 0) {
            pitch = (tracker.RND.nextFloat() - tracker.RND.nextFloat()) * 0.2F + 1.5F;
        } else {
            pitch = (tracker.RND.nextFloat() - tracker.RND.nextFloat()) * 0.2F + 1.0F;
        }

        sound.setPitch(pitch);
    }

}
