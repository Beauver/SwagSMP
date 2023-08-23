package com.beauver.swagsmp.util;

import java.util.Calendar;
import java.util.Date;

public class CalculateDates {

    public static Date calculateDate(String args) {
        long currentTime = System.currentTimeMillis();
        int durationValue = Integer.parseInt(args.replaceAll("[^0-9]", ""));
        char unit = args.charAt(args.length() - 1);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);

        switch (unit) {
            case 's':
                calendar.add(Calendar.SECOND, durationValue);
                break;
            case 'm':
                calendar.add(Calendar.MINUTE, durationValue);
                break;
            case 'h':
                calendar.add(Calendar.HOUR, durationValue);
                break;
            case 'd':
                calendar.add(Calendar.DAY_OF_MONTH, durationValue);
                break;
            case 'w':
                calendar.add(Calendar.WEEK_OF_YEAR, durationValue);
                break;
            case 'M':
                calendar.add(Calendar.MONTH, durationValue);
                break;
            case 'y':
                calendar.add(Calendar.YEAR, durationValue);
                break;
            default:
                return null; // Invalid unit
        }
        return calendar.getTime();
    }

    public static boolean isValidDuration(String duration) {
        // Regular expression to match valid duration format (e.g., 1d, 3M, etc.)
        String durationPattern = "\\d+[smhdwMy]";
        return duration.matches(durationPattern);
    }
}
