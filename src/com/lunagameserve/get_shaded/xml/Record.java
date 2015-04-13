package com.lunagameserve.get_shaded.xml;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public class Record {

    public final String owner;
    public final long time;
    public final double longitude;
    public final double latitude;
    public final double altitude;
    public final double xAccel;
    public final double yAccel;
    public final double zAccel;
    public final double xRot;
    public final double yRot;
    public final double zRot;
    public final double light;
    public final double pressure;

    public Record(String owner, long time, double longitude, double latitude,
                  double altitude, double xAccel, double yAccel, double zAccel,
                  double xRot, double yRot, double zRot, double light,
                  double pressure) {
        this.owner = owner;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.xAccel = xAccel;
        this.yAccel = yAccel;
        this.zAccel = zAccel;
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
        this.light = light;
        this.pressure = pressure;
    }
}
