package com.lunagameserve.get_shaded.directions;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;
import com.lunagameserve.get_shaded.io.IOUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public class Directions {

    public JSONObject dirObj;

    public final LatLngBounds bounds;

    public final List<LatLng> polyline;

    public Directions(LatLng origin, LatLng destination)
            throws IOException, JSONException {
        this(latLngString(origin), latLngString(destination));
    }

    public Directions(String origin, String destination)
            throws IOException, JSONException {
        String request = buildRequest(origin, destination);
        dirObj = new JSONObject(IOUtil.webRequestContents(request));

        bounds = generateBounds();
        polyline = generatePolylines();
    }

    public Directions(String origin, LatLng destination)
            throws IOException, JSONException {
        this(origin, latLngString(destination));
    }

    public Directions(LatLng origin, String destination)
            throws IOException, JSONException {
        this(latLngString(origin), destination);
    }

    private List<LatLng> generatePolylines() throws JSONException {
        List<LatLng> pathPts = new ArrayList<LatLng>();

        JSONArray routes = dirObj.getJSONArray("routes");
        for (int i = 0; i < routes.length(); i++) {
            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");
            for (int j = 0; j < legs.length(); j++) {
                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");
                for (int k = 0; k < steps.length(); k++) {
                    JSONObject polyline = steps.getJSONObject(k)
                            .getJSONObject("polyline");

                    pathPts.addAll(PolyUtil.decode(
                            polyline.getString("points")));
                }
            }
        }

        return pathPts;
    }

    private LatLngBounds generateBounds() throws JSONException {
        List<Double> lats = new ArrayList<Double>();
        List<Double> lons = new ArrayList<Double>();

        JSONArray routes = dirObj.getJSONArray("routes");
        for (int i = 0; i < routes.length(); i++) {
            JSONObject bounds = routes.getJSONObject(i).getJSONObject("bounds");

            JSONObject ne = bounds.getJSONObject("northeast");
            lats.add(ne.getDouble("lat"));
            lons.add(ne.getDouble("lng"));

            JSONObject sw = bounds.getJSONObject("southwest");
            lats.add(sw.getDouble("lat"));
            lons.add(sw.getDouble("lng"));
        }

        double minLon = lons.get(0);
        double maxLon = lons.get(0);
        for (double d : lons) {
            if (Math.max(maxLon, d) == d) {
                maxLon = d;
            }

            if (Math.min(minLon, d) == d) {
                minLon = d;
            }
        }

        double minLat = lats.get(0);
        double maxLat = lats.get(0);
        for (double d : lats) {
            if (Math.max(maxLat, d) == d) {
                maxLat = d;
            }

            if (Math.min(minLat, d) == d) {
                minLat = d;
            }
        }

        return new LatLngBounds(new LatLng(minLat, minLon),
                                new LatLng(maxLat, maxLon));
    }

    private static String latLngString(LatLng pos) {
        String latStr = "N";
        String lonStr = "W";
        if (pos.latitude < 0) {
            latStr = "S";
        }

        if (pos.longitude > 0) {
            lonStr = "E";
        }

        return Math.abs(pos.latitude) + latStr +
               Math.abs(pos.longitude) + lonStr;
    }

    private String buildRequest(String origin, String destination) {
        return ("https://maps.googleapis.com/maps/api/directions/json?origin=" +
               origin +
               "&destination=" + destination).replaceAll(" ", "%20");
    }
}
