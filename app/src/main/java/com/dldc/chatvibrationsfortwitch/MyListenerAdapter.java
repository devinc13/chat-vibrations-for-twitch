package com.dldc.chatvibrationsfortwitch;

import android.content.Context;
import android.os.Vibrator;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class MyListenerAdapter extends ListenerAdapter {
    private static final String TAG = "MyListenerAdapter";
    private Context mContext;

    MyListenerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        Vibrator v = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }
}
