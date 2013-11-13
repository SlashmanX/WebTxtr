package com.slashmanx.webtxtr.classes;

import java.util.ArrayList;

public class SMSThread {
    private int person;
    private ArrayList<SMS> messages;

    public SMSThread() {
        messages = new ArrayList<SMS>();
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public ArrayList<SMS> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<SMS> messages) {
        this.messages = messages;
    }

    public void addSMS(SMS message) {
        this.messages.add(message);
    }
}