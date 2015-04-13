package com.lunagameserve.get_shaded.light;

import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.lunagameserve.get_shaded.color.ColorMap;
import com.lunagameserve.get_shaded.xml.Filter;
import com.lunagameserve.get_shaded.xml.Query;
import com.lunagameserve.get_shaded.xml.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public class LightGrid {
    public final double[][] lightData;

    private static int X_CELLS = 50;
    private static int Y_CELLS = 50;

    private final double height;
    private final double width;
    private final double dx;
    private final double dy;

    private final LatLngBounds bounds;

    private final double minBin;
    private final double maxBin;

    private final double minLat;
    private final double maxLat;
    private final double minLng;
    private final double maxLng;

    private final List<Record> records;

    public LightGrid(LatLngBounds bounds) {
        this.bounds = bounds;

        minLat = Math.min(bounds.northeast.latitude,
                bounds.southwest.latitude);
        maxLat = Math.max(bounds.northeast.latitude,
                bounds.southwest.latitude);
        minLng = Math.min(bounds.northeast.longitude,
                bounds.southwest.longitude);
        maxLng = Math.max(bounds.northeast.longitude,
                bounds.southwest.longitude);

        height = Math.abs(bounds.northeast.latitude -
                          bounds.southwest.latitude);
        width = Math.abs(bounds.northeast.longitude -
                         bounds.southwest.longitude);

        dx = width / X_CELLS;
        dy = height / Y_CELLS;

        records = readRecords();
        lightData = generateLightData(records);

        minBin = findMinValue(lightData);
        maxBin = findMaxValue(lightData);
    }

    public void render(GoogleMap map) {
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
            Log.wtf(e.getClass().getName(), e.getMessage());
            return new ArrayList<Record>();
        }
    }
}
