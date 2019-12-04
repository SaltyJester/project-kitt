package com.project.kitt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;


import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

//for notifications: https://stackoverflow.com/questions/22928684/android-how-to-set-alarm-x-days-before-specific-date
//for getting preferences: https://stackoverflow.com/questions/19799874/get-all-selected-entries-from-multiselectlistpreferencesharedpreferences
//for alarm manager: https://stackoverflow.com/questions/34494910/setting-notification-alarm-in-an-android-application

public class AddInfo extends AppCompatActivity {

    int day = 0;
    int month = 0;
    int year = 0;

    long dbIndex;
    int alarmID;
    int i=0;
    int dbIndexInt;
    String foodName;

    boolean addNotification = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences notifSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notifChecked = notifSharedPref.getBoolean("Enable expiry notifications", false);
        if (notifChecked){
            addNotification = true;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
    }

    public void launchDatePicker(View v){
        OnSelectDateListener listener = new OnSelectDateListener() {
            @Override
            public void onSelect(List<Calendar> calendars) {
                Calendar expiration = calendars.get(0);
                day = expiration.get(Calendar.DAY_OF_MONTH);
                month = expiration.get(Calendar.MONTH) + 1;
                year = expiration.get(Calendar.YEAR);
                TextInputEditText til = findViewById(R.id.itemDateEditText);
                til.setText((month)+"/"+(day)+"/"+(year));
            }
        };
        Calendar min = Calendar.getInstance();
        if(day != 0){
            min.set(year, month-1, day);
        }
        DatePickerBuilder builder = new DatePickerBuilder(this, listener)
                .pickerType(CalendarView.ONE_DAY_PICKER)
                .setMinimumDate(Calendar.getInstance())
                .setDate(min);

        DatePicker datePicker = builder.build();
        datePicker.show();
    }

    public void addItemToDB(View v){
        TextInputEditText itemName;
        itemName = findViewById(R.id.itemName);
        boolean error = false;

        if(itemName.getText().toString().matches("") || day == 0 || month == 0 || year == 0 ){
            error = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please make sure all fields have valid entries")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if (!error) {
            foodName = itemName.getText().toString();

            FoodDetail foodItem = new FoodDetail();
            foodItem.setFoodName(foodName);
            foodItem.setFoodDay(day);
            foodItem.setFoodMon(month);
            foodItem.setFoodYr(year);

            SQLiteDBHelper db = new SQLiteDBHelper(this);
            dbIndex = db.addFood(foodItem);
            dbIndexInt = (int) dbIndex;

            Intent myIntent = new Intent(AddInfo.this, MainActivity.class);
            startActivity(myIntent);
        }
        if (addNotification){
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            Set<String> selections = sharedPrefs.getStringSet("notification frequency", null);
            String[] selected = selections.toArray(new String[] {});
            if (selected.length != 0){
                for (String s : selected) {
                    setAlarm(s);
                }
            }
        }

    }
    public void setAlarm(String s) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        //1 for pm, 0 for am
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        long inputtedDate = calendar.getTimeInMillis();
        long dayToMilli = AlarmManager.INTERVAL_DAY; //converts 24 hours to 1 day
        int amtDays;
        long reminderTime = 0L;
        switch (s) {
            case "0":
                reminderTime = inputtedDate;
                break;
            case "1":
                amtDays = 1;
                reminderTime = inputtedDate - (amtDays * dayToMilli);
                break;
            case "2":
                amtDays = 3;
                reminderTime = inputtedDate - (amtDays * dayToMilli);
                break;
            case "3":
                amtDays = 7;
                reminderTime = inputtedDate - (amtDays * dayToMilli);
                break;
        }


        String id = Integer.toString(dbIndexInt) + Integer.toString(i++);
        //alarmid is for example 40, 41, 42, 43 for the fifth item in db
        alarmID = Integer.parseInt(id);
        System.out.println("adding alarm for: "  + alarmID);
        System.out.println(foodName + "is the food name");
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, myReceiver.class);
        intent.putExtra("foodName", foodName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmID, intent, 0);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
    }
}
