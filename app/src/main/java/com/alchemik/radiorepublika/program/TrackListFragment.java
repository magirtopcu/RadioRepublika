package com.alchemik.radiorepublika.program;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alchemik.radiorepublika.R;
import com.alchemik.radiorepublika.mock.Playlist;
import com.alchemik.radiorepublika.model.Track;
import com.alchemik.radiorepublika.parser.ScheduleParser;
import com.alchemik.radiorepublika.service.RadioService;
import com.alchemik.radiorepublika.util.ConnectionUtil;
import com.alchemik.radiorepublika.views.CustomFontTextView;
import com.devbrackets.android.exomedia.EMAudioPlayer;

import org.jsoup.helper.StringUtil;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.telephony.TelephonyManager.CALL_STATE_IDLE;
import static android.telephony.TelephonyManager.CALL_STATE_RINGING;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TrackListFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private static final String REPUBLIKA_RADIO_URL = "http://stream5.videostar.pl:1935/tvrepublika_audio/audio.stream/playlist.m3u8";

    public TelephonyManager telephonyManager;
    public ConnectivityManager connectivityManager;

    @Bind(R.id.player_title)
    public CustomFontTextView playerTitleTV;
    @Bind(R.id.player_subtitle)
    public CustomFontTextView playerSubtitleTV;
    @Bind(R.id.player_start_time)
    public TextView playerTimeTV;
    @Bind(R.id.player_end_time)
    public TextView playerEndTimeTV;
    @Bind(R.id.player_close)
    public ImageButton playerCloseBtn;
    @Bind(R.id.player_play_pause)
    public ImageButton playerPlayPauseBtn;
    @Bind(R.id.schedule_sync_button)
    public ImageButton scheduleSyncBtn;

    private EMAudioPlayer mEMAudioPlayer;

    private boolean shouldBePlaying = false;


    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String LIFECYCLE = "Lifecycle";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    @Bind(R.id.recycler_view)
    public RecyclerView recyclerView;
    private List<Track> mTrackList;
    private MyTrackRecyclerViewAdapter mAdapter;

    //private Handler handler;
    //private Runnable updateTask;

    private RadioService mRadioService;
    private Intent playRadioIntent;
    private boolean radioBound = false;

    //Timer timer;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TrackListFragment newInstance() {
        TrackListFragment fragment = new TrackListFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_COLUMN_COUNT, columnCount);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "LIFECYCLE onAttach() called");

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "LIFECYCLE onCreate() called");

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


