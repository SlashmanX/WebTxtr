package com.slashmanx.webtxtr;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by emartin on 04/12/2013.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
