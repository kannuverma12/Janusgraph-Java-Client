package com.paytm.digital.education.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtil {

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
}
