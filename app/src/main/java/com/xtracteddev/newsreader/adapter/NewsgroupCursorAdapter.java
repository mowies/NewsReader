package com.xtracteddev.newsreader.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.xtracteddev.newsreader.queries.MessageQueries;
import com.xtracteddev.newsreader.R;
import com.xtracteddev.newsreader.ShowNewsgroupsActivity;

public class NewsgroupCursorAdapter extends CursorAdapter {

    public NewsgroupCursorAdapter(Context context, Cursor cursor)  {
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_newsgroup, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTitle = (TextView) view.findViewById(R.id.group_title);
        TextView tvNewNews = (TextView) view.findViewById(R.id.new_news);
        String name = cursor.getString(ShowNewsgroupsActivity.COL_NEWSGROUP_NAME);
        String title = cursor.getString(ShowNewsgroupsActivity.COL_NEWSGROUP_TITLE);
        int newNewsCount= new MessageQueries(context).getNewMessagesCount(cursor.getLong(ShowNewsgroupsActivity.COL_NEWSGROUP_ID));
        int freshNewsCount = new MessageQueries(context).getFreshMessagesCount(cursor.getLong(ShowNewsgroupsActivity.COL_NEWSGROUP_ID));
        tvTitle.setText(TextUtils.isEmpty(title) ? name : title);
        tvNewNews.setText(Integer.toString(newNewsCount));
        tvNewNews.setVisibility(newNewsCount == 0 ? View.GONE : View.VISIBLE);
        tvNewNews.setBackgroundResource(freshNewsCount == 0 ? R.drawable.shape_new_messages : R.drawable.shape_fresh_messages);
    }



}
