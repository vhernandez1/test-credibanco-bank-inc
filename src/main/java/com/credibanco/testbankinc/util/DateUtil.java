package com.credibanco.testbankinc.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Date addYearsToDate(int yearsToAdd, Date currentDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, yearsToAdd);
        return calendar.getTime();
    }
}
