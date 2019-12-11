package com.project.kitt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static android.app.AlarmManager.*;

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
        //check to make sure the user wants to receive notifications
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
        Calendar today = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);

        TypedValue value = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.bgColor, value, true);

        DatePickerBuilder builder = new DatePickerBuilder(this, listener)
                .pickerType(CalendarView.ONE_DAY_PICKER)
                // .setMinimumDate(today)
                .setHeaderColor(R.color.colorPrimaryDark)
                .setPagesColor(R.color.colorPrimaryDark)
                .setTodayLabelColor(R.color.calendar_light)
                .setDaysLabelsColor(R.color.white)
                .setAbbreviationsLabelsColor(R.color.white)
                .setAbbreviationsBarColor(R.color.colorPrimaryDark)
                .setSelectionColor(R.color.calendar_light)
                .setDialogButtonsColor(R.color.white)
                .setDate(today);

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
            this.finish();
        }
        if (addNotification){
            //check the mulitselection preferences for how often the user wants notifications for their food
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            Set<String> selections = sharedPrefs.getStringSet("notification frequency", null);
            String[] selected = selections.toArray(new String[] {});
            //if the users made 1 or more selections for their notification preferences, make a call to set alarm function
            if (selected.length != 0){
                for (String s : selected) {
                    setAlarm(s);
                }
            }
        }

    }
    public void setAlarm(String s) {
        //set calendar instance with dates specified from date picker/user input
        //we want the user to recieve notifications at 11am for the days they choose
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long inputtedDate = calendar.getTimeInMillis();
        //number of days is based on the user preferences, multiple by milliseconds in a day
        //subtract inputted (expiration date) by the number of days before user chose in milliseconds
        // case 0: day of expiry
        // case 1: day before expiration date
        // case 2: 3 days before expiration date
        // case 3: 7 days before expiration date
        long dayToMilli = INTERVAL_DAY;
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

        //alarmid is for example 40, 41, 42, 43 for the fifth item in db
        String id = Integer.toString(dbIndexInt) + Integer.toString(i++);
        alarmID = Integer.parseInt(id);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, myReceiver.class);
        intent.putExtra("foodName", foodName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmID, intent, 0);
        am.setExactAndAllowWhileIdle(RTC_WAKEUP, reminderTime, pendingIntent);
    }
}
