package de.florianmichael.viabeta.api.model;

import com.viaversion.viaversion.api.minecraft.Position;

import java.util.Objects;

public class Location {

    private double x;
    private double y;
    private double z;

    public Location(final Position position) {
        this(position.x(), position.y(), position.z());
    }

    public Location(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public double getX() {
        return this.x;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public double getY() {
        return this.y;
    }

    public void setZ(final double z) {
        this.z = z;
    }

    public double getZ() {
        return this.z;
    }

    public double distanceTo(final Location p2) {
        return Math.sqrt(Math.pow(p2.getX() - this.x, 2) +
                Math.pow(p2.getY() - this.y, 2) +
                Math.pow(p2.getZ() - this.z, 2)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.x, x) == 0 && Double.compare(location.y, y) == 0 && Double.compare(location.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

}
