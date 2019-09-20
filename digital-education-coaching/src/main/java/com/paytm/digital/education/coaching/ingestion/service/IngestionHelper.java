package com.paytm.digital.education.coaching.ingestion.service;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class IngestionHelper {

    public static String convertNumberToA1Notation(int columnNumber) {

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
}
