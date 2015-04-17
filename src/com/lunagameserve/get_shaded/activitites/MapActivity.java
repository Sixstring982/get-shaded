package com.lunagameserve.get_shaded.activitites;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lunagameserve.get_shaded.R;
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
                    startRendering();
                }
            });
        }
    }

    private void startRendering() {
        Bundle extras = getIntent().getExtras();
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(extras.getDouble("swlat"),
                           extras.getDouble("swlng")),
                new LatLng(extras.getDouble("nelat"),
                           extras.getDouble("nelng"))
        );

        LightLine lightLine = extras.getParcelable("lightLine");

        renderOnMap(bounds, lightLine);
    }

    private void renderOnMap(final LatLngBounds bounds,
                             final LightLine lightLine) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GoogleMap googleMap =
                        ((MapFragment) getFragmentManager()
                                .findFragmentById(R.id.map))
                                .getMap();
                lightLine.render(googleMap);

                googleMap.setMyLocationEnabled(true);

                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(bounds, 32));
            }
        });
    }
}
