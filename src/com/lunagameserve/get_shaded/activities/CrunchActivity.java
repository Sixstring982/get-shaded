package com.lunagameserve.get_shaded.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lunagameserve.get_shaded.R;
import com.lunagameserve.get_shaded.directions.Directions;
import com.lunagameserve.get_shaded.light.LightGrid;
import com.lunagameserve.get_shaded.light.LightLine;
import com.lunagameserve.get_shaded.settings.Settings;
import com.lunagameserve.get_shaded.util.StringUtil;

import java.io.File;
import java.io.IOException;

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

    private void pushMapActivity(LatLngBounds bounds,
                                 LightGrid grid,
                                 LightLine lightLine) {
        /* If more than one route was found, let's push a route
        *  selection activity.*/

        /* Put settings */
        Settings settings = new Settings(getFilesDir());
        Intent intent = new Intent(getBaseContext(), MapActivity.class);

        intent.putExtra("grid",
                settings.isRenderGrid());
        intent.putExtra("points",
                settings.isRenderPoints());

        intent.putExtra("nelat",
                bounds.northeast.latitude);
        intent.putExtra("nelng",
                bounds.northeast.longitude);
        intent.putExtra("swlat",
                bounds.southwest.latitude);
        intent.putExtra("swlng",
                bounds.southwest.longitude);
        intent.putExtra("lightLine",
                lightLine);

        /* Write light grid to drive, will be read later */
        try {
            grid.write(new File(getFilesDir(), "lg.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        startActivity(intent);

        finish();
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
                if (directions.isDirectionsFound()) {
                    Toast.makeText(
                            getBaseContext(),
                            StringUtil.pluralize("route",
                                    directions.polylines.size()) +
                            " found.",
                            Toast.LENGTH_LONG).show();

                    asyncGenerateLightMap(directions);
                } else {
                    Toast.makeText(getBaseContext(),
                                   "No directions found. Try a street " +
                                   "intersection along with your city name.",
                                   Toast.LENGTH_LONG).show();
                    finish();
                }
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
                LightLine l = new LightLine(lightGrid, directions.polyline);
                setProgressBar(100);
                return l;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                setProgressBar(values[0]);
            }

            @Override
            protected void onPreExecute() {
                setText("Generating Light Line");
            }

            @Override
            protected void onPostExecute(LightLine lightLine) {
                pushMapActivity(directions.bounds, lightGrid, lightLine);
            }
        }.execute();
    }
}
