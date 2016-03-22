package com.alchemik.radiorepublika;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alchemik.radiorepublika.util.ConnectionUtil;
import com.devbrackets.android.exomedia.EMAudioPlayer;

import static android.telephony.TelephonyManager.CALL_STATE_IDLE;
import static android.telephony.TelephonyManager.CALL_STATE_RINGING;


public class MainActivity extends AppCompatActivity implements CustomWebViewClient.OnUrlOutOfScopeClickedListener{

    private final String TAG = this.getClass().getSimpleName();

    private static final String REPUBLIKA_RADIO_URL = "http://stream5.videostar.pl:1935/tvrepublika_audio/audio.stream/playlist.m3u8";
    private static final String REPUBLIKA_URL = "http://telewizjarepublika.pl/";

    public TelephonyManager telephonyManager;

    private ImageButton playerCloseBtn;
    private ImageButton playerMuteBtn;
    private ImageButton playerPlayPauseBtn;
    private EMAudioPlayer mEMAudioPlayer;

    private boolean shouldBePlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
        setContentView(R.layout.activity_main);

/*        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);*/

        playerPlayPauseBtn = (ImageButton) findViewById(R.id.player_play_pause);
        playerCloseBtn = (ImageButton) findViewById(R.id.player_close);
        playerMuteBtn = (ImageButton) findViewById(R.id.player_mute);

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        setupPlayer();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setListeners() {
        playerPlayPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Player: play/pause clicked");

                if (ConnectionUtil.isConnectedToNetwork(getApplicationContext())) {
                    playerToogle();
                } else {
                    showMessage(R.string.snackbar_no_internet_connection);
                }
            }
        });

        playerCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Player: close clicked");
                finish();
                System.exit(0);
            }
        });

        playerMuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Player: mute clicked");
            }
        });


        PhoneStateListener stateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
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
        Log.d(TAG, "playerToogle: mEMAudioPlayer.isPlaying() = " + mEMAudioPlayer.isPlaying() + ", shouldBePlaying = " + shouldBePlaying);
        if (mEMAudioPlayer.isPlaying() && shouldBePlaying) {
            stopPlayer();
        } else if (!shouldBePlaying){
            setupPlayer();
        }
    }

    public void stopPlayer() {
        mEMAudioPlayer.reset();
        Log.d(TAG, "stopPlayer(): EMAudioPlayer was stopped");

        playerPlayPauseBtn.setImageResource(R.drawable.ic_play_arrow_48dp);
        shouldBePlaying = false;
    }

    public void startPlayer() {
        mEMAudioPlayer.start();
        Log.d(TAG, "startPlayer(): EMAudioPlayer was started");

        playerPlayPauseBtn.setImageResource(R.drawable.ic_pause_48dp);
        shouldBePlaying = true;
    }

    private void setupPlayer() {
        //TODO: show spinner during loading EMAudioPlayer

        mEMAudioPlayer = AudioPlayer.getPlayer(this);
        mEMAudioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mEMAudioPlayer.setDataSource(this, Uri.parse(REPUBLIKA_RADIO_URL));
        mEMAudioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
                stopPlayer();
                return true;
            }

        });
        mEMAudioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //TODO: dismiss spinner after EMAudioPlayer loaded

                Log.i(TAG, "setupPlayer(): EUAudioPlayer is ready");

                playerPlayPauseBtn.setClickable(true);
                playerPlayPauseBtn.setImageResource(R.drawable.ic_play_arrow_48dp);
                startPlayer();
            }
        });
        mEMAudioPlayer.prepareAsync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEMAudioPlayer != null) {
            mEMAudioPlayer.reset();
            mEMAudioPlayer.release();
        }
    }

    public void showMessage(@StringRes int textMessage) {
        CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_coordinator_layout);
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, textMessage, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    @Override
    public void showInfoMessage(@StringRes int stringRes) {
        showMessage(stringRes);
    }
}
