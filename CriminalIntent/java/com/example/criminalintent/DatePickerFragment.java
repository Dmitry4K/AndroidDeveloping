package com.example.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.nio.channels.AlreadyBoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {
    private Date mDate;
    private DatePicker mDatePicker;
    public static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.example.criminalintent.date";
    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment frag = new DatePickerFragment();
        frag.setArguments(args);
        return frag;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mDate = (Date)getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);

        mDatePicker = (DatePicker)v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date date = new GregorianCalendar(mDatePicker.getYear(),
                                mDatePicker.getMonth(),
                                mDatePicker.getDayOfMonth()).getTime();
                        sendResult(Activity.RESULT_OK, date);
                        TimePickerFragment timePicker = TimePickerFragment.newInstance(mDate);
                        timePicker.setTargetFragment(getTargetFragment(), CrimeFragment.REQUEST_TIME);
                        timePicker.show(getFragmentManager(), CrimeFragment.DIALOG_TIME);
                    }
                })
                .create();
    }
    private void sendResult(int resultCode, Date date){
        if(getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
