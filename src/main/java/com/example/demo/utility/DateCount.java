package com.example.demo.utility;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateCount {
    public static int getDayDiffer(Timestamp startDate, Timestamp endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startDateTime = dateFormat.parse(dateFormat.format(startDate)).getTime();
        long endDateTime = dateFormat.parse(dateFormat.format(endDate)).getTime();
        return (int) ((endDateTime - startDateTime) / (1000 * 3600 * 24));
    }
}
