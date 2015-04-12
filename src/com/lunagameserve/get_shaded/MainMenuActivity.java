package com.lunagameserve.get_shaded;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.maps.model.LatLng;
import com.lunagameserve.get_shaded.directions.Directions;
import org.json.JSONException;

import java.io.IOException;

public class MainMenuActivity extends Activity {

    private LatLng currentLocation;
    private LocationManager locationManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        locationManager =
               (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    public void pushMapActivity(View view) throws IOException, JSONException {
        EditText et = (EditText)findViewById(R.id.editText);
        Directions directions =
                new Directions(currentLocation, et.getText().toString());

        System.out.println(directions.dirObj);

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());

            findViewById(R.id.button).setEnabled(true);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };
}
