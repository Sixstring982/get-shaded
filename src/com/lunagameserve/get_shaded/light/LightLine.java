package com.lunagameserve.get_shaded.light;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lunagameserve.get_shaded.color.ColorMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sixstring982 on 4/14/15.
 */
public class LightLine implements Parcelable {
    private LightGrid grid;
    private List<LatLng> path;

    private List<PolylineOptions> lPopCache = null;

    private List<LatLng> generatedIntersections = null;

    public static final Parcelable.Creator<LightLine> CREATOR
            = new Parcelable.Creator<LightLine>() {
        public LightLine createFromParcel(Parcel in) {
            return new LightLine(in);
        }

        public LightLine[] newArray(int size) {
            return new LightLine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        for (PolylineOptions pop : getPolyLines()) {
            dest.writeDouble(pop.getPoints().get(0).latitude);
            dest.writeDouble(pop.getPoints().get(0).longitude);
            dest.writeDouble(pop.getPoints().get(1).latitude);
            dest.writeDouble(pop.getPoints().get(1).longitude);
            dest.writeInt(pop.getColor());
        }
    }

    public LightLine(Parcel in) {
        lPopCache = new ArrayList<PolylineOptions>();
        while (in.dataAvail() > 0) {
            LatLng s = new LatLng(
              in.readDouble(), in.readDouble()
            );
            LatLng e = new LatLng(
                    in.readDouble(), in.readDouble()
            );
            int color = in.readInt();

            PolylineOptions o = new PolylineOptions();
            o.add(s);
            o.add(e);
            o.color(color);

            lPopCache.add(o);
        }
    }

    public LightLine(LightGrid grid, List<LatLng> path) {
        this.grid = grid;
        this.path = path;
    }

    public List<PolylineOptions> getPolyLines() {
        if (lPopCache != null) {
            return lPopCache;
        } else {
            return lPopCache = generatePolyLines();
        }
    }

    private List<PolylineOptions> generatePolyLines() {
        List<PolylineOptions> acc = new ArrayList<PolylineOptions>();

        generatedIntersections = new ArrayList<LatLng>();

        for (int i = 0; i < path.size() - 1; i++) {
            LatLng start = path.get(i);
            LatLng end = path.get(i + 1);

            /* Find all grid intersections */
            List<LatLng> intersections = grid.gridIntersections(start, end);

            generatedIntersections.addAll(intersections);

            /* Make PolyLineOptions of each segment */
            List<PolylineOptions> ops = new ArrayList<PolylineOptions>();
            for (int j = 0; j < intersections.size() - 1; j++) {
                LatLng s = intersections.get(j);
                LatLng e = intersections.get(j + 1);
                LatLng between = new LatLng((s.latitude + e.latitude) / 2.0,
                                            (s.longitude + e.longitude) / 2.0);
                double lval = grid.lightAt(between);
                int color = 0xff7f7f7f;
                if (lval > 0) {
                    color = ColorMap.redBlueGradient(
                            grid.getMinBin(), lval, grid.getMaxBin());
                }

                PolylineOptions o = new PolylineOptions();
                o.add(s);
                o.add(e);
                o.color(color);
                ops.add(o);
            }
            acc.addAll(ops);
        }

        return acc;
    }

    public void renderGridIntersections(GoogleMap map) {
        for (LatLng i : generatedIntersections) {
            CircleOptions cops = new CircleOptions();
            cops.center(i);
            cops.radius(1.0);
            cops.fillColor(0xff000000);
            map.addCircle(cops);
        }
    }

    public void render(GoogleMap map) {
        List<PolylineOptions> pops = getPolyLines();
        for (PolylineOptions p : pops) {
            map.addPolyline(p);
        }
    }
}
