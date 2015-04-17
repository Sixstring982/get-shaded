package com.lunagameserve.get_shaded.activitites;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lunagameserve.get_shaded.R;
import com.lunagameserve.get_shaded.directions.Directions;
import com.lunagameserve.get_shaded.light.LightGrid;
import com.lunagameserve.get_shaded.light.LightLine;

/**
 * @author Sixstring982
 * @since 4/16/2015
 */
public class CrunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crunch_layout);
        asyncGetDirections();
    }

    private void setProgressBar(final int value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pbar =
                        (ProgressBar) findViewById(R.id.crunchProgress);
                pbar.setProgress(value);
            }
        });
    }

    private void setText(final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv =
                        (TextView)findViewById(R.id.crunchText);
                if (tv != null) {
                    tv.setText(value);
                }
            }
        });
    }

    private void pushMapActivity(LatLngBounds bounds, LightLine lightLine) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("nelat", bounds.northeast.latitude);
        intent.putExtra("nelng", bounds.northeast.longitude);
        intent.putExtra("swlat", bounds.southwest.latitude);
        intent.putExtra("swlng", bounds.southwest.longitude);
        intent.putExtra("lightLine", lightLine);
        startActivity(intent);
    }

    private void asyncGetDirections() {
        new AsyncTask<Bundle, Integer, Directions>() {

            @Override
            protected Directions doInBackground(Bundle... extras) {
                try {
                    LatLng currentLocation = new LatLng(
                            extras[0].getDouble("latitude"),
                            extras[0].getDouble("longitude"));

                    setProgress(0);
                    Directions dirs = new Directions(
                            currentLocation,
                            extras[0].getString("destination"));

                    setProgress(33);
                    return dirs;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                setProgressBar(values[0]);
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
                    LightGrid g = new LightGrid(dirs[0].bounds);
                    setProgress(66);
                    return g;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                setProgressBar(values[0]);
            }

            @Override
            protected void onPreExecute() {
                setText("Generating Light Map");
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
                setText("Generating Light Line");
            }

            @Override
            protected void onPostExecute(LightLine lightLine) {
                /* Instead, push the other activity with the lightline
                * serialized. Might as well serialize to JSON because
                * GSON seems to be good at that. */
                //renderOnMap(directions, lightGrid, lightLine);
                pushMapActivity(directions.bounds, lightLine);
            }
        }.execute();
    }
}
