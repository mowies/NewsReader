package org.random_access.newsreader.queries;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.random_access.newsreader.provider.contracts.DBJoins;
import org.random_access.newsreader.provider.contracts.MessageContract;
import org.random_access.newsreader.provider.contracts.MessageHierarchyContract;

import java.util.Arrays;

/**
 * <b>Project:</b> Newsreader for Android <br>
 * <b>Date:</b> 25.06.2015 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class MessageQueries {

    private static final String TAG = MessageQueries.class.getSimpleName();
    private final Context context;

    private static final String[] PROJECTION_MESSAGE = new String[] {MessageContract.MessageEntry._ID, MessageContract.MessageEntry.COL_MSG_ID,
            MessageContract.MessageEntry.COL_FROM_EMAIL, MessageContract.MessageEntry.COL_FROM_NAME, MessageContract.MessageEntry.COL_SUBJECT,
            MessageContract.MessageEntry.COL_CHARSET, MessageContract.MessageEntry.COL_DATE,MessageContract.MessageEntry.COL_NEW,
            MessageContract.MessageEntry.COL_FK_N_ID, MessageContract.MessageEntry.COL_HEADER, MessageContract.MessageEntry.COL_BODY };

    public static final int COL_ID = 0;
    public static final int COL_MSG_ID = 1;
    public static final int COL_FROM_EMAIL = 2;
    public static final int COL_FROM_NAME = 3;
    public static final int COL_SUBJECT = 4;
    public static final int COL_CHARSET = 5;
    public static final int COL_DATE = 6;
    public static final int COL_NEW = 7;
    public static final int COL_FK_N_ID = 8;
    public static final int COL_HEADER = 9;
    public static final int COL_BODY = 10;

    public MessageQueries(Context context) {
        this.context = context;
    }

    public CursorLoader getMessagesInCursorLoader(long newsgroupId, boolean onlyTopItems) {
        if (onlyTopItems) {
            String childString = new MessageHierarchyQueries(context).getChildrenListAsString();
            Log.d(TAG, childString);
            return new CursorLoader(context, MessageContract.CONTENT_URI, PROJECTION_MESSAGE, MessageContract.MessageEntry.COL_FK_N_ID + " = ? AND "
                    + MessageContract.MessageEntry._ID + " NOT IN " + childString, new String[]{newsgroupId + ""}, MessageContract.MessageEntry.COL_DATE + " DESC");
        } else {
            return new CursorLoader(context, MessageContract.CONTENT_URI, PROJECTION_MESSAGE, MessageContract.MessageEntry.COL_FK_N_ID + " = ?", new String[]{newsgroupId + ""},
                    MessageContract.MessageEntry.COL_DATE + " DESC");
        }
    }

    public Cursor getMessagesOfNewsgroup(long newsgroupId) {
        return context.getContentResolver().query(MessageContract.CONTENT_URI, PROJECTION_MESSAGE, MessageContract.MessageEntry.COL_FK_N_ID + " = ?", new String[] {newsgroupId + ""},
                MessageContract.MessageEntry.COL_DATE + " DESC");
    }

    public long getIdFromMessageId(String messageId) {
        long id = -1;
        Cursor c = context.getContentResolver().query(MessageContract.CONTENT_URI, PROJECTION_MESSAGE, MessageContract.MessageEntry.COL_MSG_ID + " = ?",
                new String[] {messageId}, null);
        if (c.moveToFirst()) {
            id = c.getLong(COL_ID);
        }
        c.close();
        return  id;
    }

    public Cursor getMessageWithId(long messageId) {
        return context.getContentResolver().query(MessageContract.CONTENT_URI, PROJECTION_MESSAGE, MessageContract.MessageEntry._ID + " = ?", new String[]{messageId + ""},
                null);
    }

    public boolean isMessageInDatabase(String messageId) {
        Cursor c = context.getContentResolver().query(MessageContract.CONTENT_URI, PROJECTION_MESSAGE, MessageContract.MessageEntry.COL_MSG_ID + " = ?", new String[]{messageId + ""}, null);
        boolean result = c.moveToFirst();
        c.close();
        return result;
    }

    public String getMessageIdFromId(long id) {
        String messageId = null;
        Cursor c = getMessageWithId(id);
        if (c.moveToFirst()) {
            messageId = c.getString(COL_MSG_ID);
        }
        c.close();
        return messageId;
    }

    public int getNewMessagesCount(long newsgroupId) {
        return QueryHelper.count(context, MessageContract.CONTENT_URI, MessageContract.MessageEntry.COL_FK_N_ID + " = ? AND " + MessageContract.MessageEntry.COL_NEW + " = ?",
                new String[]{newsgroupId + "", "1"});
    }

    public boolean setMessageUnread(long messageId, boolean isNew) {
        int value = isNew ? 1 : 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageContract.MessageEntry.COL_NEW, value);
        return context.getContentResolver().update(MessageContract.CONTENT_URI, contentValues, MessageContract.MessageEntry._ID + " = ? ",
                new String[] {messageId + ""}) > 0;
    }


    public boolean addMessage(String messageId, String fromEmail, String fromName, String subject, String charset, long date, int isNew,
                              long newsgroupId, String header, String body, long[] refIds) {
        // insert message
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageContract.MessageEntry.COL_MSG_ID, messageId);
        contentValues.put(MessageContract.MessageEntry.COL_FROM_EMAIL, fromEmail);
        contentValues.put(MessageContract.MessageEntry.COL_FROM_NAME, fromName);
        contentValues.put(MessageContract.MessageEntry.COL_SUBJECT, subject);
        contentValues.put(MessageContract.MessageEntry.COL_CHARSET, charset);
        contentValues.put(MessageContract.MessageEntry.COL_DATE, date);
        contentValues.put(MessageContract.MessageEntry.COL_NEW, isNew);
        contentValues.put(MessageContract.MessageEntry.COL_FK_N_ID, newsgroupId);
        contentValues.put(MessageContract.MessageEntry.COL_HEADER, header);
        contentValues.put(MessageContract.MessageEntry.COL_BODY, body);
        contentValues.put(MessageContract.MessageEntry.COL_LEFT_VALUE, 0); // TODO
        contentValues.put(MessageContract.MessageEntry.COL_RIGHT_VALUE, 0); // TODO
        contentValues.put(MessageContract.MessageEntry.COL_PARENT_MSG, 0); // TODO
        contentValues.put(MessageContract.MessageEntry.COL_ROOT_MSG, 0); // TODO
        Uri msgUri = Uri.parse(context.getContentResolver().insert(MessageContract.CONTENT_URI, contentValues).getLastPathSegment());
        long msgId = Long.parseLong(msgUri.getLastPathSegment());
        // insert message relations
        for (long l : refIds) {
            ContentValues cvMsgHierarchy = new ContentValues();
            cvMsgHierarchy.put(MessageHierarchyContract.MessageHierarchyEntry.COL_MSG_DB_ID, msgId);
            cvMsgHierarchy.put(MessageHierarchyContract.MessageHierarchyEntry.COL_IN_REPLY_TO, l);
            context.getContentResolver().insert(MessageHierarchyContract.CONTENT_URI, cvMsgHierarchy);
        }
        return true;
    }

    /**
     * Deletes all messages with COL_FK_N_ID = newsgroupId
     * @param newsgroupId ID of a newsgroup
     */
    public  void deleteMessagesFromNewsgroup(long newsgroupId) {
        MessageHierarchyQueries messageHierarchyQueries = new MessageHierarchyQueries(context);
        Cursor cursor = getMessagesOfNewsgroup(newsgroupId);
        if (cursor.moveToFirst()) {
            while(!cursor.isAfterLast()) {
                messageHierarchyQueries.deleteEntriesFromMessageIds(cursor.getLong(COL_ID));
                cursor.moveToNext();
            }
        }
        cursor.close();
        int delCount = context.getContentResolver().delete(MessageContract.CONTENT_URI, MessageContract.MessageEntry.COL_FK_N_ID + " = ? ", new String[] {newsgroupId + ""});
        Log.i(TAG, delCount + " rows deleted");
    }

     /* public String getReplyMessageIdString(long[] refIds) {
        StringBuilder sb = new StringBuilder();
        String refIdString = Arrays.toString(refIds).replace('[', '(').replace(']', ')');
        Cursor c = context.getContentResolver().query(MessageContract.CONTENT_URI, PROJECTION_MESSAGE, MessageContract.MessageEntry._ID + " in " + refIdString, null,
                MessageContract.MessageEntry.COL_DATE + " ASC");
        if (c.moveToFirst()){
            while(!c.isAfterLast()) {
                sb.append(c.getString(COL_MSG_ID)).append(" ");
            }
            sb.replace(sb.length()-1, sb.length(), "");
        }
        c.close();
        return sb.toString();
       Cursor cursor = context.getContentResolver().query(DBJoins.CONTENT_URI_MESSAGE_JOIN_MESSAGEHIERARCHY_ROOT, new String[] {MessageContract.MessageEntry.COL_MSG_ID},
                MessageContract.MessageEntry.COL_MSG_ID + " = ? ", new String[]{messageId + ""}, MessageContract.MessageEntry.COL_DATE + " ASC");
        StringBuilder sb = new StringBuilder();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                sb.append(cursor.getString(0)).append(" ");
            }
            sb.replace(sb.length()-1, sb.length(), "");
        }
        cursor.close();
        return sb.toString();
    } */
}
