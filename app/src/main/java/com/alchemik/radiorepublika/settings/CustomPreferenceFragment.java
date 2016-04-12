package com.alchemik.radiorepublika.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.Toast;

import com.alchemik.radiorepublika.R;
import com.alchemik.radiorepublika.about.AboutActivity;

/**
 * Created by Leszek Jasek on 29.03.2016.
 */
public class CustomPreferenceFragment extends PreferenceFragmentCompat {

    private static final String TAG = CustomPreferenceFragment.class.getSimpleName();
    private static final String EMAIL = "dev.alchemik@gmail.com";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "LIFECYCLE onCreate() called");
        addPreferencesFromResource(R.xml.settings);

        Preference.OnPreferenceClickListener preferenceClickListener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                switch (preference.getKey()) {
                    case "pref_sync":
                        Log.d(TAG, "onPreferenceClick: pref_sync");
                        Toast.makeText(getActivity(), "Zmiany zostaną wprowadzone po ponownym uruchomieniu aplikacji", Toast.LENGTH_SHORT).show();
                        break;
                    case "pref_send_feedback":
                        Log.d(TAG, "onPreferenceClick: pref_send_feedback");
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", EMAIL, null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.pref_feedback_subject));

                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(Intent.createChooser(intent, "Wyślij wiadomość..."));
                        } else {
                            Toast.makeText(getActivity(), "Brak aplikacji do obsługi poczty", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        };

        findPreference("pref_send_feedback").setOnPreferenceClickListener(preferenceClickListener);
        findPreference("pref_sync").setOnPreferenceClickListener(preferenceClickListener);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //
    }
}