package simo.homes.models;

import org.bukkit.Location;

public class Home {
    private final Location location;

    public Home(Location location) {
        this.location = location;
    }

    public double getX() {
        return location.x();
    }
    public double getY() {
        return location.y();
    }
    public double getZ() {
        return location.z();
    }
    public String getWorld() {
        return location.getWorld().getName();
    }
    public float getYaw() {
        return location.getYaw();
    }
    public float getPitch() {
        return location.getPitch();
    }
    public Location getLocation() {
        return location;
    }
}
