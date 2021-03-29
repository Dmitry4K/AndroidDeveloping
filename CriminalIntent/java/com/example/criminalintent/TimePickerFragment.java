package com.example.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {
    private TimePicker mTimePicker;
    private static final String ARG_TIME = "crime_time";
    public static final String EXTRA_TIME = "com.example.criminalintent.time";

    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        TimePickerFragment frag = new TimePickerFragment();
        frag.setArguments(args);
        return frag;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
       Date date = (Date)getArguments().getSerializable(ARG_TIME);

       View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

       mTimePicker = (TimePicker)v.findViewById(R.id.dialog_time_picker);
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(date);
       mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR));
       mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

       return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.time_picker_title)
               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       Calendar cal = Calendar.getInstance();
                       int hour = mTimePicker.getCurrentHour();
                       int minute = mTimePicker.getCurrentMinute();
                       cal.set(Calendar.HOUR_OF_DAY, hour);
                       cal.set(Calendar.MINUTE, minute);
                       sendResult(Activity.RESULT_OK, cal.getTime());
                   }
               })
               .create();
    }
    private void sendResult(int resultCode, Date date){
        if(getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
