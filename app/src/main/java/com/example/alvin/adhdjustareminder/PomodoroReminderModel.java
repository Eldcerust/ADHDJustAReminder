package com.example.alvin.adhdjustareminder;

import java.util.Calendar;
import java.util.UUID;

public class PomodoroReminderModel extends ReminderModel{
    protected int WorkTime,ShortBreakTime,LongBreakTime,LongBreakSessionTimes;

    public PomodoroReminderModel(String title, Calendar dateTime, UUID randomUUID, int workTime, int shortBreakTime, int longBreakTime, int longBreakSessionTime){
        super(title,dateTime,randomUUID);
        this.WorkTime=workTime;
        this.ShortBreakTime=shortBreakTime;
        this.LongBreakTime=longBreakTime;
        this.LongBreakSessionTimes=longBreakSessionTime;
    }

    public void setSession(int workTime, int shortBreakTime, int longBreakTime, int longBreakSessionTime){
        this.WorkTime=workTime;
        this.ShortBreakTime=shortBreakTime;
        this.LongBreakTime=longBreakTime;
        this.LongBreakSessionTimes=longBreakSessionTime;
    }
}
