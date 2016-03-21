package com.alchemik.radiorepublika;

import android.content.Context;
import android.util.Log;

import com.devbrackets.android.exomedia.EMAudioPlayer;


public class AudioPlayer {

    private static final String TAG = AudioPlayer.class.getSimpleName();
    private static EMAudioPlayer mAudioPlayer;

    public static EMAudioPlayer getPlayer(Context context) {
        if (mAudioPlayer == null) {
            Log.d(TAG, "Create new EMAudioPlayer instance");
            mAudioPlayer = new EMAudioPlayer(context);
        }
        return mAudioPlayer;
    }

    private AudioPlayer() { }
}
