package com.xtracteddev.newsreader;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;

import com.xtracteddev.newsreader.sync.NNTPSyncAdapter;

public class NewsgroupObserver extends ContentObserver {

    private static final String TAG = NewsgroupObserver.class.getSimpleName();

    public NewsgroupObserver() {
        super(null);
    }

    @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Bundle extras = new Bundle();
        extras.putString(NNTPSyncAdapter.SYNC_REQUEST_ORIGIN, TAG);
        extras.putBoolean(NNTPSyncAdapter.SYNC_REQUEST_TAG, true);
        ContentResolver.requestSync(ShowServerActivity.ACCOUNT, ShowServerActivity.AUTHORITY, extras);
    }
}
