package com.alchemik.radiorepublika.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devbrackets.android.exomedia.EMAudioPlayer;

/**
 * Created by Leszek Jasek on 3/20/2016.
 */
public class PhoneServiceReceiver extends BroadcastReceiver {

    private static final String TAG = PhoneServiceReceiver.class.getSimpleName();
    private Context mContext;
    private EMAudioPlayer mEMAudioPlayer;

    public PhoneServiceReceiver(Context context, EMAudioPlayer audioPlayer) {
        mContext = context;
        mEMAudioPlayer = audioPlayer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
