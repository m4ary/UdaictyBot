package com.mshlab.udacityBot.uitils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.mshlab.udacityBot.Consts.CLOSED_DATA_PATTERN;

public class Helper {

    public static String convertDateGMT(Date date) throws ParseException {
        DateFormat formatter = new SimpleDateFormat(CLOSED_DATA_PATTERN);
        Date userFormattedDate = formatter.parse(date.toString());
        DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy HH:mm z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String correctDate = dateFormat.format(userFormattedDate);
        return correctDate;
    }

}
