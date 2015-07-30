package org.random_access.newsreader.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.random_access.newsreader.R;
import org.random_access.newsreader.nntp.NNTPDateFormatter;
import org.random_access.newsreader.queries.MessageQueries;

/**
 * <b>Project:</b> FlashcardsManager for Android <br>
 * <b>Date:</b> 26.07.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class MessageHierarchyCursorAdapter extends CursorAdapter {

    private static final String TAG = MessageHierarchyCursorAdapter.class.getSimpleName();

    public MessageHierarchyCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_message_parent, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        boolean isNew = cursor.getInt(MessageQueries.COL_NEW) == 1 || cursor.getInt(MessageQueries.COL_NEW) == -1;
            // -1: root messages which have unread children -> should be shown as new

        TextView title = (TextView) view.findViewById(R.id.message_title);
        TextView date = (TextView) view.findViewById(R.id.message_date);
        TextView from = (TextView) view.findViewById(R.id.message_from);
        ImageView imgChildren = (ImageView) view.findViewById(R.id.img_children);

        title.setText(cursor.getString(MessageQueries.COL_SUBJECT));
        title.setTextColor(isNew ? context.getResources().getColor(R.color.black) : context.getResources().getColor(R.color.grey));
        date.setText(NNTPDateFormatter.getPrettyDateString(cursor.getLong(MessageQueries.COL_DATE), context));
        date.setTextColor(isNew ? context.getResources().getColor(R.color.light_blue) : context.getResources().getColor(R.color.dark_grey));
        from.setText(cursor.getString(MessageQueries.COL_FROM_NAME));
        from.setTextColor(isNew ? context.getResources().getColor(R.color.black) : context.getResources().getColor(R.color.dark_grey));
        boolean isParent = new MessageQueries(context).hasMessageChildren(cursor.getLong(MessageQueries.COL_ID));
        imgChildren.setVisibility(isParent ? View.VISIBLE : View.INVISIBLE);
        imgChildren.setColorFilter(context.getResources().getColor(R.color.light_blue));
    }

}
