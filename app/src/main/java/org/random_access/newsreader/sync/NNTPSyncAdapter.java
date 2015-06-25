package org.random_access.newsreader.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import org.apache.commons.net.nntp.ArticleInfo;
import org.apache.commons.net.nntp.NNTPClient;
import org.apache.commons.net.nntp.NewGroupsOrNewsQuery;
import org.random_access.newsreader.NetworkStateHelper;
import org.random_access.newsreader.nntp.CustomNNTPClient;
import org.random_access.newsreader.nntp.NNTPMessageHeader;
import org.random_access.newsreader.nntp.NNTPDateFormatter;
import org.random_access.newsreader.queries.MessageQueries;
import org.random_access.newsreader.queries.NewsgroupQueries;
import org.random_access.newsreader.queries.ServerQueries;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

/**
 * <b>Project:</b> Newsreader for Android <br>
 * <b>Date:</b> 25.07.2015 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class NNTPSyncAdapter extends AbstractThreadedSyncAdapter {

    // Global variables
    // Define a variable to contain a content resolver instance
    private static final String TAG = NNTPSyncAdapter.class.getSimpleName();

    public static final String SYNC_REQUEST_TAG = "Sync-Request";
    public static final String SYNC_REQUEST_ORIGIN = "Sync-Origin";

    private static boolean isSyncStopped= false;
    private static int syncNumber = 0;

    private final ContentResolver mContentResolver;
    private final Context context;

    private static final String DATABASE_DATE_PATTERN = "yyyyMMddhhmmss Z";
    private static final String NNTPHEADER_DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";

    private long currentNewsgroupId = -1;
    private long currentMessageDate = -1;

    /**
     * Set up the sync adapter
     */
    public NNTPSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        this.context = context;
        mContentResolver = context.getContentResolver();
    }
    /**
     * Set up the sync adapter. This form of the
     * constructor malongains compatibility with Android 3.0
     * and later platform versions
     */
    public NNTPSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        this.context = context;
        mContentResolver = context.getContentResolver();
    }

    /*
    * Specify the code you want to run in the sync adapter. The entire
    * sync adapter runs in a background thread, so you don't have to set
    * up your own background processing.
    */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

     // TODO Put the data transfer code here.
        if (extras.getBoolean(SYNC_REQUEST_TAG) && NetworkStateHelper.isOnline(context)) {
            Log.d(TAG, "*************** SYNCING: " + ++syncNumber + " from " + extras.getString(SYNC_REQUEST_ORIGIN) + " *****************");
            ServerQueries serverQueries = new ServerQueries(context);
            Cursor c = serverQueries.getAllServers();
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    try {
                        getNewNewsForServer(c.getLong(ServerQueries.COL_ID), c.getString(ServerQueries.COL_NAME), c.getInt(ServerQueries.COL_PORT),
                                c.getInt(ServerQueries.COL_AUTH) == 1, c.getString(ServerQueries.COL_USER),
                                c.getString(ServerQueries.COL_PASSWORD));
                    } catch (IOException | LoginException e) {
                        e.printStackTrace();
                        if (currentNewsgroupId != -1 && currentMessageDate != -1) {
                            Log.d(TAG, "Sync date in group " + currentNewsgroupId + " is " + currentMessageDate);
                            NewsgroupQueries newsgroupQueries = new NewsgroupQueries(context);
                            newsgroupQueries.setLastSyncDate(currentNewsgroupId, currentMessageDate);
                        }
                    }
                    c.moveToNext();
                }
                c.close();
            }
        }
    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
        Log.d(TAG, "-----> Sync cancelled!");
        if (currentNewsgroupId != -1 && currentMessageDate != -1) {
            Log.d(TAG, "Sync date in group " + currentNewsgroupId + " is " + currentMessageDate);
            NewsgroupQueries newsgroupQueries = new NewsgroupQueries(context);
            newsgroupQueries.setLastSyncDate(currentNewsgroupId, currentMessageDate);
        }
    }

    private void getNewNewsForServer(long serverId, String server, int port, boolean auth, String user, String password) throws IOException, LoginException {
        NewsgroupQueries newsgroupQueries = new NewsgroupQueries(context);

        NNTPConnector nntpConnector = new NNTPConnector(context);
        NNTPClient client =  nntpConnector.connectToNewsServer(context, server, port, auth, user, password);
        Cursor c = newsgroupQueries.getNewsgroupsOfServer(serverId);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                getNewNewsForNewsgroup(serverId, client, c.getLong(NewsgroupQueries.COL_ID), c.getString(NewsgroupQueries.COL_NAME));
                // TODO cleanup old news -> use number of messages to keep / number of days to keep messages
                Log.d(TAG, "Finished sync for Newsgroup " + c.getString(NewsgroupQueries.COL_NAME));
                c.moveToNext();
            }
        }
        c.close();
        Log.d(TAG, "************ FINISHED SYNC: " + syncNumber + "*********************");


    }


    private void  getNewNewsForNewsgroup(long serverId, NNTPClient client, long groupId, String groupName) throws IOException, LoginException{
        currentNewsgroupId = groupId;

        // Create a GregorianCalendar instance with date of last sync
        NewsgroupQueries newsgroupQueries = new NewsgroupQueries(context);
        long lastSyncDate = newsgroupQueries.getLastSyncDate(groupId);
        GregorianCalendar calendar = new GregorianCalendar();
        if (lastSyncDate != -1) {
            calendar.setTimeInMillis(lastSyncDate);
            Log.d(TAG, "Last synced: " + lastSyncDate);
        } else {
            calendar.setTimeInMillis(System.currentTimeMillis() -  TimeUnit.MILLISECONDS.convert(30L, TimeUnit.DAYS));
            Log.d(TAG, "Time in millis: " + calendar.getTimeInMillis());
        }

        // get news list from server
        NewGroupsOrNewsQuery query = new NewGroupsOrNewsQuery(calendar, true);
        query.addNewsgroup(groupName);
        String[] messages = client.listNewNews(query);
        long currentSyncDate = System.currentTimeMillis();
        if (messages == null) {
            messages = applyNextCommand(client, groupName); // workaround for servers not listing news
        }

        // get messages and add them to database
        for (String s : messages) {
            fetchMessage(serverId, groupId, s);
        }
        newsgroupQueries.setLastSyncDate(groupId,currentSyncDate);
        currentNewsgroupId = -1;
        currentMessageDate = -1;
    }


    private String[] applyNextCommand (NNTPClient client, String group) throws  IOException{
        ArrayList<String> articleList = new ArrayList<>();
        client.selectNewsgroup(group);
        ArticleInfo pointer = new ArticleInfo();
        int i = 0;
        while (client.selectNextArticle(pointer) && i < 100){
            // client.selectArticle(pointer.articleNumber, pointer);
            Log.d(TAG, "pointer.articleNumber = " + pointer.articleNumber + ", pointer.articleId = " + pointer.articleId);
            articleList.add(pointer.articleId);
            i++;
        }
        String[] articleArray = new String[articleList.size()];
        return articleList.toArray(articleArray);
    }

    private void fetchMessage(long serverId, long groupId, String articleId) throws IOException, LoginException{
        boolean auth = new ServerQueries(context).hasServerAuth(serverId);

        // fetch header
        CustomNNTPClient client = new NNTPConnector(context).connectToNewsServer(serverId, null, auth);
        BufferedReader reader = new BufferedReader(client.retrieveArticleHeader(articleId));
        NNTPMessageHeader headerData = new NNTPMessageHeader();
        boolean decodingOk = headerData.parseHeaderData(reader, articleId, context);
        String charset = headerData.getCharset();
        long msgDate = new NNTPDateFormatter().getDateInMillis(headerData.getDate());
        client.disconnect();

        // fetch body
        client = new NNTPConnector(context).connectToNewsServer(serverId, charset, auth);
        String line;

        StringBuilder sbMessageBody = new StringBuilder();
        reader = new BufferedReader(client.retrieveArticleBody(articleId));
        while((line=reader.readLine()) != null) {
            sbMessageBody.append(line).append("\n");
        }
        reader.close();
        client.disconnect();

        // save message to database
        MessageQueries messageQueries = new MessageQueries(context);
        messageQueries.addMessage(articleId, headerData.getEmail(), headerData.getFullName(), headerData.getSubject(), headerData.getCharset(),
                msgDate, 1, 0, groupId, headerData.getHeaderSource(), sbMessageBody.toString());
        currentMessageDate = msgDate;
        Log.d(TAG, "Added message " +  articleId);
    }
}
