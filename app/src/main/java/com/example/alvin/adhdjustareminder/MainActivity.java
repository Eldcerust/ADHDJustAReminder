package com.example.alvin.adhdjustareminder;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{
    ArrayList<AlarmModel> alarmArray=new ArrayList<AlarmModel>();
    ReminderArray reminderArray; //this thing will stay uninitialized and load from a current database, unless set database was not found from loadAllFromSql
    boolean defaultPomodoro=true;
    private static final int NOTIFICATION_ID=102;
    private static final String TAG = "YourSmallerNotifications";
    ArrayList<Integer> defaultPomodoroValues=new ArrayList<Integer>();
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        NotificationManager notificationManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        super.onCreate(savedInstanceState);
        Intent thisIntent=new Intent(MainActivity.this,DrawerActivity.class);
        thisIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,thisIntent,0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,"default")
                .setSmallIcon(R.raw.aperture)
                .setContentTitle("Reminder Is Running.")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(TAG,NOTIFICATION_ID,mBuilder.build());

        if(defaultPomodoro){
            defaultPomodoroValues.set(0,25);
            defaultPomodoroValues.set(1,5);
            defaultPomodoroValues.set(2,15);
            defaultPomodoroValues.set(3,6);

            SQLiteDatabase defaultPomodoroValueDatabase=this.openOrCreateDatabase("defaultPomodoro",MODE_PRIVATE,null);
            defaultPomodoroValueDatabase.execSQL("CREATE TABLE IF NOT EXISTS defaultPomodoro(Session VARCHAR, Time INTEGER)");

            String sessionNames[]={
                    String.valueOf("WorkTime"),
                    String.valueOf("ShortBreakTime"),
                    String.valueOf("LongBreakTime"),
                    String.valueOf("SessionTimes")
            };

            for(int a=0;a<4;a++) {
                defaultPomodoroValueDatabase.execSQL("INSERT INTO defaultPomodoro(Session, Time) values ("+sessionNames[a]+"," + String.valueOf(defaultPomodoroValues.get(a)) + ")");
            }

            defaultPomodoro=false;

        } else {

            SQLiteDatabase defaultPomodoroValueDatabase=this.openOrCreateDatabase("defaultPomodoro",MODE_PRIVATE,null);
            Cursor defaultPomodoroCursor=defaultPomodoroValueDatabase.rawQuery("SELECT * FROM defaultPomodoro",null);

            int timeIndex=defaultPomodoroCursor.getColumnIndex("Time");

            defaultPomodoroCursor.moveToFirst();

            for(int i=0;i<4;i++){
                defaultPomodoroValues.set(i,Integer.valueOf(defaultPomodoroCursor.getString(timeIndex)));
                defaultPomodoroCursor.moveToNext();
            }

        }


        try{
            reminderArray=loadAllAlarmSql();
        } catch (ParseException e){
            e.printStackTrace();
        }

    }

    public ReminderArray loadAllAlarmSql() throws ParseException {
        ArrayList<ReminderModel> basicModel=loadBasicReminderSql();
        ArrayList<ReminderModel> pomodoroModel=loadPomodoroModel();
        ArrayList<ReminderModel> recurringModel=loadRecurringModel();

        basicModel.addAll(pomodoroModel);
        basicModel.addAll(recurringModel);

        basicModel=mergeSortReminders(basicModel);

        return(new ReminderArray(basicModel));
    }

    public ArrayList<ReminderModel> loadBasicReminderSql() throws ParseException{
        ArrayList<ReminderModel> basicReminderArray = new ArrayList<ReminderModel>();
        try {
            SQLiteDatabase basicReminderDatabase = this.openOrCreateDatabase("BasicReminders", MODE_PRIVATE, null);
            Cursor c = basicReminderDatabase.rawQuery("SELECT * FROM reminders ORDER BY name, calendar", null);

            int nameIndex = c.getColumnIndex("name");
            int calendarIndex = c.getColumnIndex("calendar");

            c.moveToFirst();

            while (c != null) {
                String nameModel = c.getString(nameIndex);
                String dateToParse = c.getString(calendarIndex);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                Calendar theCalendar = Calendar.getInstance();
                theCalendar.setTime(dateFormat.parse(dateToParse));

                UUID randomUUID=UUID.randomUUID();
                ReminderModel reminderModelToAdd = new ReminderModel(nameModel, theCalendar, randomUUID);
                createAlarmAndIntent(reminderModelToAdd,randomUUID);
                basicReminderArray.add(reminderModelToAdd);
                c.moveToNext();
            }

            return basicReminderArray;
        } catch(Exception e){
            return basicReminderArray;
        }
    }

    public ArrayList<ReminderModel> loadPomodoroModel() throws ParseException{
        ArrayList<ReminderModel> basicReminderArray = new ArrayList<ReminderModel>();
        try {
            SQLiteDatabase basicReminderDatabase = this.openOrCreateDatabase("PomodoroReminders", MODE_PRIVATE, null);
            Cursor c = basicReminderDatabase.rawQuery("SELECT * FROM reminders ORDER BY name, calendar", null);

            int nameIndex = c.getColumnIndex("name");
            int calendarIndex = c.getColumnIndex("calendar");

            c.moveToFirst();

            while (c != null) {
                String nameModel = c.getString(nameIndex);
                String dateToParse = c.getString(calendarIndex);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                Calendar theCalendar = Calendar.getInstance();
                theCalendar.setTime(dateFormat.parse(dateToParse));

                UUID randomUUID=UUID.randomUUID();
                ReminderModel reminderModelToAdd = new PomodoroReminderModel(nameModel, theCalendar,randomUUID,defaultPomodoroValues.get(0),defaultPomodoroValues.get(1),defaultPomodoroValues.get(2),defaultPomodoroValues.get(3));
                createAlarmAndIntent(reminderModelToAdd,randomUUID);
                basicReminderArray.add(reminderModelToAdd);
                c.moveToNext();
            }

            return basicReminderArray;
        } catch(Exception e){
            return basicReminderArray;
        }
    }

    public void cancelAlarm(AlarmModel alarmCreated){
        alarmCreated.alarmManager.cancel(alarmCreated.alarmIntent);
    }

    public ArrayList<ReminderModel> loadRecurringModel() throws ParseException{
        ArrayList<ReminderModel> basicReminderArray = new ArrayList<ReminderModel>();
        try {
            SQLiteDatabase basicReminderDatabase = this.openOrCreateDatabase("RecurringReminders", MODE_PRIVATE, null);
            Cursor c = basicReminderDatabase.rawQuery("SELECT * FROM reminders ORDER BY name, calendar", null);

            int nameIndex = c.getColumnIndex("name");
            int calendarIndex = c.getColumnIndex("calendar");
            int recurringTimes = c.getColumnIndex("recurrenceNumber");
            int daysRepeated = c.getColumnIndex("recurrenceDays");

            c.moveToFirst();

            while (c != null) {
                String nameModel = c.getString(nameIndex);
                String dateToParse = c.getString(calendarIndex);
                int recurringTimesValue = Integer.valueOf(c.getString(recurringTimes));
                String daysRepeatedValue = c.getString(daysRepeated);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                Calendar theCalendar = Calendar.getInstance();

                theCalendar.setTime(dateFormat.parse(dateToParse));

                ReminderModel reminderModelToAdd;

                UUID randomUUID=UUID.randomUUID();
                reminderModelToAdd = new RecurringReminderModel(nameModel,theCalendar,randomUUID,recurringTimesValue,setRepeatDaysFromString(daysRepeatedValue));
                createAlarmAndIntent(reminderModelToAdd,randomUUID);
                basicReminderArray.add(reminderModelToAdd);

                c.moveToNext();
            }

            return basicReminderArray;
        } catch(Exception e){
            return basicReminderArray;
        }
    }

    public void createAlarmAndIntent(ReminderModel reminderModel,UUID toBeLinked){
        Calendar calendar=reminderModel.reminderDateAndTime;
        Intent intent=new Intent(this,AlarmReceiver.class);
        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm");
        intent.putExtra("NameIs",reminderModel.reminderTitle+","+dateFormat.format(reminderModel.reminderDateAndTime.getTime()));

        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,count,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        count++;
        AlarmManager oneAlarmBoi=(AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        oneAlarmBoi.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        AlarmModel alarmCreated=new AlarmModel(pendingIntent,oneAlarmBoi,toBeLinked);
        alarmArray.add(alarmCreated);
    }

    public Boolean[] setRepeatDaysFromString(String daysRepeated){
        Boolean[] daysRepeatedArr=initializeDaysRepetition();

        while(daysRepeated.length()!=0){
            if(daysRepeated.contains("Sun")){
                daysRepeatedArr[0]=true;
                daysRepeated=daysRepeated.replace("Sun","");
            }

            if(daysRepeated.contains("Mon")){
                daysRepeatedArr[1]=true;
                daysRepeated=daysRepeated.replace("Mon","");
            }

            if(daysRepeated.contains("Tue")){
                daysRepeatedArr[2]=true;
                daysRepeated=daysRepeated.replace("Tue","");
            }

            if(daysRepeated.contains("Wed")){
                daysRepeatedArr[3]=true;
                daysRepeated=daysRepeated.replace("Wed","");
            }

            if(daysRepeated.contains("Thu")){
                daysRepeatedArr[4]=true;
                daysRepeated=daysRepeated.replace("Thu","");
            }

            if(daysRepeated.contains("Fri")){
                daysRepeatedArr[5]=true;
                daysRepeated=daysRepeated.replace("Fri","");
            }

            if(daysRepeated.contains("Sat")){
                daysRepeatedArr[6]=true;
                daysRepeated=daysRepeated.replace("Sat","");
            }
        }

        return daysRepeatedArr;
    }

    public Boolean[] initializeDaysRepetition(){
        Boolean[] daysRepeatedArr=new Boolean[7];
        for(int i=0;i<daysRepeatedArr.length;i++){
            daysRepeatedArr[i]=false;
        }

        return daysRepeatedArr;
    }

    public Boolean compareUUIDValues(ReminderModel reminderModel, AlarmModel almModel){
        boolean returnValue;
        if(reminderModel.reminderUUID.equals(almModel.linkedUUID)){
            returnValue=true;
        } else {
            returnValue=false;
        }
        return returnValue;
    }

    public ArrayList<ReminderModel> mergeSortReminders(ArrayList<ReminderModel> remindersToBeSorted){
        ArrayList<ReminderModel> left=new ArrayList<ReminderModel>();
        ArrayList<ReminderModel> right=new ArrayList<ReminderModel>();
        int center;

        if(remindersToBeSorted.size()==1){
            return remindersToBeSorted;
        } else {
            center=remindersToBeSorted.size()/2;
            for(int i=0;i<center;i++){
                left.add(remindersToBeSorted.get(i));
            }

            for(int i=center;i<remindersToBeSorted.size();i++){
                right.add(remindersToBeSorted.get(i));
            }

            left=mergeSortReminders(left);
            right=mergeSortReminders(right);

            merge(left,right,remindersToBeSorted);
    }
    return remindersToBeSorted;
    }

    private void merge(ArrayList<ReminderModel> left,ArrayList<ReminderModel> right,ArrayList<ReminderModel> whole){
        int leftIndex=0;
        int rightIndex=0;
        int wholeIndex=0;

        while(leftIndex<left.size() && rightIndex<right.size()){
            if(left.get(leftIndex).reminderDateAndTime.compareTo(right.get(rightIndex).reminderDateAndTime)<0){
                whole.set(wholeIndex,left.get(leftIndex));
                leftIndex++;
            } else {
                whole.set(wholeIndex,right.get(rightIndex));
                rightIndex++;
            }
            wholeIndex++;
        }

        ArrayList<ReminderModel> rest;
        int restIndex;
        if(leftIndex>=left.size()){
            rest=right;
            restIndex=rightIndex;
        } else {
            rest=left;
            restIndex=leftIndex;
        }

        for(int i=restIndex;i<rest.size();i++){
            whole.set(wholeIndex,rest.get(i));
            wholeIndex++;
        }
    }
}