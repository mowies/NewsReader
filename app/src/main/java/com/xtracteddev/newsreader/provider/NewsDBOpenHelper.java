package com.xtracteddev.newsreader.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xtracteddev.newsreader.provider.contracts.MessageContract;
import com.xtracteddev.newsreader.provider.contracts.NewsgroupContract;
import com.xtracteddev.newsreader.provider.contracts.ServerContract;
import com.xtracteddev.newsreader.provider.contracts.SettingsContract;

class NewsDBOpenHelper extends SQLiteOpenHelper{


    private static final String TAG = NewsDBOpenHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "NEWSREADER.db";

    public NewsDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates all databases if not existing
     * @param db the sqlite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        SettingsContract.onCreate(db);
        ServerContract.onCreate(db);
        NewsgroupContract.onCreate(db);
        MessageContract.onCreate(db);
        Log.d(TAG, "Finished onCreate in NewsDBOpenHelper");
    }

    /**
     * Starts update routines in all tables
     * @param db the sqlite database
     * @param oldVersion current version of database
     * @param newVersion new version of database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SettingsContract.onUpdate(db, oldVersion, newVersion);
        ServerContract.onUpdate(db, oldVersion, newVersion);
        NewsgroupContract.onUpdate(db, oldVersion, newVersion);
        MessageContract.onUpdate(db, oldVersion, newVersion);
        Log.d(TAG, "Finished onUpdate in NewsDBOpenHelper");
    }
}
