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

    public final LatLngBounds bounds;

    public final List<List<LatLng>> polylines;

    public final List<LatLng> polyline;

    private final boolean directionsFound;

    public Directions(LatLng origin, LatLng destination)
            throws IOException, JSONException {
        this(latLngString(origin), latLngString(destination));
    }

    public Directions(String origin, String destination)
            throws IOException, JSONException {
        String request = buildRequest(origin, destination);
        JSONObject dirObj = new JSONObject(IOUtil.webRequestContents(request));

        if (dirObj.getJSONArray("routes").length() > 0) {
            bounds = generateBounds(dirObj);
            polylines = generatePolylines(dirObj);
            polyline = polylines.get(0);
            directionsFound = true;
        } else {
            bounds = null;
            polyline = null;
            polylines = null;
            directionsFound = false;
        }
    }

    public Directions(String origin, LatLng destination)
            throws IOException, JSONException {
        this(origin, latLngString(destination));
    }

    public Directions(LatLng origin, String destination)
            throws IOException, JSONException {
        this(latLngString(origin), destination);
    }

    public boolean isDirectionsFound() {
        return directionsFound;
    }

    private List<List<LatLng>> generatePolylines(JSONObject dirObj)
            throws JSONException {

        List<List<LatLng>> paths = new ArrayList<List<LatLng>>();

        JSONArray routes = dirObj.getJSONArray("routes");
        for (int i = 0; i < routes.length(); i++) {
            List<LatLng> pathPts = new ArrayList<LatLng>();
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
            paths.add(pathPts);
        }

        return paths;
    }

    private LatLngBounds generateBounds(JSONObject dirObj)
            throws JSONException {
        List<Double> lats = new ArrayList<Double>();
        List<Double> lngs = new ArrayList<Double>();

        JSONArray routes = dirObj.getJSONArray("routes");
        for (int i = 0; i < routes.length(); i++) {
            JSONObject bounds = routes.getJSONObject(i).getJSONObject("bounds");

            JSONObject ne = bounds.getJSONObject("northeast");
            lats.add(ne.getDouble("lat"));
            lngs.add(ne.getDouble("lng"));

            JSONObject sw = bounds.getJSONObject("southwest");
            lats.add(sw.getDouble("lat"));
            lngs.add(sw.getDouble("lng"));
        }

        double minLon = lngs.get(0);
        double maxLon = lngs.get(0);
        for (double d : lngs) {
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
        return ("https://maps.googleapis.com/maps/api/directions/json?" +
               "origin=" + origin +
               "&destination=" + destination +
               "&mode=walking").replaceAll(" ", "%20");
    }
}
