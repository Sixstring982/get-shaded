package com.lunagameserve.get_shaded;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.lunagameserve.get_shaded.directions.Directions;
import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Created by sixstring982 on 4/9/15.
 */
public class MapActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        final GoogleMap googleMap =
                ((MapFragment)getFragmentManager()
                        .findFragmentById(R.id.map)).getMap();

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                final Bundle extras = getIntent().getExtras();

                try {
                    final LatLng currentLocation = new LatLng(
                            extras.getDouble("latitude"),
                            extras.getDouble("longitude"));
                    Directions directions =
                            Executors.newFixedThreadPool(16)
                                    .submit(new Callable<Directions>() {
                                        @Override
                                        public Directions call()
                                                throws IOException,
                                                       JSONException {
                                            return new Directions(
                                                    currentLocation,
                                                    extras
                                                    .getString("destination"));
                                        }
                                    }).get();

                    googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                    directions.bounds, 32));
                } catch (ExecutionException e) {
                    Log.e("ExecutionException", e.getMessage());
                } catch (InterruptedException e) {
                    Log.e("ExecutionException", e.getMessage());
                }
            }
        });
    }
}
