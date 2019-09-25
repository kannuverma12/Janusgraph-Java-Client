package com.paytm.digital.education.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;



public class DateUtil {

    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);


    public static Date stringToDate(String dateString, SimpleDateFormat dateFormatter) {
        if (StringUtils.isNotBlank(dateString)) {
            try {
                return dateFormatter.parse(dateString);
            } catch (ParseException ex) {
                log.error(
                        "ParseException caught while parsing the dateString : {}, formatter : {}, Exception : {}",
                         dateString, dateFormatter, ex);
            } catch (Exception ex) {
                log.error(
                        "Error caught while parsing the dateString : {}, formatter : {}, Exception : {}",
                        dateString, dateFormatter, ex);
            }
        }
        return null;
    }

    public static String dateToString(Date date, SimpleDateFormat dateFormatter) {
        if (date != null) {
            return dateFormatter.format(date);
        }
        return null;
    }

    public static String formatDateString(String dateString, SimpleDateFormat inFormatter,
            SimpleDateFormat outFormatter) {
        if (StringUtils.isNotBlank(dateString) && inFormatter != null && outFormatter != null) {
            try {
                return outFormatter.format(inFormatter.parse(dateString));
            } catch (ParseException ex) {
                log.error(
                        "ParseException caught while parsing the dateString : {}, inFormatter : {}, "
                                + "outFormatter: {}, Exception : {}",
                        dateString, inFormatter, outFormatter, ex);
            } catch (Exception ex) {
                log.error(
                        "Error caught while parsing the dateString : {}, inFormatter : {}, "
                                + "outFormatter: {}, Exception : {}",
                        dateString, inFormatter, outFormatter, ex);
            }
        }
        return null;
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static Date convertDateFormat(String currentPattern, String newPattern,
            String dateString)
            throws
            ParseException {
        DateFormat formatter = new SimpleDateFormat(currentPattern);
        Date date = formatter.parse(dateString);
        SimpleDateFormat newFormat = new SimpleDateFormat(newPattern);
        String finalString = newFormat.format(date);
        return newFormat.parse(finalString);
    }
}
