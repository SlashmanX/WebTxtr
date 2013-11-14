package com.slashmanx.webtxtr.classes;

public class SMS{
    private String id;
    private String address;
    private String msg;
    private boolean read; //"0" for have not read sms and "1" for have read sms
    private Long time;
    private String folderName;
    private int person;
    private int threadId;

    public String getId(){
        return this.id;
    }
    public String getAddress(){
        return this.address;
    }
    public String getMsg(){
        return this.msg;
    }
    public boolean isRead(){
        return this.read;
    }
    public Long getTime(){
        return this.time;
    }
    public String getFolderName(){
        return this.folderName;
    }
    public int getPerson() {
        return person;
    }
    public int getThreadId() {
        return threadId;
    }

    public void setId(String id){
        this.id = id;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }
    public void setRead(boolean read){
        this.read = read;
    }
    public void setTime(Long time){
        this.time = time;
    }
    public void setFolderName(String folderName){
        this.folderName = folderName;
    }
    public void setPerson(int person) {
        this.person = person;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }
}
