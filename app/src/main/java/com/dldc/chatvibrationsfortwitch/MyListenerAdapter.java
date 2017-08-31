package com.dldc.chatvibrationsfortwitch;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class MyListenerAdapter extends ListenerAdapter {
    private static final String TAG = "MyListenerAdapter";
    private Context mContext;
    private int num_vibrations;
    private int min_time_between;
    private long last_vibration_time;
    private Vibrator vibrator;

    MyListenerAdapter(Context context, int num_vibrations, int min_time_between) {
        this.mContext = context;
        this.num_vibrations = num_vibrations;
        this.min_time_between = min_time_between;
        last_vibration_time = 0;
        vibrator = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        Log.d(TAG, event.getMessage());

        long current_time = System.currentTimeMillis();
        if (last_vibration_time == 0 || (last_vibration_time + (min_time_between * 1000) <= current_time)) {
            last_vibration_time = current_time;

            if (num_vibrations == 1) {
                vibrator.vibrate(Constants.one_length);
            } else if (num_vibrations == 2) {
                vibrator.vibrate(Constants.two_pattern, -1);
            } else if (num_vibrations == 3) {
                vibrator.vibrate(Constants.three_pattern, -1);
            }
        }
    }

    @Override
    public void onEvent(Event event) throws Exception {
        Log.d(TAG, event.toString());
        super.onEvent(event);
    }
}
