package com.slashmanx.webtxtr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ListView;

import com.slashmanx.webtxtr.classes.SMS;
import com.slashmanx.webtxtr.classes.SMSThread;
import com.slashmanx.webtxtr.custom.ThreadListAdapter;
import com.slashmanx.webtxtr.helpers.SMSHelpers;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * An activity representing a list of Threads. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ThreadDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ThreadListFragment} and the item details
 * (if present) is a {@link ThreadDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ThreadListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ThreadListActivity extends FragmentActivity
        implements ThreadListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static final int TYPE_ALL_MESSAGE = 0;
    private static final int TYPE_INCOMING_MESSAGE = 1;
    private static final int TYPE_OUTGOING_MESSAGE = 2;
    private ListView messageList;
    private ThreadListAdapter messageListAdapter;
    private ArrayList<SMSThread> recordsStored;
    private ArrayList<SMSThread> listInboxMessages;
    private ProgressDialog progressDialogInbox;
    private CustomHandler customHandler;
    private SMSHelpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_list);
        initViews();
        helpers = new SMSHelpers(ThreadListActivity.this, getApplicationContext());

        if (findViewById(R.id.thread_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ThreadListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.thread_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link ThreadListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ThreadDetailFragment.ARG_ITEM_ID, id);
            ThreadDetailFragment fragment = new ThreadDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.thread_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ThreadDetailActivity.class);
            detailIntent.putExtra(ThreadDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //populateMessageList();
    }
    private void initViews() {
        customHandler = new CustomHandler(this);
        progressDialogInbox = new ProgressDialog(this);
        recordsStored = new ArrayList<SMSThread>();
        messageList = (ListView) findViewById(R.id.messageList);
        populateMessageList();
    }
    public void populateMessageList() {
        fetchInboxMessages();
        messageListAdapter = new ThreadListAdapter(this,
                R.layout.message_list_item, recordsStored);
        messageList.setAdapter(messageListAdapter);
    }
    private void showProgressDialog(String message) {
        progressDialogInbox.setMessage(message);
        progressDialogInbox.setIndeterminate(true);
        progressDialogInbox.setCancelable(true);
        progressDialogInbox.show();
    }
    private void fetchInboxMessages() {
        if (listInboxMessages == null) {
            showProgressDialog("Fetching Inbox Messages...");
            startThread();
        } else {
            // messageType = TYPE_INCOMING_MESSAGE;
            recordsStored = listInboxMessages;
            messageListAdapter.setArrayList(recordsStored);
        }
    }
    public class FetchMessageThread extends Thread {
        public int tag = -1;
        public FetchMessageThread(int tag) {
            this.tag = tag;
        }
        @Override
        public void run() {
            recordsStored = fetchInboxSms(TYPE_ALL_MESSAGE);
            listInboxMessages = recordsStored;
            customHandler.sendEmptyMessage(0);
        }
    }
    public ArrayList<SMSThread> fetchInboxSms(int type) {
        ArrayList<SMSThread> smsInbox = new ArrayList<SMSThread>();
        Uri uriSms = Uri.parse("content://sms");
        Cursor cursor = this.getContentResolver()
                .query(uriSms,
                        new String[] { "_id", "person", "thread_id", "address", "date", "body",
                                "type", "read" }, null, null,
                        "date" + " COLLATE LOCALIZED DESC");
        if (cursor != null) {
            cursor.moveToLast();
            if (cursor.getCount() > 0) {
                do {
                    SMS message = new SMS();
                    message.setAddress(cursor.getString(cursor
                            .getColumnIndex("address")));
                    message.setMsg(cursor.getString(cursor
                            .getColumnIndex("body")));
                    message.setRead(cursor.getInt(cursor
                            .getColumnIndex("read")) == 1);
                    message.setTime(cursor.getLong(cursor
                            .getColumnIndex("date")));
                    message.setId(cursor.getString(cursor
                            .getColumnIndex("_id")));
                    message.setFolderName(cursor.getString(cursor
                            .getColumnIndex("type")));
                    message.setPerson(cursor.getInt(cursor
                            .getColumnIndex("person")));
                    message.setThreadId(cursor.getInt(cursor
                            .getColumnIndex("thread_id")));
                    int index = helpers.getThreadIndexByThreadId(smsInbox, message.getThreadId());
                    SMSThread t;
                    if(index == -1) {
                        t = new SMSThread();
                        t.setId(message.getThreadId());
                        t.setAddress(message.getAddress());
                        long contactId = helpers.getPersonIDFromAddress(message.getAddress());
                        t.setPerson(helpers.getContactInfoFromID(contactId));
                    }
                    else t = smsInbox.get(index);
                    if(!message.isRead()) t.setRead(false);
                    t.addSMS(message);
                    if(index == -1) smsInbox.add(t);
                    else smsInbox.set(index, t);
                } while (cursor.moveToPrevious());
            }
        }
        cursor.close();
        return smsInbox;
    }
    private FetchMessageThread fetchMessageThread;
    private int currentCount = 0;
    public synchronized void startThread() {
        if (fetchMessageThread == null) {
            fetchMessageThread = new FetchMessageThread(currentCount);
            fetchMessageThread.start();
        }
    }
    public synchronized void stopThread() {
        if (fetchMessageThread != null) {
            Log.i("Cancel thread", "stop thread");
            FetchMessageThread moribund = fetchMessageThread;
            currentCount = fetchMessageThread.tag == 0 ? 1 : 0;
            fetchMessageThread = null;
            moribund.interrupt();
        }
    }

    static class CustomHandler extends Handler {
        private final WeakReference<ThreadListActivity> activityHolder;
        CustomHandler(ThreadListActivity inboxListActivity) {
            activityHolder = new WeakReference<ThreadListActivity>(inboxListActivity);
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            ThreadListActivity inboxListActivity = activityHolder.get();
            if (inboxListActivity.fetchMessageThread != null
                    && inboxListActivity.currentCount == inboxListActivity.fetchMessageThread.tag) {
                Log.i("received result", "received result");
                inboxListActivity.fetchMessageThread = null;
                inboxListActivity.messageListAdapter.setArrayList(inboxListActivity.recordsStored);
                inboxListActivity.progressDialogInbox.dismiss();
            }
        }
    }
    private DialogInterface.OnCancelListener dialogCancelListener = new DialogInterface.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            stopThread();
        }
    };
}
