package org.random_access.newsreader.provider.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.random_access.newsreader.provider.NNTPProvider;

/**
 * <b>Project:</b> Newsreader for Android <br>
 * <b>Date:</b> 25.06.2015 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class MessageContract {
    private static final String TAG = ServerContract.class.getSimpleName();

    public static final String TABLE_NAME = "_TBL_MESSAGES";

    public static final Uri CONTENT_URI = Uri.parse("content://" + NNTPProvider.AUTHORITY + "/" + TABLE_NAME);

    // prevent instantiation
    private MessageContract(){}

    /**
     * Table name: _TBL_MESSAGES
     * <br>
     * Columns:
     * <ul>
     *      <li>_ID: int, PK, AI -> inherited from BaseColumns</li>
     *      <li>_MSG_ID: int NN-> message id string</li>
     *      <li>_SUBJECT: text NN-> message subject</li>
     *      <li>_DATE: integer jjjjmmtthhmmss NN -> message creation date & time</li>
     *      <li>_TIMEZONE: integer +/-zz00 NN -> time zone</li>
     *      <li>_NEW: integer {0,1} NN -> message read by user</li>
     *      <li>_IN_REPLY_TO: int -> references _TBL_MESSAGES._ID</li>
     *      <li>_FK_N_ID: int -> references _TBL_NEWSGROUPS._ID</li>
     * </ul>
     */
    public static abstract class MessageEntry implements BaseColumns {

        public static final String COL_MSG_ID = "_MSG_ID";
        public static final String COL_FROM_NAME = "_FROM_NAME";
        public static final String COL_FROM_EMAIL = "_FROM_EMAIL";
        public static final String COL_SUBJECT = "_SUBJECT";
        public static final String COL_CHARSET = "_CHARSET";
        public static final String COL_DATE = "_DATE";
        public static final String COL_NEW = "_NEW";
        public static final String COL_FK_N_ID = "_FK_N_ID";
        public static final String COL_HEADER = "_HEADER";
        public static final String COL_BODY = "_BODY";
        public static final String COL_LEFT_VALUE = "_LEFT_VALUE";
        public static final String COL_RIGHT_VALUE = "_RIGHT_VALUE";
        public static final String COL_PARENT_MSG = "_PARENT_MSG";
        public static final String COL_ROOT_MSG = "_ROOT_MSG";
        public static final String COL_LEVEL = "_LEVEL";
        public static final String COL_REFERENCES = "_REFERENCES";

        public static final String COL_ID_FULLNAME = TABLE_NAME + "." + _ID;
        public static final String COL_MSG_ID_FULLNAME = TABLE_NAME + "." + COL_MSG_ID;
        public static final String COL_FROM_NAME_FULLNAME = TABLE_NAME + "." + COL_FROM_NAME;
        public static final String COL_FROM_EMAIL_FULLNAME = TABLE_NAME + "." + COL_FROM_EMAIL;
        public static final String COL_SUBJECT_FULLNAME = TABLE_NAME + "." + COL_SUBJECT;
        public static final String COL_CHARSET_FULLNAME = TABLE_NAME + "." + COL_CHARSET;
        public static final String COL_DATE_FULLNAME = TABLE_NAME + "." + COL_DATE;
        public static final String COL_NEW_FULLNAME = TABLE_NAME + "." + COL_NEW;
        public static final String COL_FK_N_ID_FULLNAME = TABLE_NAME + "." + COL_FK_N_ID;
        public static final String COL_HEADER_FULLNAME = TABLE_NAME + "." + COL_HEADER;
        public static final String COLL_BODY_FULLNAME = TABLE_NAME + "." + COL_BODY;
        public static final String COL_LEFT_VALUE_FULLNAME = TABLE_NAME + "." + COL_LEFT_VALUE;
        public static final String COL_RIGHT_VALUE_FULLNAME = TABLE_NAME + "." + COL_RIGHT_VALUE;
        public static final String COL_PARENT_MSG_FULLNAME = TABLE_NAME + "." + COL_PARENT_MSG;
        public static final String COL_ROOT_MSG_FULLNAME = TABLE_NAME + "." + COL_ROOT_MSG;
        public static final String COL_LEVEL_FULLNAME = TABLE_NAME + "." + COL_LEVEL;
        public static final String COL_REFERENCES_FULLNAME = TABLE_NAME + "." + COL_REFERENCES;
    }

    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_NAME
            + "("
            + MessageEntry._ID + " integer primary key autoincrement, "
            + MessageEntry.COL_MSG_ID + " text not null, "
            + MessageEntry.COL_FROM_EMAIL + " text, "
            + MessageEntry.COL_FROM_NAME + " text, "
            + MessageEntry.COL_SUBJECT + " text not null, "
            + MessageEntry.COL_CHARSET + " text not null, "
            + MessageEntry.COL_DATE + " integer not null, "
            + MessageEntry.COL_NEW + " integer not null, "
            + MessageEntry.COL_FK_N_ID + " integer, "
            + MessageEntry.COL_HEADER + " text not null, "
            + MessageEntry.COL_BODY + " text, "
            + MessageEntry.COL_LEFT_VALUE + " integer not null, "
            + MessageEntry.COL_RIGHT_VALUE + " integer not null, "
            + MessageEntry.COL_PARENT_MSG + " integer not null, "
            + MessageEntry.COL_ROOT_MSG + " integer not null, "
            + MessageEntry.COL_LEVEL + " integer not null, "
            + MessageEntry.COL_REFERENCES + " text not null, "
            + "foreign key (" + MessageEntry.COL_FK_N_ID + ") references "
            +  NewsgroupContract.TABLE_NAME + " (" + NewsgroupContract.NewsgroupEntry._ID + ")"
            + ");";

    public static void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d(TAG, DATABASE_CREATE);
    }

    @SuppressWarnings("EmptyMethod")
    public static void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        // add upgrade procedure if necessary
    }

}
