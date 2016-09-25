package com.alchemik.radiorepublika;

import android.content.Context;
import android.util.Log;

import com.devbrackets.android.exomedia.EMAudioPlayer;


public class AudioPlayer {

    private static final String TAG = AudioPlayer.class.getSimpleName();
    private static EMAudioPlayer mAudioPlayer;

    private AudioPlayer() { }

    public static EMAudioPlayer getPlayer(Context context) {

        //if (mAudioPlayer == null) {
        //    // Thread safe, might be costly operation
        //    synchronized (AudioPlayer.class) {
        //        if (mAudioPlayer == null) {
        //            Log.d(TAG, "Create new EMAudioPlayer instance");
        //            mAudioPlayer = new EMAudioPlayer(context);
        //        }
        //    }
        //}

        if (mAudioPlayer == null) {
            Log.d(TAG, "Create new EMAudioPlayer instance");
            mAudioPlayer = new EMAudioPlayer(context);
        }
        return mAudioPlayer;
    }
}
