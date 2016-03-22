package com.alchemik.radiorepublika.program;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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

import com.alchemik.radiorepublika.AudioPlayer;
import com.alchemik.radiorepublika.R;
import com.alchemik.radiorepublika.model.Track;
import com.alchemik.radiorepublika.parser.RadioProgramParser;
import com.alchemik.radiorepublika.service.RadioService;
import com.alchemik.radiorepublika.util.ConnectionUtil;
import com.devbrackets.android.exomedia.EMAudioPlayer;

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

    private TextView playerTitleTV;
    private TextView playerSubtitleTV;
    private TextView playerTimeTV;
    private ImageButton playerCloseBtn;
    private ImageButton playerMuteBtn;
    private ImageButton playerPlayPauseBtn;
    private ImageButton scheduleSyncBtn;

    private EMAudioPlayer mEMAudioPlayer;

    private boolean shouldBePlaying = false;


    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String LIFECYCLE = "Lifecycle";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private List<Track> mTrackList;
    private RecyclerView recyclerView;
    private MyTrackRecyclerViewAdapter mAdapter;

    private Handler handler;
    private Runnable updateTask;

    long nextUpdate = 1000;

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
    public static TrackListFragment newInstance(int columnCount) {
        TrackListFragment fragment = new TrackListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LIFECYCLE, "onCreate() called");

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        //if (ConnectionUtil.isConnectedToNetwork(getActivity())) {
        //    new AsyncScheduleParserTask().execute();
        //} else {
        //
        //}

    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.i(LIFECYCLE, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // Set the adapter
        //if (view instanceof RecyclerView) {
        //}
        Context context = view.getContext();
        //recyclerView = (RecyclerView) view;
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        playerPlayPauseBtn = (ImageButton) view.findViewById(R.id.player_play_pause);
        playerCloseBtn = (ImageButton) view.findViewById(R.id.player_close);
        playerMuteBtn = (ImageButton) view.findViewById(R.id.player_mute);
        scheduleSyncBtn = (ImageButton) view.findViewById(R.id.schedule_sync_button);
        playerTitleTV = (TextView) view.findViewById(R.id.player_title);
        playerSubtitleTV = (TextView) view.findViewById(R.id.player_subtitle);
        playerTimeTV = (TextView) view.findViewById(R.id.player_start_time);
        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Oswald.ttf");
        playerTitleTV.setTypeface(font);
        playerSubtitleTV.setTypeface(font);

        setButtonListeners();
        return view;
    }

    private void tryToConnect() {
        final Handler handler = new Handler();
        final int MAX_ATTEMPT = 3;
        handler.post(new Runnable() {
            int attempt = 0;

            @Override
            public void run() {
                Log.d(TAG, "run: attempt=" + attempt);
                ++attempt;
                if (!ConnectionUtil.isConnectedToNetwork(getActivity()) && attempt <= MAX_ATTEMPT) {
                    handler.postDelayed(this, 2000);
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
        // handler.removeCallbacks();

        //final int maxAttempts = 5;
//
        //for (int attempt = 0; attempt < maxAttempts; attempt++) {
        //    if (ConnectionUtil.isConnectedToNetwork(getActivity())) {
        //        scheduleSyncBtn.setVisibility(View.INVISIBLE);
        //        setupPlayer();
        //        setAudioListeners();
        //        new AsyncScheduleParserTask().execute();
        //    }
        //    wait();
        //}

        //
        //if (ConnectionUtil.isConnectedToNetwork(getActivity())) {
        //    scheduleSyncBtn.clearAnimation();
        //    scheduleSyncBtn.setVisibility(View.INVISIBLE);
        //    setupPlayer();
        //    setAudioListeners();
        //    new AsyncScheduleParserTask().execute();
        //} else {
        //    scheduleSyncBtn.setVisibility(View.VISIBLE);
        //    showConnectionErrorMsg()
        //}
    }

    @Override
    public void onStart() {
        super.onStart();
        tryToConnect();
        Log.i(LIFECYCLE, "onStart() called");

        if (playRadioIntent == null) {
            Log.d(TAG, "inside playRadioIntent == null clause");
            playRadioIntent = new Intent(getActivity(), RadioService.class);
            getActivity().bindService(playRadioIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playRadioIntent);
        }
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
    }

    private void updateUI() {
        mAdapter.notifyDataSetChanged();
        updateCurrentPlaying();
    }

    private void updateCurrentPlaying() {
        for (Track track : mTrackList) {
            if (track.isCurrentlyPlaying()) {
                playerTimeTV.setText(track.getStartTime());
                playerTitleTV.setText(track.getTrackTitle());
                playerSubtitleTV.setText(track.getTrackSubtitle());
            }
        }
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
    public void onDetach() {
        super.onDetach();
        Log.i(LIFECYCLE, "onDetach() called");
        mListener = null;
    }

    @Override
    public void onDestroy() {
        Log.i(LIFECYCLE, "onDestroy() called");
        //getActivity().stopService(playRadioIntent);
        getActivity().unbindService(serviceConnection);
        mRadioService = null;

        //timer.cancel();
        if (handler != null) {
            handler.removeCallbacks(updateTask);
        }
        super.onDestroy();
    }

    public static String convertMillisToHMS(long millis) {
        DateTimeZone timeZone = DateTimeZone.forID("CET");
        DateTime dateTime = new DateTime( millis, timeZone );
        DateTimeFormatter formatter = ISODateTimeFormat.hourMinuteSecond();
        return formatter.print(dateTime);
    }
    
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
                trackList.addAll(RadioProgramParser.parse());
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
            return trackList;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            Log.d("AsyncScheduleParserTask", "onPostExecute: tracks.size()" + tracks.size());
            mTrackList = tracks;
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
        playerPlayPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerToogle();
                //if (ConnectionUtil.isConnectedToNetwork(getActivity())) {
                //    playerToogle();
                //} else {
                //    //showMessage(R.string.snackbar_no_internet_connection);
                //}
            }
        });

        playerCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                System.exit(0);
            }
        });

        playerMuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        scheduleSyncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "scheduleSyncBtn clicked");
                Animation rotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.sync_button_rotation);
                scheduleSyncBtn.startAnimation(rotateAnim);
                tryToConnect();
            }
        });

    }

    private void setAudioListeners() {
        PhoneStateListener stateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                Log.d(TAG, "onCallStateChanged() called");
                if (state == CALL_STATE_RINGING) {
                    if (mEMAudioPlayer.isPlaying()) {
                        mEMAudioPlayer.pause();
                        Log.d(TAG, "onCallStateChanged: CALL_STATE_RINGING ~ AudioPLayer pause");
                    }
                } else if (state == CALL_STATE_IDLE) {
                    if (!mEMAudioPlayer.isPlaying() && shouldBePlaying) {
                        mEMAudioPlayer.start();
                        Log.d(TAG, "onCallStateChanged: CALL_STATE_IDLE ~ AudioPlayer start");
                    }
                }
            }
        };
        telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void playerToogle() {
        //Log.d(TAG, "playerToogle: mEMAudioPlayer.isPlaying() = " + mEMAudioPlayer.isPlaying() + ", shouldBePlaying = " + shouldBePlaying);
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

    public void stopPlayer() {
        mEMAudioPlayer.stopProgressPoll();
        mEMAudioPlayer.reset();
        Log.i(TAG, "stopPlayer(): EMAudioPlayer was stopped");

        playerPlayPauseBtn.setImageResource(R.drawable.ic_play_arrow_48dp);
        shouldBePlaying = false;
    }

    public void startPlayer() {
        mEMAudioPlayer.start();
        Log.i(TAG, "startPlayer(): EMAudioPlayer was started");

        playerPlayPauseBtn.setImageResource(R.drawable.ic_pause_48dp);
        shouldBePlaying = true;
    }

    private void setupPlayer() {

        final ProgressDialog playerProgessDialog = startProgressDailog(R.string.player_connecting_message);

        // TODO: move EMAudioPlayer to seperate class
        mEMAudioPlayer = AudioPlayer.getPlayer(getActivity());
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
