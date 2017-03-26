package com.xtracteddev.newsreader.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtracteddev.newsreader.queries.MessageQueries;
import com.xtracteddev.newsreader.R;
import com.xtracteddev.newsreader.nntp.NNTPDateFormatter;

public class MessageFlatCursorAdapter extends CursorAdapter{

    private static final String TAG = MessageFlatCursorAdapter.class.getSimpleName();

    public MessageFlatCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_message_simple, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        boolean isNew = cursor.getInt(MessageQueries.COL_NEW) == 1;
        boolean isFresh = cursor.getInt(MessageQueries.COL_FRESH) == 1;

        TextView title = (TextView) view.findViewById(R.id.message_title);
        TextView date = (TextView) view.findViewById(R.id.message_date);
        TextView from = (TextView) view.findViewById(R.id.message_from);
        ImageView fresh = (ImageView) view.findViewById(R.id.message_fresh);

        title.setText(cursor.getString(MessageQueries.COL_SUBJECT));
        title.setTextColor(isNew ? ContextCompat.getColor(context, R.color.black) : ContextCompat.getColor(context, R.color.grey));
        date.setText(NNTPDateFormatter.getPrettyDateString(cursor.getLong(MessageQueries.COL_DATE), context));
        date.setTextColor(isNew ? ContextCompat.getColor(context, R.color.light_blue) : ContextCompat.getColor(context, R.color.dark_grey));
        from.setText(cursor.getString(MessageQueries.COL_FROM_NAME));
        from.setTextColor(isNew ? ContextCompat.getColor(context, R.color.black) : ContextCompat.getColor(context, R.color.dark_grey));
        fresh.setVisibility(isFresh ? View.VISIBLE : View.INVISIBLE);
    }
}
