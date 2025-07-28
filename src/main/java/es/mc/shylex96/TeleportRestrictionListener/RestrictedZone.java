package es.mc.shylex96.TeleportRestrictionListener;

import org.bukkit.Location;
import org.bukkit.World;

public class RestrictedZone {
    public final String name;
    public final Location center;
    public final String requiredRank;
    public final double xRange, yRange, zRange;

    public RestrictedZone(String name, Location center, String requiredRank,
                          double xRange, double yRange, double zRange) {
        this.name = name;
        this.center = center;
        this.requiredRank = requiredRank;
        this.xRange = xRange;
        this.yRange = yRange;
        this.zRange = zRange;
    }

    public boolean isInside(Location loc) {
        World world = center.getWorld();
        return world != null && world.equals(loc.getWorld()) &&
                Math.abs(loc.getX() - center.getX()) <= xRange &&
                Math.abs(loc.getY() - center.getY()) <= yRange &&
                Math.abs(loc.getZ() - center.getZ()) <= zRange;
    }
}
