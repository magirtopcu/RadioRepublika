package com.alchemik.radiorepublika.program;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.alchemik.radiorepublika.SingleFragmentActivity;

/**
 * Created by Leszek Jasek on 3/20/2016.
 */
public class TrackListActivity extends SingleFragmentActivity implements TrackListFragment.OnListFragmentInteractionListener{

    @Override
    public Fragment createFragment() {
        return TrackListFragment.newInstance();
    }

    @Override
    public void showMessage(@StringRes int textMessage) {
        Toast.makeText(this, textMessage, Toast.LENGTH_SHORT).show();
    }

}
