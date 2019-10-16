package com.paytm.digital.education.ingestion.model;

import com.paytm.digital.education.exception.InvalidRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IngestionFormEntity {

    STREAM_FORM("stream"),
    MERCHANT_STREAM("merchant_stream"),
    EXAM_STREAM_MAPPING("exam_stream_mapping");

    private String value;

    public static IngestionFormEntity fromString(final String value) {
        for (final IngestionFormEntity formEntity : IngestionFormEntity.values()) {
            if (formEntity.getValue().equalsIgnoreCase(value)) {
                return formEntity;
            }
        }
        throw new InvalidRequestException(String.format(
                "Unknown IngestionFormEntity requested: %s, valid options are: %s",
                value, IngestionFormEntity.values()));
    }
}

