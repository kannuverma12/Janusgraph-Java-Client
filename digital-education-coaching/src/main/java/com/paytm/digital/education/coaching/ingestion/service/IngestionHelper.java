package com.paytm.digital.education.coaching.ingestion.service;

import com.paytm.digital.education.coaching.ingestion.model.GoogleSheetColumnName;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@UtilityClass
public class IngestionHelper {

    public String convertNumberToA1Notation(int columnNumber, int padding) {
        return convertNumberToA1Notation(columnNumber + padding);
    }

    public String convertNumberToA1Notation(int columnNumber) {

        final StringBuilder columnName = new StringBuilder();

        while (columnNumber > 0) {
            int rem = columnNumber % 26;

            if (rem == 0) {
                columnName.append("Z");
                columnNumber = (columnNumber / 26) - 1;
            } else {
                columnName.append((char) ((rem - 1) + 'A'));
                columnNumber = columnNumber / 26;
            }
        }

        return (columnName.reverse().toString());
    }

    public List<Object> getHeaderKeysList(final Class clazz) {
        if (null == clazz) {
            log.error("Got null clazz");
            return new ArrayList<>();
        }

        final Field[] fields = clazz.getDeclaredFields();
        final List<Object> headersList = new ArrayList<>();

        for (final Field field : fields) {
            final String name = field.getAnnotation(GoogleSheetColumnName.class).value();
            headersList.add(name);
        }
        return headersList;
    }
}
