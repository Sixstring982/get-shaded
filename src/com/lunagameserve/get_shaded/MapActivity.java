package com.lunagameserve.get_shaded;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

/**
 * Created by sixstring982 on 4/9/15.
 */
public class MapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        GoogleMap googleMap =
                ((MapFragment)getFragmentManager()
                        .findFragmentById(R.id.map)).getMap();

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }
}
