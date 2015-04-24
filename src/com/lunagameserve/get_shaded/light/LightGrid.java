package com.lunagameserve.get_shaded.light;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.lunagameserve.get_shaded.color.ColorMap;
import com.lunagameserve.get_shaded.json.JSONSerializable;
import com.lunagameserve.get_shaded.util.MathUtil;
import com.lunagameserve.get_shaded.xml.Filter;
import com.lunagameserve.get_shaded.xml.Query;
import com.lunagameserve.get_shaded.xml.Record;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public class LightGrid extends JSONSerializable {
    public final double[][] lightData;

    private static int X_CELLS = 25;
    private static int Y_CELLS = 25;

    private final double dx;
    private final double dy;

    private final LatLngBounds bounds;

    public double getMinBin() {
        return minBin;
    }

    public double getMaxBin() {
        return maxBin;
    }

    private final double minBin;
    private final double maxBin;

    private final double minLat;
    private final double maxLat;
    private final double minLng;
    private final double maxLng;

    private final List<Record> records;

    public LightGrid(LatLngBounds bounds) {
        this(bounds, false);
    }

    public LightGrid(LatLngBounds bounds, boolean debug) {
        this.bounds = bounds;

        minLat = Math.min(bounds.northeast.latitude,
                bounds.southwest.latitude);
        maxLat = Math.max(bounds.northeast.latitude,
                bounds.southwest.latitude);
        minLng = Math.min(bounds.northeast.longitude,
                bounds.southwest.longitude);
        maxLng = Math.max(bounds.northeast.longitude,
                bounds.southwest.longitude);

        double height = Math.abs(bounds.northeast.latitude -
                bounds.southwest.latitude);
        double width = Math.abs(bounds.northeast.longitude -
                bounds.southwest.longitude);

        dx = width / X_CELLS;
        dy = height / Y_CELLS;

        if (debug) {
            records = new ArrayList<Record>();
        } else {
            records = readRecords();
        }
        lightData = generateLightData(records);

        minBin = findMinValue(lightData);
        maxBin = findMaxValue(lightData);
    }

    public void renderPolygons(GoogleMap map) {
        for (int x = 0; x < lightData.length; x++) {
            for (int y = 0; y < lightData[x].length; y++) {
                PolygonOptions pops = new PolygonOptions();
                pops.add(new LatLng(bounds.northeast.latitude - y * dy,
                        bounds.northeast.longitude - x * dx));
                pops.add(new LatLng(bounds.northeast.latitude - (y + 1) * dy,
                        bounds.northeast.longitude - x * dx));
                pops.add(new LatLng(bounds.northeast.latitude - (y + 1) * dy,
                        bounds.northeast.longitude - (x + 1) * dx));
                pops.add(new LatLng(bounds.northeast.latitude - y * dy,
                        bounds.northeast.longitude - (x + 1) * dx));

                pops.strokeWidth(1f);
                pops.strokeColor(0x7f7f7f7f);

                if (lightData[x][y] > 0) {
                    pops.fillColor(ColorMap.redBlueGradient(
                            minBin, lightData[x][y], maxBin
                    ));
                    map.addPolygon(pops);
                }
            }
        }
    }

    public void renderRecords(GoogleMap map) {
        for (Record r : records) {
            CircleOptions cops = new CircleOptions();
            cops.center(new LatLng(r.latitude, r.longitude));
            cops.radius(1);
            cops.fillColor(ColorMap.redBlueGradient(minBin, r.light, maxBin));
            cops.strokeWidth(1f);
            cops.strokeColor(0x7f7f7f7f);
            map.addCircle(cops);
        }
    }

    public void render(GoogleMap map) {
        renderPolygons(map);
        renderRecords(map);
    }

    private double findMinValue(double[][] vals) {
        double min = vals[0][0];
        for (double[] val : vals) {
            for (double aVal : val) {
                if (aVal > 0) {
                    min = Math.min(min, aVal);
                }
            }
        }
        return min;
    }

    private double findMaxValue(double[][] vals) {
        double max = vals[0][0];
        for (double[] val : vals) {
            for (double aVal : val) {
                if (aVal > 0 ) {
                    max = Math.max(max, aVal);
                }
            }
        }
        return max;
    }

    private double[][] generateLightData(List<Record> records) {
        double[][] lightData = new double[X_CELLS][];
        for (int x = 0; x < lightData.length; x++) {
            lightData[x] = new double[Y_CELLS];
            for (int y = 0; y < lightData[x].length; y++) {
                double total = 0.0;
                int count = 0;

                LatLng c1 = new LatLng(
                        bounds.northeast.latitude - y * dy,
                        bounds.northeast.longitude - x * dx);
                LatLng c3 = new LatLng(
                        bounds.northeast.latitude - (y + 1) * dy,
                        bounds.northeast.longitude - (x + 1) * dx);

                for (Record record : records) {
                    if (c1.latitude > record.latitude &&
                        c3.latitude < record.latitude &&
                        c1.longitude > record.longitude &&
                        c3.longitude < record.longitude) {
                        total += record.light;
                        count++;
                    }
                }
                if (count == 0) {
                    lightData[x][y] = -1.0;
                } else {
                    lightData[x][y] = total / count;
                }
            }
        }
        return lightData;
    }

    private List<Record> readRecords() {
        List<String> filters = new ArrayList<String>();

        filters.add(Filter.MinLat.create(minLat));
        filters.add(Filter.MaxLat.create(maxLat));
        filters.add(Filter.MinLon.create(minLng));
        filters.add(Filter.MaxLon.create(maxLng));

        try {
            return new Query(filters).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf(e.getClass().getName(), e.getMessage());
            return new ArrayList<Record>();
        }
    }

    private double linDist(double a, double b) {
        return Math.abs(a - b);
    }

    private double dist(LatLng a, LatLng b) {
        double rise = a.latitude - b.latitude;
        double run = a.longitude - b.longitude;
        return Math.sqrt(rise * rise + run * run);
    }

    public List<LatLng> gridIntersections(LatLng start, LatLng end) {
        double run = end.longitude - start.longitude;
        double rise = end.latitude - start.latitude;

        List<LatLng> betweens = new ArrayList<LatLng>();
        List<Double> horiz =
                horizontalIntersections(start.longitude, end.longitude);
        for (Double d : horiz) {
            double fractionOfX = (d - start.longitude) / run;
            double y = fractionOfX * rise;
            betweens.add(new LatLng(y + start.latitude, d));
        }

        for (Double d : verticalIntersections(start.latitude,
                                              end.latitude)) {
            double fractionOfY = (d - start.latitude) / rise;
            double x = fractionOfY * run;
            betweens.add(new LatLng(d, x + start.longitude));
        }

        /* Insertion sort by distance from start */
        List<LatLng> sorted = new ArrayList<LatLng>();
        sorted.add(start);
        while (betweens.size() > 0) {
            LatLng closest = betweens.get(0);
            for (int i = 1; i < betweens.size(); i++) {
                if (dist(start, betweens.get(i)) <
                    dist(start, closest)) {
                    closest = betweens.get(i);
                }
            }
            if (!sorted.contains(closest)) {
                sorted.add(closest);
            }
            betweens.remove(closest);
        }
        sorted.add(end);

        return sorted;
    }

    private List<Double> horizontalIntersections(double startLng,
                                                 double endLng) {
        List<Double> acc = new ArrayList<Double>();
        double stepScalar = 1.0;
        if (startLng > endLng) {
            stepScalar = -1.0;
        }

        double restDx =
                MathUtil.mod((startLng - bounds.southwest.longitude), dx);
        if (stepScalar > 0) {
            restDx = dx - restDx;
        }

        /* Doesn't start and end in the same unit? */
        if (linDist(startLng, startLng + restDx * stepScalar) <
                linDist(startLng, endLng)) {
            for (double ix = startLng + restDx * stepScalar;
                 linDist(startLng, ix) < linDist(startLng, endLng);
                 ix += dx * stepScalar) {
                acc.add(ix);
            }
        }

        return acc;
    }

    private List<Double> verticalIntersections(double startLat, double endLat) {
        List<Double> acc = new ArrayList<Double>();
        double stepScalar = 1.0;
        if (startLat > endLat) {
            stepScalar = -1.0;
        }


        double restDy =
                MathUtil.mod((startLat - bounds.northeast.latitude), dy);
        if (stepScalar > 0) {
            restDy = dy - restDy;
        }

        /* Doesn't start and end in the same unit? */
        if (linDist(startLat, startLat + restDy * stepScalar) <
            linDist(startLat, endLat)) {
            for (double ix = startLat + restDy * stepScalar;
                 linDist(startLat, ix) < linDist(startLat, endLat);
                 ix += dy * stepScalar) {
                acc.add(ix);
            }
        }

        return acc;
    }

    public double lightAt(LatLng point) {
        for (int x = 0; x < lightData.length; x++) {
            for (int y = 0; y < lightData[x].length; y++) {

                LatLng c1 = new LatLng(
                        bounds.northeast.latitude - y * dy,
                        bounds.northeast.longitude - x * dx);
                LatLng c3 = new LatLng(
                        bounds.northeast.latitude - (y + 1) * dy,
                        bounds.northeast.longitude - (x + 1) * dx);

                if (c1.latitude > point.latitude &&
                        c3.latitude < point.latitude &&
                        c1.longitude > point.longitude &&
                        c3.longitude < point.longitude) {
                    return lightData[x][y];
                }
            }
        }
        return -1.0;
    }

    private LightGrid(double[][] lightData, double dx, double dy,
                      LatLngBounds bounds, double minBin, double maxBin,
                      double minLat, double minLng, double maxLat,
                      double maxLng, List<Record> records) {
        this.lightData = lightData;
        this.dx = dx;
        this.dy = dy;
        this.bounds = bounds;
        this.minBin = minBin;
        this.maxBin = maxBin;
        this.minLat = minLat;
        this.minLng = minLng;
        this.maxLat = maxLat;
        this.maxLng = maxLng;
        this.records = records;
    }

    public static LightGrid read(File file) throws IOException, JSONException {
        FileInputStream in = new FileInputStream(file);
        GZIPInputStream gz = new GZIPInputStream(in);
        InputStreamReader r = new InputStreamReader(gz);
        JsonReader reader = new JsonReader(r);

        LightGrid grid = readJson(reader);

        r.close();

        return grid;
    }

    public static LightGrid readJson(JsonReader reader) throws IOException {
        double[][] lightData = null;
        double dx = 0;
        double dy = 0;
        double swlat = 0;
        double swlng = 0;
        double nelat = 0;
        double nelng = 0;
        double minbin = 0;
        double maxbin = 0;
        double minlat = 0;
        double minlng = 0;
        double maxlat = 0;
        double maxlng = 0;
        List<Record> records = new ArrayList<Record>();
        reader.beginObject();
        {
            while(reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("lightData")) {
                    reader.beginArray();
                    {
                        lightData = new double[X_CELLS][];
                        for (int x = 0; x < X_CELLS; x++) {
                            lightData[x] = new double[Y_CELLS];
                            for (int y = 0; y < Y_CELLS; y++) {
                                lightData[x][y] = reader.nextDouble();
                            }
                        }
                    }
                    reader.endArray();
                } else if (name.equals("dx")) {
                    dx = reader.nextDouble();
                } else if (name.equals("dy")) {
                    dy = reader.nextDouble();
                } else if(name.equals("swlat")) {
                    swlat = reader.nextDouble();
                } else if(name.equals("swlng")) {
                    swlng = reader.nextDouble();
                } else if(name.equals("nelat")) {
                    nelat = reader.nextDouble();
                } else if(name.equals("nelng")) {
                    nelng = reader.nextDouble();
                } else if(name.equals("minbin")) {
                    minbin = reader.nextDouble();
                } else if(name.equals("maxbin")) {
                    maxbin = reader.nextDouble();
                } else if(name.equals("minLat")) {
                    minlat = reader.nextDouble();
                } else if(name.equals("minLng")) {
                    minlng = reader.nextDouble();
                } else if(name.equals("maxLat")) {
                    maxlat = reader.nextDouble();
                } else if(name.equals("maxLng")) {
                    maxlng = reader.nextDouble();
                } else if(name.equals("records")) {
                    reader.beginArray();
                    {
                        while (reader.hasNext()) {
                            reader.beginObject();
                            {
                                String owner = "";
                                long time = 0;
                                double lng = 0;
                                double lat = 0;
                                double alt = 0;
                                double xaccel = 0;
                                double yaccel = 0;
                                double zaccel = 0;
                                double xRot = 0;
                                double yRot = 0;
                                double zRot = 0;
                                double light = 0;
                                double pressure = 0;
                                while (reader.hasNext()) {
                                    String rname = reader.nextName();
                                    if (rname.equals("owner")) {
                                        owner = reader.nextString();
                                    } else if (rname.equals("time")) {
                                        time = reader.nextLong();
                                    } else if (rname.equals("lng")) {
                                        lng = reader.nextDouble();
                                    } else if (rname.equals("lat")) {
                                        lat = reader.nextDouble();
                                    }  else if (rname.equals("alt")) {
                                        alt = reader.nextDouble();
                                    } else if (rname.equals("xaccel")) {
                                        xaccel = reader.nextDouble();
                                    } else if (rname.equals("yaccel")) {
                                        yaccel = reader.nextDouble();
                                    } else if (rname.equals("zaccel")) {
                                        zaccel = reader.nextDouble();
                                    } else if (rname.equals("xRot")) {
                                        xRot = reader.nextDouble();
                                    } else if (rname.equals("yRot")) {
                                        yRot = reader.nextDouble();
                                    } else if (rname.equals("zRot")) {
                                        zRot = reader.nextDouble();
                                    } else if (rname.equals("light")) {
                                        light = reader.nextDouble();
                                    } else if (rname.equals("pressure")) {
                                        pressure = reader.nextDouble();
                                    }
                                }

                                records.add(new Record(
                                        owner, time, lng, lat, alt, xaccel,
                                        yaccel, zaccel, xRot, yRot, zRot,
                                        light, pressure
                                ));
                            }
                            reader.endObject();
                        }
                    }
                    reader.endArray();
                }
                else {
                    Log.d("LightGridRead", "Unknown value for <" + name + ">");
                    reader.skipValue();
                }
            }
        }
        reader.endObject();

        return new LightGrid(lightData, dx, dy, new LatLngBounds(
                new LatLng(swlat, swlng), new LatLng(nelat, nelng)
        ), minbin, maxbin, minlat, minlng, maxlat, maxlng, records);
    }

    @Override
    public void writeJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        {
            writer.name("lightData");
            writer.beginArray();
            {
                for (double[] aLightData : lightData) {
                    for (double anALightData : aLightData) {
                        writer.value(anALightData);
                    }
                }
            }
            writer.endArray();

            writer.name("dx").value(dx);
            writer.name("dy").value(dy);

            writer.name("swlat").value(bounds.southwest.latitude);
            writer.name("swlng").value(bounds.southwest.longitude);
            writer.name("nelat").value(bounds.northeast.latitude);
            writer.name("nelng").value(bounds.northeast.longitude);

            writer.name("minbin").value(minBin);
            writer.name("maxbin").value(maxBin);

            writer.name("minLat").value(minLat);
            writer.name("maxLat").value(maxLat);
            writer.name("minLng").value(minLng);
            writer.name("maxLng").value(maxLng);

            writer.name("records");
            writer.beginArray();
            {
                for(Record r : records) {
                    writer.beginObject();
                    {
                        writer.name("owner").value(r.owner);
                        writer.name("time").value(r.time);
                        writer.name("lng").value(r.longitude);
                        writer.name("lat").value(r.latitude);
                        writer.name("alt").value(r.altitude);
                        writer.name("xaccel").value(r.xAccel);
                        writer.name("yaccel").value(r.yAccel);
                        writer.name("zaccel").value(r.zAccel);
                        writer.name("xRot").value(r.xRot);
                        writer.name("yRot").value(r.yRot);
                        writer.name("zRot").value(r.zRot);
                        writer.name("light").value(r.light);
                        writer.name("pressure").value(r.pressure);
                    }
                    writer.endObject();
                }
            }
            writer.endArray();
        }
        writer.endObject();
    }
}
