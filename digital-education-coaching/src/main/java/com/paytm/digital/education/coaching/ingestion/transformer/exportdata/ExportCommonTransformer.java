package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

public class ExportCommonTransformer {

    private static final String YES = "Yes";
    private static final String NO  = "No";

    static String convertBooleanToString(final Boolean input) {
        if (null == input) {
            return NO;
        }
        return input ? YES : NO;
    }
}
