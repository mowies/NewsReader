package com.xtracteddev.newsreader.queries;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

class QueryHelper {

    public static int count(Context context, Uri uri,String selection,String[] selectionArgs) {
        Cursor cursor = context.getContentResolver().query(uri,new String[] {"count(*) AS count"},
                selection, selectionArgs, null);
        int result = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
            cursor.close();
        }
        return result;
    }

    public static String makePlaceholderArray(int length) {
        if (length < 1) {
            throw new RuntimeException("No placeholders");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("?, ");
        }
        sb.replace(sb.length()-2, sb.length(), "");
        return sb.toString();
    }
}
