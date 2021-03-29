package com.example.criminalintent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

public class CustomDateFormat extends DateFormat {
    public static String getDayByNumber(int number) {
        String day;
        switch (number) {
            case 1: day = "Sunday"; break;
            case 2: day = "Monday"; break;
            case 3: day = "Tuesday"; break;
            case 4: day = "Wednesday"; break;
            case 5: day = "Thursday"; break;
            case 6: day = "Friday"; break;
            case 7: day = "Saturday"; break;
            default: day = "Invalid number of day!"; break;
        }
        return day;
    }
    public static String getMonthByNumber(int number){
        String monthString;
        switch (number) {
            case 0:  monthString = "January";       break;
            case 1:  monthString = "February";      break;
            case 2:  monthString = "March";         break;
            case 3:  monthString = "April";         break;
            case 4:  monthString = "May";           break;
            case 5:  monthString = "June";          break;
            case 6:  monthString = "July";          break;
            case 7:  monthString = "August";        break;
            case 8:  monthString = "September";     break;
            case 9: monthString = "October";       break;
            case 10: monthString = "November";      break;
            case 11: monthString = "December";      break;
            default: monthString = "Invalid month"; break;
        }
        return monthString;
    }
    public static StringBuffer dateToStringBuffer(Date date){
        StringBuffer resultString = new StringBuffer();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        resultString.append(getDayByNumber(cal.get(Calendar.DAY_OF_WEEK)) + ", " +
                getMonthByNumber(cal.get(Calendar.MONTH)) + ' '+
                cal.get(Calendar.DAY_OF_MONTH) + ", " + (cal.get(Calendar.YEAR))+", "
                +cal.get(Calendar.HOUR_OF_DAY)+':'+ minuteFormat(cal.get(Calendar.MINUTE)));
        return resultString;
    }
    public static String minuteFormat(int minute){
        String r = new String();
        if(minute < 10){
            r += '0';
        }
        r += (new Integer(minute)).toString();
        return r;
    }
    @NonNull
    @Override
    public StringBuffer format(@NonNull Date date, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition fieldPosition) {
        return dateToStringBuffer(date);
    }
    @Nullable
    @Override
    public Date parse(@NonNull String source, @NonNull ParsePosition pos) {
        return null;
    }

};


