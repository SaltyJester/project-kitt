
package com.project.kitt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.style.StyleSpan;
import android.util.TypedValue;
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

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
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

        // loop to cycle through all foods in database and add icon events for them
        while (i < arrayLength){
            Calendar calendar = Calendar.getInstance();
            calendar.set(foodArray[i].getFoodYr(), foodArray[i].getFoodMon() -1, foodArray[i].getFoodDay());
            // month -1 because calendar month is indexed starting at 0
            Calendar calendarToday = Calendar.getInstance();
            // if user expiration date is before today, set the icon color to be grey
            if (calendar.before(calendarToday)){
                events.add(new EventDay(calendar,R.drawable.ic_alertpast));
            }
            else{
                events.add(new EventDay(calendar,R.drawable.ic_alert));
            }

            i = i +1;
        }

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        // add the events to our calendar
        calendarView.setEvents(events);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                Calendar clickedDayCalendar = eventDay.getCalendar();
                Date daySelected = clickedDayCalendar.getTime();
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
                String date2 = sdf2.format(daySelected);
                // convert day clicked to string
                int j = 0;
                int listLen = 0;
                List<FoodDetail> selectedFood = new ArrayList<>();

                while (j < arrayLength) { // list of selected food aka food expiring on
                                        // selected day cant be longer than total food list
                    String dateStr2;
                    //check if day and month are less than 10 to append a 0, because the
                    // selected day is stored with two numbers, so 9 would be 09
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
                    String dateYear2 =  Integer.toString(foodArray[j].getFoodYr());
                    dateStr2 = dateYear2 + dateMonth2 + dateDay2; // the food in the database as string

                    if (dateStr2.equals(date2)) { // if a food in database has same expiration
                                                    // date as the day selected by user
                        selectedFood.add(foodArray[j]);
                        listLen = listLen +1;
                    }

                    j = j + 1;
                }

                if (!selectedFood.isEmpty()) { // if there is food expiring on day user selected

                    String message = "";
                    for (int i = 0; i<listLen; i++){ // add foods in selected food to a string
                        String str = selectedFood.get(i).getFoodName();
                        message = message + str + "\n";
                    }
                    //show a different message if the food has already expired
                    if (clickedDayCalendar.before(Calendar.getInstance()))  {
                        showAddPastItemDialog(getContext(),message);
                    }
                    else{
                        showAddItemDialog(getContext(),message);
                    }
                }
            }
        });


        return view;
    }

    /**
     *
     * @param c
     * @param message
     *
     * inputs are the context and the message of foods expiring on the day selected
     * the following methods show a dialog pop up of the foods expiring
     */
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
