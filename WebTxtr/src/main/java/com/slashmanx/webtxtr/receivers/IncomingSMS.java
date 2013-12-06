package com.slashmanx.webtxtr.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

import com.slashmanx.webtxtr.R;
import com.slashmanx.webtxtr.ThreadListActivity;
import com.slashmanx.webtxtr.classes.SMS;
import com.slashmanx.webtxtr.helpers.SMSHelpers;

import java.util.ArrayList;

/**
 * Created by emartin on 18/11/2013.
 */
public class IncomingSMS extends BroadcastReceiver {
    public SMSHelpers helpers;
    @Override
    public void onReceive(Context context, Intent intent) {
        //---get the SMS message passed in---
        if(helpers == null)
               helpers = new SMSHelpers(null, context);
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String messageReceived = "";
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            SMS sms = new SMS();
            ArrayList<SMS> messages = new ArrayList<SMS>();
            for (int i=0; i<msgs.length; i++)
            {
                SmsMessage msg = SmsMessage.createFromPdu((byte[])pdus[i]);
                sms = new SMS();
                sms.setAddress(msg.getOriginatingAddress());
                sms.setRead(false);
                sms.setMsg(msg.getMessageBody());
                sms.setPerson((int) helpers.getPersonIDFromAddress(sms.getAddress()));
                sms.setThreadId(-1);
                messages.add(sms);
            }
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            Boolean showNotification = sharedPref.getBoolean("pref_key_show_notification", false);
            if(messages.size() > 0 && showNotification) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(helpers.getContactInfoFromID(messages.get(0).getPerson()).getName())
                                .setContentText(messages.get(0).getMsg());
// Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(context, ThreadListActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(ThreadListActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                mNotificationManager.notify(100, mBuilder.build());
            }

        }
    }
}
