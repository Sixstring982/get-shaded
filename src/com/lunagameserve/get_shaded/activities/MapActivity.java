package com.lunagameserve.get_shaded.activities;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lunagameserve.get_shaded.R;
import com.lunagameserve.get_shaded.light.LightGrid;
import com.lunagameserve.get_shaded.light.LightLine;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/**
 * Created by sixstring982 on 4/9/15.
 */
public class MapActivity extends Activity {
    private boolean mapInitialized = false;

    private LatLngBounds bounds;

    private LightLine lightLine;

    private boolean renderGrid;
    private boolean renderPoints;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        bounds = new LatLngBounds(
                new LatLng(getIntent().getExtras().getDouble(
                        "swlat"),
                           getIntent().getExtras().getDouble(
                        "swlng")),
                new LatLng(getIntent().getExtras().getDouble(
                        "nelat"),
                           getIntent().getExtras().getDouble(
                        "nelng"))
        );

        lightLine =
                getIntent().getParcelableExtra(
                        "lightLine");

        renderGrid = getIntent().getBooleanExtra("grid", false);
        renderPoints = getIntent().getBooleanExtra("points", false);

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
                    renderOnMap();
                }
            });
        }
    }

    private void renderOnMap() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GoogleMap googleMap =
                        ((MapFragment) getFragmentManager()
                                .findFragmentById(R.id.map))
                                .getMap();

                LightGrid grid = null;


                if (renderPoints) {
                    try {
                        grid = LightGrid.read(
                                new File(getFilesDir(), "lg.json"));
                        grid.renderRecords(googleMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (renderGrid) {
                    try {
                        if (grid == null) {
                            grid = LightGrid.read(
                                    new File(getFilesDir(), "lg.json"));
                        }
                        grid.renderPolygons(googleMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                lightLine.render(googleMap);

                googleMap.setMyLocationEnabled(true);

                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(bounds, 32));
            }
        });
    }
}
