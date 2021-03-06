package com.xtracteddev.newsreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xtracteddev.newsreader.EditSubscriptionsActivity;
import com.xtracteddev.newsreader.NetworkStateHelper;
import com.xtracteddev.newsreader.ServerSettingsActivity;
import com.xtracteddev.newsreader.ShowServerActivity;
import com.xtracteddev.newsreader.queries.NewsgroupQueries;
import com.xtracteddev.newsreader.R;


public class ServerCursorAdapter extends CursorAdapter {

    private static final int COLLAPSED = 0;
    private static final int EXTENDED = 1;
    private int mCurrentDetailPosition = -1;

    // resources
    private TextView txtTitle;
    private TextView txtName;
    private TextView txtSubscriptions;
    private ImageButton btnEditServerSettings;
    private ImageButton btnEditSubscriptions;

    private static final String TAG = ServerCursorAdapter.class.getSimpleName();

    private final Resources res;

    @SuppressWarnings("SameParameterValue")
    public ServerCursorAdapter(Context context, Cursor cursor)  {
        super(context, cursor, 0);
        res = context.getResources();
    }

    @Override
    public int getItemViewType(int position) {
        return EXTENDED;
        // TODO maybe remove this
       // return (position == mCurrentDetailPosition) ? EXTENDED : COLLAPSED;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final int position = cursor.getPosition();
        final int type = getItemViewType(position);
        switch(type) {
            case EXTENDED:
                return LayoutInflater.from(context).inflate(R.layout.item_server_extended, parent, false);
            default: // collapsed
                return LayoutInflater.from(context).inflate(R.layout.item_server, parent, false);
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int position = cursor.getPosition();
        final int type = getItemViewType(position);
        findResources(type, view, context);
        assignValuesToViewElems(context, type, cursor);
        setListeners(type, cursor, context);
    }

    private void findResources(int type, View parent, Context context) {
        switch (type) {
            case EXTENDED:
                txtTitle = (TextView) parent.findViewById(R.id.id_server_title_extended);
                txtName = (TextView) parent.findViewById(R.id.id_server_name_extended);
                txtSubscriptions = (TextView) parent.findViewById(R.id.id_subscriptions_extended);
                btnEditServerSettings = (ImageButton) parent.findViewById(R.id.btn_edit_server);
                btnEditServerSettings.setColorFilter(ContextCompat.getColor(context, R.color.light_blue));
                btnEditSubscriptions = (ImageButton) parent.findViewById(R.id.btn_edit_subscriptions);
                btnEditSubscriptions.setColorFilter(ContextCompat.getColor(context, R.color.light_blue));
                break;
            default:
                txtTitle = (TextView) parent.findViewById(R.id.id_server_title);
        }
    }

    private void assignValuesToViewElems(Context context, int type, Cursor cursor) {
        String title = cursor.getString(ShowServerActivity.COL_SERVER_TITLE);
        String name = cursor.getString(ShowServerActivity.COL_SERVER_NAME);
        int noOfSubscriptions = new NewsgroupQueries(context).getNewsgroupCountFromServer(cursor.getInt(ShowServerActivity.COL_SERVER_ID));
        switch (type) {
            case EXTENDED:
                txtTitle.setText(title);
                // txtName.setText(res.getString(R.string.server) + ": " + name);
                txtName.setText(String.format(res.getString(R.string.serverName), name));
                // txtSubscriptions.setText(res.getString(R.string.subscriptions) + ": " + noOfSubscriptions);
                txtSubscriptions.setText(String.format(res.getString(R.string.subscriptions), noOfSubscriptions));
                break;
            default:
                txtTitle.setText(title);
        }
    }

    private void setListeners(int type, Cursor cursor, final Context context) {
        if (type == EXTENDED) {
            final long id = cursor.getLong(ShowServerActivity.COL_SERVER_ID);
            final String title = cursor.getString(ShowServerActivity.COL_SERVER_TITLE);
            btnEditSubscriptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetworkStateHelper.isOnline(context)) {
                        Toast.makeText(context, context.getResources().getString(R.string.error_offline), Toast.LENGTH_SHORT).show();
                    } else {
                        //ContentResolver.cancelSync(null, null);
                        Intent intent = new Intent(context, EditSubscriptionsActivity.class);
                        intent.putExtra(EditSubscriptionsActivity.KEY_SERVER_ID, id);
                        context.startActivity(intent);
                    }
                }
            });
            btnEditServerSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ServerSettingsActivity.class);
                    intent.putExtra(ServerSettingsActivity.TAG_SERVER_ID, id);
                    context.startActivity(intent);
                }
            });
        }
    }

    public int getmCurrentDetailPosition() {
        return mCurrentDetailPosition;
    }

    public void setmCurrentDetailPosition(int mCurrentDetailPosition) {
        this.mCurrentDetailPosition = mCurrentDetailPosition;
        notifyDataSetChanged();
    }

}
