package com.xtracteddev.newsreader.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NNTPAuthenticatorService extends Service {

    private NNTPServerAuthenticator mAuthenticator;

    public void onCreate() {
        mAuthenticator = new NNTPServerAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
