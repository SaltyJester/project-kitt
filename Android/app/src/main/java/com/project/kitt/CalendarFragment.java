
package com.project.kitt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalendarFragment extends Fragment{
    FoodDetail[] foodArray;
    int arrayLength;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        List<EventDay> events = new ArrayList<>();

        SQLiteDBHelper db = new SQLiteDBHelper(getContext());
        foodArray = db.getAllFood();
        int i = 0;
        arrayLength = foodArray.length;


        while (i < arrayLength){
            Calendar calendar = Calendar.getInstance();
            String dateStr;
            String dateDay;
            if (foodArray[i].getFoodDay() < 10){
                dateDay = Integer.toString(foodArray[i].getFoodDay());
                dateDay = "0" + dateDay;
            }
            else{
                dateDay = Integer.toString(foodArray[i].getFoodDay());
            }
            String dateMonth;
            if (foodArray[i].getFoodMon() < 10){
                dateMonth = Integer.toString(foodArray[i].getFoodMon());
                dateMonth = "0" + dateMonth;
            }
            else{
                dateMonth = Integer.toString(foodArray[i].getFoodMon());
            }
            String dateYear =  Integer.toString(foodArray[i].getFoodYr());
            dateStr = dateYear + dateMonth + dateDay;

            Date date1 = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                date1 = sdf.parse(dateStr);

            } catch (ParseException e) {
                e.printStackTrace();
            }


            Date todayDate = Calendar.getInstance().getTime();
            String today = sdf.format(todayDate);
            Date date2 = Calendar.getInstance().getTime();
            try {
                date2 = sdf.parse(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }



            long diff = date1.getTime() - date2.getTime();
            long daysDiff = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
            int difference = (int)daysDiff;
            calendar.add(Calendar.DAY_OF_MONTH, difference);
            if (difference < 0){
                events.add(new EventDay(calendar,R.drawable.ic_alert));
            }
            else{
                events.add(new EventDay(calendar,R.drawable.ic_alert));
            }


            i = i +1;

        }


        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setEvents(events);


        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                Calendar clickedDayCalendar = eventDay.getCalendar();
                Date daySelected = clickedDayCalendar.getTime();
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
                String date2 = sdf2.format(daySelected);

                int j = 0;
                int listLen = 0;
                List<FoodDetail> selectedFood = new ArrayList<>();

                while (j < arrayLength) {
                    String dateStr2;
                    String dateDay2;
                    if (foodArray[j].getFoodDay() < 10){
                        dateDay2 = Integer.toString(foodArray[j].getFoodDay());
                        dateDay2 = "0" + dateDay2;
                    }
                    else{
                        dateDay2 = Integer.toString(foodArray[j].getFoodDay());
                    }
                    String dateMonth2;
                    if (foodArray[j].getFoodMon() < 10){
                        dateMonth2 = Integer.toString(foodArray[j].getFoodMon());
                        dateMonth2 = "0" + dateMonth2;
                    }
                    else{
                        dateMonth2 = Integer.toString(foodArray[j].getFoodMon());
                    }
                    System.out.println(foodArray[j].getFoodDay());
                    System.out.println(foodArray[j].getFoodMon());
                    String dateYear2 =  Integer.toString(foodArray[j].getFoodYr());
                    System.out.println(foodArray[j].getFoodYr());
                    System.out.println(date2);
                    dateStr2 = dateYear2 + dateMonth2 + dateDay2;
                    System.out.println(dateStr2);


                    if (dateStr2.equals(date2)) {
                        selectedFood.add(foodArray[j]);
                        listLen = listLen +1;
                    }


                    j = j + 1;
                }
                if (!selectedFood.isEmpty()) {
                    System.out.println("NOT EMPTY");
                    String message = "";
                    for (int i = 0; i<listLen; i++){
                        String str = selectedFood.get(i).getFoodName();
                        message = message + str + "\n";

                    }

                    Date date11 = Calendar.getInstance().getTime();
                    Date date22 = Calendar.getInstance().getTime();
                    String todaysDate = sdf2.format(date22);
                    try {
                        date11 = sdf2.parse(date2);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        date22 = sdf2.parse(todaysDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long diff = date11.getTime() - date22.getTime();
                    long daysDiff = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
                    int difference = (int)daysDiff;
                    if (difference < 0){
                        showAddPastItemDialog(getContext(),message);
                    }
                    else{
                        showAddItemDialog(getContext(),message);
                    }

                }
                else{
                    System.out.println("EMPTY");
                }

            }
        });


        return view;
    }

    private void showAddItemDialog(Context c, String message){

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Food expiring on this day:")
                .setMessage(message)
                .setNegativeButton("Okay", null)
                .create();
        dialog.show();
    }

    private void showAddPastItemDialog(Context c, String message){

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Food expired on this day:")
                .setMessage(message)
                .setNegativeButton("Okay", null)
                .create();
        dialog.show();
    }
}
