package com.slashmanx.webtxtr;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by emartin on 04/12/2013.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}