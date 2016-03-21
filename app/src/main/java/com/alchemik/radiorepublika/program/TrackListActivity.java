package com.alchemik.radiorepublika.program;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.alchemik.radiorepublika.R;
import com.alchemik.radiorepublika.SingleFragmentActivity;
import com.alchemik.radiorepublika.dummy.DummyContent;
import com.alchemik.radiorepublika.model.Track;

/**
 * Created by lesze on 3/20/2016.
 */
public class TrackListActivity extends SingleFragmentActivity implements TrackListFragment.OnListFragmentInteractionListener{

    @Override
    public Fragment createFragment() {
        return new TrackListFragment();
    }

    @Override
    public void onListFragmentInteraction(Track track) {
        Toast.makeText(this, track.toString(), Toast.LENGTH_SHORT).show();
    }

}
