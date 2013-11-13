package com.slashmanx.webtxtr.helpers;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.slashmanx.webtxtr.classes.SMSThread;

import java.util.ArrayList;

public class SMSHelpers {

    Activity mActivity;
    Context ctx;

    public SMSHelpers(Activity activity, Context ctx){
        this.mActivity = activity;
        this.ctx = ctx;
    }

    public int getThreadIndexByPersonId(ArrayList<SMSThread> threads, int person_id) {
        Log.d("Looking", "Person: "+ person_id);
        for(SMSThread t : threads) {
            if(t.getPerson() == person_id) return threads.indexOf(t);
        }

        return -1;
    }

    public Uri getPhotoForPerson(long person_id) {
        try {
            Cursor cur = this.ctx.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + person_id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, person_id);
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }
}