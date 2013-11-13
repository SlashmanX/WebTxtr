package com.slashmanx.webtxtr.classes;

import java.util.ArrayList;

public class SMSThread {
    private Person person;
    private int id;
    private ArrayList<SMS> messages;
    private String address;

    public SMSThread() {
        messages = new ArrayList<SMS>();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}