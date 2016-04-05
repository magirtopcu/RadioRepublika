package com.alchemik.radiorepublika.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.alchemik.radiorepublika.R;

/**
 * Created by Leszek Jasek on 29.03.2016.
 */
public class CustomPreferenceFragment extends PreferenceFragmentCompat {

    private static final String TAG = CustomPreferenceFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "LIFECYCLE onCreate() called");
        addPreferencesFromResource(R.xml.settings);
    }


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }
}