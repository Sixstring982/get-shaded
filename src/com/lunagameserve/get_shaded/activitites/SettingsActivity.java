package com.lunagameserve.get_shaded.activitites;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import com.lunagameserve.get_shaded.R;
import com.lunagameserve.get_shaded.settings.Settings;
import org.json.JSONException;

import java.io.IOException;

/**
 * @author Sixstring982
 * @since 4/17/2015
 */
public class SettingsActivity extends Activity {

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        /* Load settings from drive */
        settings = new Settings(getFilesDir());

        ((CheckBox)findViewById(R.id.settingsDataPointCheckbox))
                .setChecked(settings.isRenderPoints());

        ((CheckBox)findViewById(R.id.settingsGridCheckbox))
                .setChecked(settings.isRenderGrid());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        boolean success = true;

        try {
            settings.save();
            Toast.makeText(getBaseContext(), "Settings file saved.",
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            success = false;
        } catch (JSONException e) {
            success = false;
        }
        if (!success) {
            Toast.makeText(getBaseContext(),
                           "Settings file could not be saved.",
                    Toast.LENGTH_LONG).show();
        }

        finish();
    }

    public void clickLightGridBox(View view) {
        settings.setRenderGrid(((CheckBox)view).isChecked());
    }

    public void clickDataPointBox(View view) {
        settings.setRenderPoints(((CheckBox)view).isChecked());
    }
}
