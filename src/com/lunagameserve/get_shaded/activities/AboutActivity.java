package com.lunagameserve.get_shaded.activities;

import android.app.Activity;
import android.os.Bundle;
import com.lunagameserve.get_shaded.R;

/**
 * Created by sixstring982 on 4/23/15.
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
