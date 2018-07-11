package com.example.alvin.adhdjustareminder;

import java.util.Calendar;
import java.util.UUID;

public class ReminderModel {
    protected String reminderTitle;
    protected Calendar reminderDateAndTime;
    protected int reminderCount;
    protected UUID reminderUUID;

    public ReminderModel(){
        this.reminderTitle="";
        this.reminderDateAndTime=Calendar.getInstance();
    }

    public ReminderModel(String title, Calendar time,UUID externalUUID){
        this.reminderTitle=title;
        this.reminderDateAndTime=time;
        this.reminderUUID=externalUUID;
    }

    public void updateTitle(String title){this.reminderTitle=title;}

    public void updateDateTime(Calendar dateTime){this.reminderDateAndTime=dateTime;}

    public void setCount(Integer reminderCountStuff){reminderCount=reminderCountStuff;}
}
