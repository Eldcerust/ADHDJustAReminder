package com.example.alvin.adhdjustareminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class RecurringReminderModel extends ReminderModel{
    protected int recurringTimes;
    protected Boolean [] daysRepeatedArr=new Boolean[7];

    public RecurringReminderModel(String title, Calendar dateTime, UUID randomUUID, int recurringTime, Boolean[] daysRepeatedArray){
        super(title,dateTime,randomUUID);
        this.recurringTimes=recurringTime;
        daysRepeatedArr=daysRepeatedArray;
    }

    public void setRepeating(int numberOfRepeats){
        this.recurringTimes=numberOfRepeats;
    }

    public void setRepeatDays(Boolean[] days){
        daysRepeatedArr=days;
    }
}