/*
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() ");
            RadioService.RadioBinder binder = (RadioService.RadioBinder)service;
            //get service
            mRadioService = binder.getService();
            //pass list
            //mRadioService.setList(songList);
            radioBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() ");
            radioBound = false;
        }
    };
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.i(TAG, "LIFECYCLE onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        ButterKnife.bind(this, view);

        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        setButtonListeners();
        tryToConnect();
        return view;
    }

    private void tryToConnect() {
        Log.d(TAG, "tryToConnect: before connectionHandler");
        final Handler connectionHandler = new Handler();
        final int MAX_ATTEMPT = 3;

        connectionHandler.post(new Runnable() {
            int attempt = 0;

            @Override
            public void run() {
                Log.d(TAG, "run: attempt=" + attempt);
                ++attempt;

                if (!ConnectionUtil.isConnectedToNetwork(getActivity()) && attempt <= MAX_ATTEMPT) {
                    connectionHandler.postDelayed(this, 2000);
                } else if (ConnectionUtil.isConnectedToNetwork(getActivity())){
                    scheduleSyncBtn.clearAnimation();
                    scheduleSyncBtn.setVisibility(View.INVISIBLE);
                    setupPlayer();
                    setAudioListeners();
                    new AsyncScheduleParserTask().execute();
                } else {
                    scheduleSyncBtn.setVisibility(View.VISIBLE);
                    showConnectionErrorMsg();
                }
            }
        });
        // FIXME
        Log.d(TAG, "setupTimer: after connectionHandler = " + connectionHandler);
        // connectionHandler.removeCallbacks();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "LIFECYCLE onStart() called");

/*        if (playRadioIntent == null) {
            Log.d(TAG, "inside playRadioIntent == null clause");
            playRadioIntent = new Intent(getActivity(), RadioService.class);
            getActivity().bindService(playRadioIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playRadioIntent);
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "LIFECYCLE onResume() called");
    }

    private void setupTimer() {
        Log.d("TIMER", "setupTimer() ");

        final Handler handler = new Handler();
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                updateUI();
                Log.d(getString(R.string.app_name) + " Timer.updateTask()", "updateTask run!");
                handler.postDelayed(this, scheduleNextUpdate());
            }
        };
        handler.post(updateTask);
        Log.d(TAG, "setupTimer: after handler");
    }

    private void updateUI() {
        mAdapter.notifyDataSetChanged();
        updateCurrentPlaying();
    }

    private void updateCurrentPlaying() {
        for (int i = 0; i < mTrackList.size() ; i++) {
            Track track = mTrackList.get(i);
            if (track.isCurrentlyPlaying()) {
                playerTimeTV.setText(track.getStartTime());
                playerTitleTV.setText(track.getTrackTitle());
                if (StringUtil.isBlank(track.getTrackSubtitle())) {
                    playerSubtitleTV.setVisibility(View.GONE);
                } else {
                    playerSubtitleTV.setText(track.getTrackSubtitle());
                }
                if (i < mTrackList.size() - 1) {
                    playerEndTimeTV.setText(mTrackList.get(i + 1).getStartTime());
                }
            }
        }
    }

    /**
     *  Set timer for specific time in order to update UI
     */
    private long scheduleNextUpdate() {
        for (Track track : mTrackList) {
            if (track.isCurrentlyPlaying()) {
                long nextUpdateInMillis = track.getEndDateTime() - System.currentTimeMillis();
                Log.i("TIMER", "Currently playing: " + track.getTrackTitle() + ", schedule next update on " + new SimpleDateFormat("kk:mm:ss:SSS", Locale.getDefault()).format(nextUpdateInMillis));
                return nextUpdateInMillis;
            }
        }
        return 1000000000;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "LIFECYCLE onDetach() called");
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "LIFECYCLE onDestroy() called mEMAudioPlayer=" + mEMAudioPlayer);
        //getActivity().stopService(playRadioIntent);
        if (mEMAudioPlayer != null) {
            stopPlayer();
            mEMAudioPlayer.release();
            mEMAudioPlayer = null;
        }

        //getActivity().unbindService(serviceConnection);
        //mRadioService = null;

        //timer.cancel();
        //if (handler != null) {
        //    handler.removeCallbacks(updateTask);
        //}
        super.onDestroy();
    }

    /*
     * An AsyncTask is not tied to the life cycle of the Activity that contains it. So, for example, if you start an AsyncTask inside an Activity and the user rotates the device, the Activity will be destroyed (and a new Activity instance will be created) but the AsyncTask will not die but instead goes on living until it completes.
     *
     * Then, when the AsyncTask does complete, rather than updating the UI of the new Activity, it updates the former instance of the Activity (i.e., the one in which it was created but that is not displayed anymore!). This can lead to an Exception (of the type java.lang.IllegalArgumentException: View not attached to window manager if you use, for instance, findViewById to retrieve a view inside the Activity).
     *
     * There’s also the potential for this to result in a memory leak since the AsyncTask maintains a reference to the Activty, which prevents the Activity from being garbage collected as long as the AsyncTask remains alive.
     *
     * For these reasons, using AsyncTasks for long-running background tasks is generally a bad idea . Rather, for long-running background tasks, a different mechanism (such as a service) should be employed.
     */
    private class AsyncScheduleParserTask extends AsyncTask<Void, Void , List<Track>> {

        ProgressDialog scheduleProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            scheduleProgressDialog = startProgressDailog(R.string.player_download_schedule_message);
        }

        @Override
        protected List<Track> doInBackground(Void... list) {
            Log.d("AsyncScheduleParserTask", "doInBackground: ");
            List<Track> trackList = new ArrayList<>();
            try {
                trackList.addAll(ScheduleParser.parse());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return trackList;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            Log.d("AsyncScheduleParserTask", "onPostExecute: tracks.size()" + tracks.size());
            mTrackList = tracks;
            /* MOCK */
            //mTrackList = Playlist.generate();
            mAdapter = new MyTrackRecyclerViewAdapter(getActivity(), mTrackList, mListener);
            recyclerView.setAdapter(mAdapter);

            //recyclerView.scrollToPosition(20);
            if (tracks.isEmpty()) {
                Toast.makeText(getActivity(), "Nie udało się pobrać programu", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "onPostExecute: track is not empty");
                stopProgressDialog(scheduleProgressDialog);
                setupTimer();
                updateCurrentPlaying();
            }
        }

    }
    private void setButtonListeners() {

        //playerPlayPauseBtn.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        playerToogle();
        //    }
        //});

        //playerCloseBtn.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        closeApplication();
        //    }
        //});


        //scheduleSyncBtn.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        synchronizeSchedule();
        //    }
        //});

    }

    @OnClick(R.id.schedule_sync_button)
    public void synchronizeSchedule() {
        Log.d(TAG, "scheduleSyncBtn clicked");
        Animation rotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.sync_button_rotation);
        scheduleSyncBtn.startAnimation(rotateAnim);
        tryToConnect();
    }

    @OnClick(R.id.player_close)
    public void closeApplication() {
        onDestroy();
        //FIXME: find better solution
        getActivity().finish();
        System.exit(0);
    }

    @OnClick(R.id.player_play_pause)
    public void playerToogle() {
        if (mEMAudioPlayer != null && mEMAudioPlayer.isPlaying() && shouldBePlaying) {
            stopPlayer();
        } else if (!shouldBePlaying) {
            if (ConnectionUtil.isConnectedToNetwork(getActivity())) {
                setupPlayer();
            } else {
                showConnectionErrorMsg();
            }
        }
    }

    private void setAudioListeners() {
        PhoneStateListener stateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                Log.d(TAG, "onCallStateChanged() called");
                if (state == CALL_STATE_RINGING) {
                    if (mEMAudioPlayer.isPlaying()) {
                        mEMAudioPlayer.pause();
                        Log.i(TAG, "onCallStateChanged: CALL_STATE_RINGING ~ AudioPLayer pause");
                    }
                } else if (state == CALL_STATE_IDLE) {
                    if (!mEMAudioPlayer.isPlaying() && shouldBePlaying) {
                        mEMAudioPlayer.start();
                        Log.i(TAG, "onCallStateChanged: CALL_STATE_IDLE ~ AudioPlayer start");
                    }
                }
            }
        };
        telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void stopPlayer() {
        if (mEMAudioPlayer != null) {
            mEMAudioPlayer.stopProgressPoll();
            mEMAudioPlayer.reset();
            playerPlayPauseBtn.setImageResource(R.drawable.ic_play_arrow_48dp);
            Log.i(TAG, "stopPlayer(): EMAudioPlayer was stopped");
        }
        shouldBePlaying = false;
    }

    public void startPlayer() {
        mEMAudioPlayer.start();
        Log.i(TAG, "startPlayer(): EMAudioPlayer was started");

        playerPlayPauseBtn.setImageResource(R.drawable.ic_pause_48dp);
        shouldBePlaying = true;
    }

    //FIXME: avoid player recreation on config (screen) changes
    private void setupPlayer() {

        final ProgressDialog playerProgessDialog = startProgressDailog(R.string.player_connecting_message);

        // TODO: move EMAudioPlayer to seperate class
        Log.d(TAG, "setupPlayer() before mEMAudioPlayer=" + mEMAudioPlayer);
        if (mEMAudioPlayer == null) {
            mEMAudioPlayer = new EMAudioPlayer(getActivity());
        }
        Log.d(TAG, "setupPlayer() after mEMAudioPlayer=" + mEMAudioPlayer);
        mEMAudioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mEMAudioPlayer.setDataSource(getActivity(), Uri.parse(REPUBLIKA_RADIO_URL));
        mEMAudioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                showConnectionErrorMsg();
                stopPlayer();
                return true;
            }

        });
        mEMAudioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "setupPlayer(): EUAudioPlayer is ready");

                playerPlayPauseBtn.setClickable(true);
                playerPlayPauseBtn.setImageResource(R.drawable.ic_play_arrow_48dp);
                startPlayer();
                stopProgressDialog(playerProgessDialog);
            }
        });
    }

    /**
     * @param stringRes Message id to be displayed
     */
    private ProgressDialog startProgressDailog(@StringRes int stringRes) {
        Log.d(TAG, "startProgressDailog: start");
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getString(stringRes));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
        progressDialog.show();
        return progressDialog;
    }

    private void stopProgressDialog(ProgressDialog progressDialog) {
        Log.d(TAG, "stopProgressDialog: start ~ progressDialog=" + progressDialog);
        progressDialog.dismiss();
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void showMessage(@StringRes int textMessage);
    }

    private void showConnectionErrorMsg() {
        Toast.makeText(getActivity(), R.string.connection_error_message, Toast.LENGTH_SHORT).show();
    }
}
