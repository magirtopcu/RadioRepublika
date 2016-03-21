package com.alchemik.radiorepublika.program;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alchemik.radiorepublika.R;
import com.alchemik.radiorepublika.mock.Playlist;
import com.alchemik.radiorepublika.parser.RadioProgramParser;
import com.alchemik.radiorepublika.model.Track;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TrackListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String LIFECYCLE = "Lifecycle";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private List<Track> mTrackList;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private MyTrackRecyclerViewAdapter mAdapter;

    private Handler handler;
    private Runnable updateTask;

    long nextUpdate = 1000;
    //Timer timer;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrackListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TrackListFragment newInstance(int columnCount) {
        TrackListFragment fragment = new TrackListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LIFECYCLE, "onCreate() called");

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        new AsyncScheduleParser().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.i(LIFECYCLE, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LIFECYCLE, "onResume() called");
    }

    private void setupTimer() {
        Log.d("TIMER", "setupTimer() ");

        handler = new Handler();
        updateTask = new Runnable() {
            @Override
            public void run() {
                updateUI();
                Log.d(getString(R.string.app_name) + " Timer.updateTask()", "updateTask run!");
                handler.postDelayed(this, scheduleNextUpdate());
            }
        };
        handler.post(updateTask);

        //long currentTime = System.currentTimeMillis();
        //timer = new Timer();
        //TimerTask timerTask = new TimerTask() {
        //    @Override
        //    public void run() {
        //        //updateUI();
        //        Log.d("TIMER", "run: started");
        //    }
        //};
        //try {
        //    //Date timeInMillis = new SimpleDateFormat("HH:mm", Locale.GERMANY).parse("14:41");
        //    Date timeInMillis = new SimpleDateFormat("HH:mm").parse("14:50");
        //    Log.d("TIMER", "setupTimer: " + timeInMillis);
        //    timer.schedule(timerTask, timeInMillis, 2000);
        //} catch (ParseException e) {
        //    e.printStackTrace();
        //}
    }

    private void updateUI() {
        // FIXME: nullpointer!
        mAdapter.notifyDataSetChanged();
    }

    /**
     *  Set timer for specific time in order to update UI
     */
    private long scheduleNextUpdate() {
        for (Track track : mTrackList) {
            Log.d("TIMER", "scheduleNextUpdate: track=" + track.getTrackTitle());
            if (track.isCurrentlyPlaying()) {
                long nextUpdateInMillis = track.getEndDateTime() - System.currentTimeMillis();
                Log.d("TIMER", "scheduleNextUpdate on " + nextUpdateInMillis);
                Log.d("TIMER", "scheduleNextUpdate on " + new SimpleDateFormat("kk:mm:ss:SSS", Locale.getDefault()).format(nextUpdateInMillis));
                Log.d("TIMER", "scheduleNextUpdate on " + convertMillisToHMS(nextUpdateInMillis));
                return nextUpdateInMillis;
            }
        }
        return 1000000000;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(LIFECYCLE, "onAttach() called");

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LIFECYCLE, "onDetach() called");
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LIFECYCLE, "onDestroy() called");

        //timer.cancel();
        handler.removeCallbacks(updateTask);
    }

    public static String convertMillisToHMS(long millis) {
        DateTimeZone timeZone = DateTimeZone.forID("CET");
        DateTime dateTime = new DateTime( millis, timeZone );
        DateTimeFormatter formatter = ISODateTimeFormat.hourMinuteSecond();
        return formatter.print(dateTime);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Track track);
    }

    private class AsyncScheduleParser extends AsyncTask<Void, Void , List<Track>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Pobieram program...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
            progressDialog.show();
        }

        @Override
        protected List<Track> doInBackground(Void... list) {
            Log.d("AsyncScheduleParser", "doInBackground: ");
            List<Track> trackList = new ArrayList<>();
            try {
                trackList.addAll(RadioProgramParser.parse());
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return trackList;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            Log.d("AsyncScheduleParser", "onPostExecute: tracks.size()" + tracks.size());
            //mTrackList = tracks;
            mTrackList = Playlist.generate();
            mAdapter = new MyTrackRecyclerViewAdapter(getActivity(), mTrackList, mListener);
            recyclerView.setAdapter(mAdapter);
            progressDialog.dismiss();
            recyclerView.scrollToPosition(20);

            setupTimer();
        }
    }

}
