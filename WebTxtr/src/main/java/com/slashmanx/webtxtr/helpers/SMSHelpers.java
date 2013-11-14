package com.slashmanx.webtxtr.helpers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.slashmanx.webtxtr.classes.Person;
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
        for(SMSThread t : threads) {
            if(t.getPerson() != null && t.getPerson().getId()  == person_id) return threads.indexOf(t);
        }

        return -1;
    }
    public long getPersonIDFromAddress(String address) {
        long personID = -1;
        // define the columns I want the query to return
        String[] projection = new String[] {
                ContactsContract.PhoneLookup._ID};

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));

        // query time
        Cursor cursor = ctx.getContentResolver().query(contactUri, projection, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                personID =  cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            }
        }

        cursor.close();
        return personID;
    }
    public Person getContactInfoFromID(long contact_id) {

        String phoneNumber = null;
        Person p = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = ctx.getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, _ID + " = ?", new String[] { String.valueOf(contact_id) }, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                p = new Person();
                p.setId(contact_id);
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                p.setName(name);
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                if (hasPhoneNumber > 0) {

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { String.valueOf(contact_id) }, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        p.setNumber(phoneNumber);
                    }

                    phoneCursor.close();
                }
            }

        }
        cursor.close();
        return p;
    }

    public int getThreadIndexByThreadId(ArrayList<SMSThread> threads, int thread_id) {
        for(SMSThread t : threads) {
            if(t.getId() == thread_id) return threads.indexOf(t);
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