package com.project.kitt;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar expCalendar = Calendar.getInstance();
            int expYear = expCalendar.get(Calendar.YEAR);
            int expMonth = expCalendar.get(Calendar.MONTH);
            int expDay = expCalendar.get(Calendar.DAY_OF_MONTH);
            //NEW INSTANCE OF DATEPICKERDIALOG & RETURN
            DatePickerDialog calendarView = new DatePickerDialog(getActivity(), this, expYear, expMonth, expDay);
            return calendarView;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            //set date TO DO
        }
    }