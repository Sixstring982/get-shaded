package com.lunagameserve.get_shaded.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
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

    private LightGrid grid = null;

    private boolean renderGrid;
    private boolean renderPoints;

    private GoogleMap map;

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
            this.map =
                    ((MapFragment) getFragmentManager()
                            .findFragmentById(R.id.map)).getMap();

            this.map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    renderOnMap();
                }
            });
        }
    }

    private void zoomToBounds() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                map.setMyLocationEnabled(true);

                map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(bounds, 32));

                renderPoints();
            }
        });
    }

    private void renderPoints() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (renderPoints) {
                    try {
                        grid = LightGrid.read(
                                new File(getFilesDir(), "lg.json"));
                        grid.renderRecords(map);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                renderGrid();
            }
        });
    }

    private void renderGrid() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (renderGrid) {
                    try {
                        if (grid == null) {
                            grid = LightGrid.read(
                                    new File(getFilesDir(), "lg.json"));
                        }
                        grid.renderPolygons(map);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                renderLightLine();
            }
        });
    }

    private void renderLightLine() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (lightLine.mostLightMissing()) {
                    Toast.makeText(getBaseContext(),
                            "This route is missing data. Consider using " +
                                    "DataGather to help us compute its " +
                                    "shadiness in the future.", Toast
                                    .LENGTH_LONG).show();
                }


                lightLine.render(map);
            }
        });
    }

    private void renderOnMap() {
        zoomToBounds();
    }
}
