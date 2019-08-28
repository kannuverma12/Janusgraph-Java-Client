package com.paytm.digital.education.database.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoachingProgramImportantDate {

    private String key;
    private String value;
    private int    priority;
}
