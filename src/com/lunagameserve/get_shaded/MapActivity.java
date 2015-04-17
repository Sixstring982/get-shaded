package com.lunagameserve.get_shaded;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lunagameserve.get_shaded.directions.Directions;
import com.lunagameserve.get_shaded.light.LightGrid;
import com.lunagameserve.get_shaded.light.LightLine;

/**
 * Created by sixstring982 on 4/9/15.
 */
public class MapActivity extends Activity {
    private boolean mapInitialized = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        initializeMap();
    }

    private void initializeMap() {
        if (!mapInitialized) {
            mapInitialized = true;
            GoogleMap googleMap =
                    ((MapFragment) getFragmentManager()
                            .findFragmentById(R.id.map)).getMap();

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    asyncGetDirections();
                }
            });
        }
    }

    private void asyncGetDirections() {
        new AsyncTask<Bundle, Integer, Directions>() {

            @Override
            protected Directions doInBackground(Bundle... extras) {
                try {
                    LatLng currentLocation = new LatLng(
                            extras[0].getDouble("latitude"),
                            extras[0].getDouble("longitude"));

                    return new Directions(
                            currentLocation,
                            extras[0].getString("destination"));
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                Toast.makeText(getBaseContext(), "Downloading Directions...",
                               Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Directions directions) {
                asyncGenerateLightMap(directions);
            }
        }.execute(getIntent().getExtras());
    }

    private void asyncGenerateLightMap(final Directions directions) {
        new AsyncTask<Directions, Integer, LightGrid>() {

            @Override
            protected LightGrid doInBackground(Directions... dirs) {
                try {
                    return new LightGrid(dirs[0].bounds);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                Toast.makeText(getBaseContext(), "Generating LightGrid...",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(LightGrid lightGrid) {
                asyncGenerateLightLine(lightGrid, directions);
            }
        }.execute(directions);
    }

    private void asyncGenerateLightLine(final LightGrid lightGrid,
                                        final Directions directions) {
        new AsyncTask<Void, Integer, LightLine>() {
            @Override
            protected LightLine doInBackground(Void... params) {
                return new LightLine(lightGrid, directions.polyline);
            }

            @Override
            protected void onPreExecute() {
                Toast.makeText(getBaseContext(), "Generating LightLine...",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(LightLine lightLine) {
                renderOnMap(directions, lightGrid, lightLine);
            }
        }.execute();
    }

    private void renderOnMap(final Directions directions,
                             final LightGrid lightGrid,
                             final LightLine lightLine) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GoogleMap googleMap =
                        ((MapFragment) getFragmentManager()
                                .findFragmentById(R.id.map))
                                .getMap();

                //PolylineOptions pops = new PolylineOptions();
                //pops.addAll(directions.polyline);
                //pops.color(Color.RED);

                //googleMap.addPolyline(pops);
                lightLine.render(googleMap);

                googleMap.setMyLocationEnabled(true);
                //lightGrid.renderPolygons(googleMap);

                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                                directions.bounds, 32));
            }
        });
    }
}
