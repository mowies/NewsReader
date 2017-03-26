package com.xtracteddev.newsreader.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xtracteddev.newsreader.queries.MessageQueries;


public class NotificationDismissReceiver extends BroadcastReceiver{

    private static final String TAG = NotificationDismissReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        new MessageQueries(context).markAllMessagesNonFresh();
        Log.i(TAG, "User dismissed fresh messages notification.");
    }
}
