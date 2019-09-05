package com.paytm.digital.education.coaching.consumer.model.response.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.ExamImportantDate;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ExamData extends SearchBaseData {

    private Integer                 examId;
    private String                  urlDisplayKey;
    private String                  examShortName;
    private String                  officialName;
    private String                  applicationMonth;
    private String                  resultMonth;
    private String                  examMonth;
    private String                  resultStartDate;
    private String                  resultEndDate;
    private String                  applicationStartDate;
    private String                  applicationEndDate;
    private String                  examStartDate;
    private String                  examEndDate;
    private String                  logoUrl;
    private List<String>            dataAvailable;
    private List<ExamImportantDate> importantDates;

}
