package com.lunagameserve.get_shaded.directions;

import com.google.android.gms.maps.model.LatLng;
import com.lunagameserve.get_shaded.io.IOUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public class Directions {

    public JSONObject dirObj;

    public Directions(LatLng origin, LatLng destination)
            throws IOException, JSONException {
        this(latLngString(origin), latLngString(destination));
    }

    public Directions(String origin, String destination)
            throws IOException, JSONException {
        String request = buildRequest(origin, destination);
        dirObj = new JSONObject(IOUtil.webRequestContents(request));
    }

    public Directions(String origin, LatLng destination)
            throws IOException, JSONException {
        this(origin, latLngString(destination));
    }

    public Directions(LatLng origin, String destination)
            throws IOException, JSONException {
        this(latLngString(origin), destination);
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
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" +
               origin +
               "&destination=" + destination;
    }
}
