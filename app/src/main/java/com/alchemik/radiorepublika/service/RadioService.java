package com.alchemik.radiorepublika.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alchemik.radiorepublika.R;

/**
 * Created by Leszek Jasek on 22-Mar-16.
 */
public class RadioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    private static final String TAG = RadioService.class.getSimpleName();
    private static final int NOTIFY_ID = 1;
    private final IBinder radioBind = new RadioBinder();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() called");
        //create player
        try {
            initRadio();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared() called");
    }

    public void initRadio() throws InterruptedException {
        //The wake lock will let playback continue when the device becomes idle and we set the stream type to music
        //player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //player.setOnPreparedListener(this);
        //player.setOnCompletionListener(this);
        //player.setOnErrorListener(this);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setSmallIcon(R.drawable.republika_logo)
                .setTicker("Title")
                .setOngoing(true)
                .setContentTitle("Subtitle")
                .setContentText("Title2");
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
        for (int i = 0; i < 20; i++) {
            Log.d(TAG, "initRadio: " + i + "s");
            Thread.sleep(1000);
        }

        Log.d(TAG, "initRadio: called");
    }

/*    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
        stopForeground(true);
    }

    public class RadioBinder extends Binder {
        public RadioService getService() {
            return RadioService.this;
        }
    }
}
