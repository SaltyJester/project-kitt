package com.project.kitt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddInfo extends AppCompatActivity {

    int day = 0;
    int month = 0;
    int year = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                EditText et = findViewById(R.id.clickDate);
                et.setHint((month)+"/"+(day)+"/"+(year));
            }
        };
        Calendar min = Calendar.getInstance();
        if(day != 0){
            min.set(year, month, day);
        }
        DatePickerBuilder builder = new DatePickerBuilder(this, listener)
                .pickerType(CalendarView.ONE_DAY_PICKER)
                .setMinimumDate(Calendar.getInstance())
                .setDate(min);

        DatePicker datePicker = builder.build();
        datePicker.show();
    }

    public void addItemToDB(View v){
        EditText itemDate;
        EditText itemName;
        itemName = findViewById(R.id.itemName);
        itemDate = findViewById(R.id.clickDate);
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
            String foodName = itemName.getText().toString();

            FoodDetail foodItem = new FoodDetail();
            foodItem.setFoodName(foodName);
            foodItem.setFoodDay(day);
            foodItem.setFoodMon(month);
            foodItem.setFoodYr(year);

            SQLiteDBHelper db = new SQLiteDBHelper(this);
            db.addFood(foodItem);

            Intent myIntent = new Intent(AddInfo.this, MainActivity.class);
            startActivity(myIntent);
        }
    }
}
