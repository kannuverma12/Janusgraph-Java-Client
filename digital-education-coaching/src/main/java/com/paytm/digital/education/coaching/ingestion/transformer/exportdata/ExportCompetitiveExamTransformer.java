package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CompetitiveExamForm;
import com.paytm.digital.education.database.entity.Exam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;

public class ExportCompetitiveExamTransformer {

    public static List<CompetitiveExamForm> convert(final List<Exam> examList) {
        if (CollectionUtils.isEmpty(examList)) {
            return new ArrayList<>();
        }
        return examList.stream()
                .map(exam -> CompetitiveExamForm.builder()
                        .examId(exam.getExamId())
                        .domains(exam.getStreamIds() == null
                                ? EMPTY_STRING : StringUtils.join(exam.getStreamIds(), ","))
                        .globalPriority(exam.getPriority())
                        .build())
                .collect(Collectors.toList());
    }
}

