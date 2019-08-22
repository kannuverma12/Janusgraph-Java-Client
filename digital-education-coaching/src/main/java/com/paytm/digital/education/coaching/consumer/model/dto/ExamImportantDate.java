package com.paytm.digital.education.coaching.consumer.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ExamImportantDate {

    private int    id;
    private Date   date;
    private String about;
}
