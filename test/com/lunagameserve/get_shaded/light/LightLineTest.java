package com.lunagameserve.get_shaded.light;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sixstring982 on 4/16/15.
 */
public class LightLineTest {
    /* Ghetto test runner because android tests are truly a pain in the ass */
    public static void main(String[] args) {
        new LightLineTest().testSimpleLightLine();
    }

    private void testSimpleLightLine() {
        LatLng ne = new LatLng(0, 0);
        LatLng sw = new LatLng(25, -25);
        LatLngBounds bounds = new LatLngBounds(ne, sw);
        LightGrid grid = new LightGrid(bounds, true);

        List<LatLng> pline = new ArrayList<LatLng>();
        pline.add(new LatLng(0.75, -0.75));
        pline.add(new LatLng(24.25, -24.25));

        LightLine line = new LightLine(grid, pline);

        List<PolylineOptions> pops = line.getPolyLines();
        System.out.println("Done");
    }

    private boolean assertLatLng(LatLng a, LatLng b) {
        return Math.abs(a.latitude - b.latitude) <   0.000001 &&
               Math.abs(a.longitude - b.longitude) < 0.000001;
    }
}
