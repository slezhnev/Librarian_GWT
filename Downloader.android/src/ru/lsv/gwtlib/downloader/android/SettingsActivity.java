package ru.lsv.gwtlib.downloader.android;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     // Display the fragment as the main content.
        setContentView(R.layout.activity_settings);
    }

}
