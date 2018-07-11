package com.example.alvin.adhdjustareminder;

import java.util.ArrayList;

public class ReminderArray extends ReminderModel{
    protected ArrayList<ReminderModel> listOfReminder=new ArrayList<ReminderModel>();

    public ReminderArray(ArrayList<ReminderModel> a){
        this.listOfReminder=a;
    }

    public void addReminderModel(ReminderModel reminderModel){
        listOfReminder.add(reminderModel);
    }

    public ReminderModel findReminderBasedOnInt(Integer reminderMdlCount){
        return(listOfReminder.get(reminderMdlCount));
    }

    public ReminderModel findReminderBasedOnName(String name){
        ReminderModel placeHolderModel=new ReminderModel();
        for(int a=0;a<this.listOfReminder.size();a++){
            if(this.listOfReminder.get(a).reminderTitle.equals(name)){
                placeHolderModel=listOfReminder.get(a);
            }
        }
        return placeHolderModel;
    }

    public void setCountForArray(){
        for(int a=0;a<this.listOfReminder.size();a++){
            this.listOfReminder.get(a).setCount(a);
        }
    }
}
