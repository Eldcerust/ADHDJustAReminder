package com.example.alvin.adhdjustareminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class AlarmModel {
    /*ArrayList<PendingIntent> alarmArray=new ArrayList<PendingIntent>();
    ArrayList<AlarmManager> alarmMgrArr=new ArrayList<AlarmManager>();
    int count=0;

    public AlarmModel()
    {}

    public void addAlarmModel(ReminderModel reminderModel,Context contextFromMain){
        alarmMgrArr.add(oneAlarmBoi);
        alarmArray.add(pendingIntent);
    }

    public void resetModel(){
        alarmArray=new ArrayList<PendingIntent>();
        alarmMgrArr=new ArrayList<AlarmManager>();
    }*/

    protected PendingIntent alarmIntent;
    protected AlarmManager alarmManager;
    protected UUID linkedUUID;

    public AlarmModel(PendingIntent receivedIntent, AlarmManager receivedAlarmManager, UUID randomUUID){
        this.alarmIntent=receivedIntent;
        this.alarmManager=receivedAlarmManager;
        this.linkedUUID=randomUUID;
    }
}