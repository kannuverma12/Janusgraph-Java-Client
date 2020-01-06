package com.paytm.digital.education.coaching.ingestion.model;

import com.paytm.digital.education.exception.InvalidRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IngestionFormEntity {

    COACHING_BANNER_FORM("banner"),
    COACHING_CENTER_FORM("center"),
    COACHING_COURSE_FEATURE_FORM("course-feature"),
    COACHING_COURSE_FORM("course"),
    COACHING_EXAM_FORM("exam"),
    COACHING_INSTITUTE_FORM("institute"),
    COMPETITIVE_EXAM_FORM("competitive-exam"),
    STREAM_FORM("stream"),
    TOP_RANKER_FORM("top-ranker"),
    COACHING_COURSE_CTA_FORM("coaching-cta");

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

