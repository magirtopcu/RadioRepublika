package com.alchemik.radiorepublika.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.devbrackets.android.exomedia.EMAudioPlayer;

import static android.telephony.TelephonyManager.CALL_STATE_IDLE;
import static android.telephony.TelephonyManager.CALL_STATE_RINGING;

/**
 * Created by lesze on 3/20/2016.
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
