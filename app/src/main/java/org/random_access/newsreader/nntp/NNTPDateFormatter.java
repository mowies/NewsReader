package org.random_access.newsreader.nntp;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * <b>Project:</b> Newsreader for Android <br>
 * <b>Date:</b> 25.07.2015 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class NNTPDateFormatter {

    private static final String TAG = NNTPDateFormatter.class.getSimpleName();

    public static final String DATE_PATTERN_DATABASE = "yyyyMMddhhmmss Z";
    public static final String DATE_PATTERN_MSG_HEADER = "EEE, dd MMM yyyy hh:mm:ss Z";
    public static final String[] DATE_PATTERNS_MSG_HEADER =  new String []{"EEE, dd MMM yyyy hh:mm:ss Z", "EEE, dd MMM yyyy hh:mm Z"};

    private long lastParsedDate;

    // pattern database: "yyyyMMddhhmmss Z"
    // ----> eg 20150502181729 +0200

    // pattern message header: "EEE, d MMM yyyy HH:mm:ss Z"
    // ----> eg Sat, 02 May 2015 18:17:29 +0200

    public long getDateInMillis(String dateString, String pattern) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.US);
        Date date = df.parse(dateString);
        return date.getTime();
    }

    public long getDateInMillis(String dateString) {
        for (String pattern : DATE_PATTERNS_MSG_HEADER) {
            try {
                DateFormat df = new SimpleDateFormat(pattern, Locale.US);
                Date date = df.parse(dateString);
                lastParsedDate = date.getTime();
                return lastParsedDate;
            } catch (ParseException e) {
                Log.e(TAG, "Date " + dateString + " not parsable with pattern " + pattern);
            }
        }
        return lastParsedDate+1;
        // cheat to return a more or less valid date, so the message is at least at the right position
        // if the date cannot be parsed at all
    }

    public String getPrettyDateString(String rawDate, Context context) {
        SimpleDateFormat rawFormat = new SimpleDateFormat(DATE_PATTERN_MSG_HEADER, Locale.US);
        Date date = null;
        try {
            date = rawFormat.parse(rawDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date from " + rawDate);
        }
        DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        if (date != null) {
            String formattedDate = dateFormat.format(date);
            String formattedTime = timeFormat.format(date);
            return formattedDate + " " + formattedTime;
        } else {
            return rawDate;
        }

    }

    public static String getRawDateString(long dateInMillis) {
        // TODO
        return null;
    }

    public static String getPrettyDateString(long dateInMillis, Context context) {
        /*DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        Date date = new Date(dateInMillis);
        String formattedDate = dateFormat.format(date);
        String formattedTime = timeFormat.format(date);
        return formattedDate + " " + formattedTime; */
        return getPrettyDateStringDate(dateInMillis, context) + " " + getPrettyDateStringTime(dateInMillis, context);
    }

    public static String getPrettyDateStringDate(long dateInMillis, Context context) {
        DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
        Date date = new Date(dateInMillis);
        return dateFormat.format(date);
    }

    public static String getPrettyDateStringTime(long dateInMillis, Context context) {
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        Date date = new Date(dateInMillis);
        return timeFormat.format(date);
    }

}
