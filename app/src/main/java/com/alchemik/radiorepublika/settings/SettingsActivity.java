package com.alchemik.radiorepublika.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;

import com.alchemik.radiorepublika.R;
import com.alchemik.radiorepublika.SingleFragmentActivity;

/**
 * Created by Leszek Jasek on 29.03.2016.
 */
public class SettingsActivity extends SingleFragmentActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    public Fragment createFragment() {
        return new CustomPreferenceFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "LIFECYCLE onCreate() called");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false); //hide icon
        getSupportActionBar().setTitle("Ustawienia");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item_info).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }
}
