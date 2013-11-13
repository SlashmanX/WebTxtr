package com.slashmanx.webtxtr.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.slashmanx.webtxtr.R;
import com.slashmanx.webtxtr.classes.SMSThread;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ThreadListAdapter extends ArrayAdapter<SMSThread> {

    private Context ctx;
    public ArrayList<SMSThread> threadListArray;
    public ThreadListAdapter(Context context, int textViewResourceId, ArrayList<SMSThread> threadListArray) {
        super(context, textViewResourceId);
        this.threadListArray = threadListArray;
        this.ctx = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View convertView1 = convertView;
        if (convertView1 == null) {
            holder = new Holder();
            LayoutInflater vi = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView1 = vi.inflate(R.layout.message_list_item, null);
            holder.messageTo = (TextView) convertView1.findViewById(R.id.txt_msgTO);
            holder.messageCount = (TextView) convertView1.findViewById(R.id.txt_msgCount);
            holder.messageDate = (TextView) convertView1.findViewById(R.id.txt_msgDate);
            holder.messageContent = (TextView) convertView1.findViewById(R.id.txt_msgContent);
            holder.contactPerson = (QuickContactBadge) convertView1.findViewById(R.id.contact_person);
            convertView1.setTag(holder);
        } else {
            holder = (Holder) convertView1.getTag();
        }
        SMSThread sms_thread = getItem(position);

        if(sms_thread.getPerson() != null) {
            holder.messageTo.setText(sms_thread.getPerson().getName());
        }
        else {
            holder.messageTo.setText(sms_thread.getAddress());
        }

        holder.messageCount.setText(sms_thread.getMessages().size() + ""); 
        holder.messageDate.setText("12:10");

        holder.messageContent.setText(sms_thread.getMessages().get(sms_thread.getMessages().size() -1).getMsg());

        if(sms_thread.getPerson() != null) {
            Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(sms_thread.getPerson().getId()));
            InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(), contactUri);
            if(photo_stream == null) {
                holder.contactPerson.setImageResource(R.drawable.ic_contact_picture);
            }
            else {
                BufferedInputStream buf = new BufferedInputStream(photo_stream);

                Bitmap my_btmp = BitmapFactory.decodeStream(buf);
                holder.contactPerson.setImageBitmap(my_btmp);
                try {
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            holder.contactPerson.setImageResource(R.drawable.ic_contact_picture);
        }

        return convertView1;
    }
    @Override
    public int getCount() {
        return threadListArray.size();
    }
    @Override
    public SMSThread getItem(int position) {
        return threadListArray.get(position);
    }
    public void setArrayList(ArrayList<SMSThread> threadList) {
        this.threadListArray = threadList;
        notifyDataSetChanged();
    }
    private class Holder {
        public TextView messageTo, messageContent, messageCount, messageDate;
        public QuickContactBadge contactPerson;
    }
}