package com.alchemik.radiorepublika.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Leszek Jasek on 22-Mar-16.
 */
public class RadioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    private static final String TAG = RadioService.class.getSimpleName();
    private final IBinder radioBind = new RadioBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate() called");
        //create player
        initRadio();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called");
        return radioBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // release radio resources
        // player stop
        // player release
        Log.d(TAG, "onUnbind() called");
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion() called");

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared() called");
    }

    public void initRadio() {
        //The wake lock will let playback continue when the device becomes idle and we set the stream type to music
        //player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //player.setOnPreparedListener(this);
        //player.setOnCompletionListener(this);
        //player.setOnErrorListener(this);
        Log.d(TAG, "initRadio: called");
    }

/*    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }*/


    public class RadioBinder extends Binder {
        public RadioService getService() {
            return RadioService.this;
        }
    }
}
