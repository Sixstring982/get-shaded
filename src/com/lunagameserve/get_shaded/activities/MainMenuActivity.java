package com.lunagameserve.get_shaded.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.maps.model.LatLng;
import com.lunagameserve.get_shaded.R;

public class MainMenuActivity extends Activity {

    private int calibrationPointsLeft = 1;
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

    public void pushMapActivity(View view) {
        final EditText et = (EditText)findViewById(R.id.editText);

        Intent intent = new Intent(this, CrunchActivity.class);
        intent.putExtra("latitude", currentLocation.latitude);
        intent.putExtra("longitude", currentLocation.longitude);
        intent.putExtra("destination", et.getText().toString());
        startActivity(intent);
    }

    private void pushSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        locationManager.removeUpdates(locationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainMenuAboutItem:
                /* TODO Show about screen, talk about DG */
                return true;
            case R.id.mainMenuSettingsItem:
                pushSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Button goButton = (Button)findViewById(R.id.button);
            if (calibrationPointsLeft > 1) {
                calibrationPointsLeft--;
                String ptsStr = "point" +
                        ((calibrationPointsLeft == 1) ? "" : "s");
                goButton.setText(calibrationPointsLeft +
                        " calibration " + ptsStr + " left...");
            } else {
                currentLocation = new LatLng(location.getLatitude(),
                        location.getLongitude());

                goButton.setText("Go!");
                goButton.setEnabled(true);
            }
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
