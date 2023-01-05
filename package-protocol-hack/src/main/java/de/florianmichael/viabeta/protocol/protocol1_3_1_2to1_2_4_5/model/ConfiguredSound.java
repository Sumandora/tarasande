package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.model;

import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.sound.Sound;

public class ConfiguredSound {

    private Sound sound;
    private float volume;
    private float pitch;

    public ConfiguredSound(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

}
