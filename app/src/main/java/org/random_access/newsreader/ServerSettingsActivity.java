package org.random_access.newsreader;

import android.app.FragmentManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.random_access.newsreader.queries.ServerQueries;
import org.random_access.newsreader.queries.SettingsQueries;

/**
 * <b>Project:</b> Newsreader for Android <br>
 * <b>Date:</b> 18.08.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class ServerSettingsActivity extends AppCompatActivity {

    private static final String TAG = ServerSettingsActivity.class.getSimpleName();

    public static final String TAG_SERVER_ID = "server-id";
    private long serverId;

    private static final String TAG_SERVER_SETTINGS = "server-settings";

    private ServerSettingsFragment serverSettingsFragment;
    private EditText txtServerTitle, txtServer, txtPort, txtUserName, txtPassword, txtUserDisplayName, txtEmailAddress, txtSignature;
    private CheckBox chkAuth;
    private Spinner spMsgLoadPeriod;
    private TextView lblUser, lblPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serverId = getIntent().getExtras().getLong(TAG_SERVER_ID);
        setContentView(R.layout.activity_server_settings);
        loadServerSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_server_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
               modifyServerSettings();
                return true;
            case R.id.action_discard:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void modifyServerSettings() {
        if (TextUtils.isEmpty(txtServer.getText().toString())) {
            txtServer.setError(getResources().getString(R.string.error_empty_field));
            txtServer.requestFocus();
        } else if (serverSettingsFragment.isAuth() && TextUtils.isEmpty(txtUserName.getText().toString())) {
            txtUserName.setError(getResources().getString(R.string.error_empty_field));
            txtUserName.requestFocus();
        } else if (TextUtils.isEmpty(txtEmailAddress.getText().toString())) {
            txtEmailAddress.setError(getResources().getString(R.string.error_empty_field));
            txtEmailAddress.requestFocus();
        } else {
            if (serverSettingsFragment != null) updateSettingsFragment();
            ServerQueries serverQueries = new ServerQueries(ServerSettingsActivity.this);
            int serverPort = TextUtils.isEmpty(serverSettingsFragment.getServerPort()) ? 119 : Integer.parseInt(serverSettingsFragment.getServerPort());
            serverQueries.modifyServer(serverId, serverSettingsFragment.getServerTitle(), serverSettingsFragment.getServerName(),
                    serverPort, false, serverSettingsFragment.isAuth(), serverSettingsFragment.getUserName(), serverSettingsFragment.getPassword());
            long settingsId = serverQueries.getServerSettingsId(serverId);
            int msgKeepTime = getResources().getIntArray(R.array.sync_period_values)[spMsgLoadPeriod.getSelectedItemPosition()];
            SettingsQueries settingsQueries = new SettingsQueries(ServerSettingsActivity.this);
            settingsQueries.modifySettingsEntry(settingsId, serverSettingsFragment.getUserDisplayName(), serverSettingsFragment.getMailAddress(), serverSettingsFragment.getSignature(), msgKeepTime);
            Toast.makeText(this, getResources().getString(R.string.success_modifying_server), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverSettingsFragment != null) {
            updateSettingsFragment();
        }
    }

    private void updateSettingsFragment() {
        serverSettingsFragment.setServerTitle(txtServerTitle.getText().toString());
        serverSettingsFragment.setServerName(txtServer.getText().toString());
        serverSettingsFragment.setServerPort(txtPort.getText().toString());
        serverSettingsFragment.setAuth(chkAuth.isChecked());
        serverSettingsFragment.setUserName(txtUserName.getText().toString());
        serverSettingsFragment.setPassword(txtPassword.getText().toString());
        serverSettingsFragment.setUserDisplayName(txtUserDisplayName.getText().toString());
        serverSettingsFragment.setMailAddress(txtEmailAddress.getText().toString());
        serverSettingsFragment.setSignature(txtSignature.getText().toString());
        serverSettingsFragment.setChooseMsgLoadTimeIndex(spMsgLoadPeriod.getSelectedItemPosition());
    }

    private void loadServerSettings() {
        FragmentManager fragmentManager = getFragmentManager();
        serverSettingsFragment = (ServerSettingsFragment)fragmentManager.findFragmentByTag(TAG_SERVER_SETTINGS);
        if (serverSettingsFragment == null) {
            serverSettingsFragment = new ServerSettingsFragment();
            fragmentManager.beginTransaction().add(serverSettingsFragment, TAG_SERVER_SETTINGS).commit();
            if (serverId != -1) {
                new LoadServerSettingsTask().execute();
            }
        } else {
            prepareGui();
        }
    }

    class LoadServerSettingsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ServerQueries serverQueries = new ServerQueries(ServerSettingsActivity.this);
            Cursor c = serverQueries.getServerWithId(serverId);
            if (c.moveToFirst()) {
                serverSettingsFragment.setServerTitle(c.getString(ServerQueries.COL_TITLE));
                serverSettingsFragment.setServerName(c.getString(ServerQueries.COL_NAME));
                serverSettingsFragment.setServerPort(c.getString(ServerQueries.COL_PORT));
                serverSettingsFragment.setAuth(c.getInt(ServerQueries.COL_AUTH) == 1);
                serverSettingsFragment.setUserName(c.getString(ServerQueries.COL_USER));
                serverSettingsFragment.setPassword(c.getString(ServerQueries.COL_PASSWORD));
            }
            c.close();
            SettingsQueries settingsQueries = new SettingsQueries(ServerSettingsActivity.this);
            c = settingsQueries.getSettingsForServer(serverId);
            if (c.moveToFirst()) {
                serverSettingsFragment.setUserDisplayName(c.getString(SettingsQueries.COL_NAME));
                serverSettingsFragment.setMailAddress(c.getString(SettingsQueries.COL_EMAIL));
                serverSettingsFragment.setSignature(c.getString(SettingsQueries.COL_SIGNATURE));
                serverSettingsFragment.setChooseMsgLoadTimeIndex(findIndexOfValue(c.getInt(SettingsQueries.COL_MSG_LOAD_DEFAULT)));
            }
            c.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            prepareGui();
        }
    }

    private int findIndexOfValue(int value) {
        int[] array = getResources().getIntArray(R.array.sync_period_values);
        for (int i = 0; i < array.length; i++ ) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    private void prepareGui() {
        txtServerTitle = (EditText) findViewById(R.id.txt_servertitle);
        txtServer = (EditText) findViewById(R.id.txt_server);
        txtPort = (EditText) findViewById(R.id.txt_port);
        chkAuth = (CheckBox) findViewById(R.id.chk_auth);
        lblUser = (TextView) findViewById(R.id.lbl_user);
        txtUserName = (EditText) findViewById(R.id.txt_user);
        lblPassword = (TextView) findViewById(R.id.lbl_pass);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        txtUserDisplayName = (EditText) findViewById(R.id.txt_name);
        txtEmailAddress = (EditText) findViewById(R.id.txt_email);
        txtSignature = (EditText) findViewById(R.id.txt_signature);
        spMsgLoadPeriod = (Spinner) findViewById(R.id.rg_msgload);

        txtServerTitle.setText(serverSettingsFragment.getServerTitle() == null ? "" : serverSettingsFragment.getServerTitle() );
        txtServer.setText(serverSettingsFragment.getServerName() == null ? "" : serverSettingsFragment.getServerName());
        txtPort.setText(serverSettingsFragment.getServerPort() == null ? "" : serverSettingsFragment.getServerPort());
        chkAuth.setChecked(serverSettingsFragment.isAuth());
        manageAuthVisibility(chkAuth.isChecked());
        txtUserName.setText(serverSettingsFragment.getUserName() == null ? "" : serverSettingsFragment.getUserName());
        txtPassword.setText(serverSettingsFragment.getPassword() == null ? "" : serverSettingsFragment.getPassword());
        txtUserDisplayName.setText(serverSettingsFragment.getUserDisplayName() == null ? "" : serverSettingsFragment.getUserDisplayName());
        txtEmailAddress.setText(serverSettingsFragment.getMailAddress() == null ? "" : serverSettingsFragment.getMailAddress());
        txtSignature.setText(serverSettingsFragment.getSignature() == null ? "" : serverSettingsFragment.getSignature());
        spMsgLoadPeriod.setSelection(serverSettingsFragment.getChooseMsgLoadTimeIndex());

        chkAuth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                manageAuthVisibility(isChecked);
            }
        });
    }

    private void manageAuthVisibility(boolean isChecked) {
        lblUser.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        txtUserName.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        lblPassword.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        txtPassword.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }
}
