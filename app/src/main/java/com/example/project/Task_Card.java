package com.example.project;

// this class stores all the Details regaring the Task being added by the user
public class Task_Card {
    String name; // name of the task
    int id; // this stores the category of the Task
    long time; // the time for which that task has been done
    String sTime; // time in String/Time format
    long Ptime;
    boolean completed; // tracks the completed status of the task
    String uId; // this stores the UID of the Taks document in which it is stored in the Firestore
    String session;
    String date; // date of task creation

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public boolean isCompleted() {
        return completed;
    }
    public Task_Card(){}
    Task_Card(String name, int id, int time, String sTime) {
        this.name = name;
        this.id = id;
        this.time = time;
        this.sTime = sTime;
    }
   Task_Card(String name, int id, int time, String sTime, boolean completed, String uId){
        this.name=name;
        this.id=id;
        this.time=time;
        this.sTime=sTime;
        this.completed=completed;
        this.uId=uId;
    }
    Task_Card(String name, int id, int time, String sTime, boolean completed, String uId, String date){
        this.name=name;
        this.id=id;
        this.time=time;
        this.sTime=sTime;
        this.completed=completed;
        this.uId=uId;
        this.date=date;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    String sPTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public long getPtime() {
        return Ptime;
    }

    public void setPtime(long ptime) {
        Ptime = ptime;
    }

    public String getsPTime() {
        return sPTime;
    }

    public void setsPTime(String sPTime) {
        this.sPTime = sPTime;
    }

    Task_Card(String name, int id){
        this.name=name;
        this.id=id;
        time=0;
        sTime="00:00:00";
    }
}
