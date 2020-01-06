package com.paytm.digital.education.database.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoachingCourseImportantDate implements Serializable {
    private static final long serialVersionUID = 9109216030569802117L;
    private String key;
    private String value;
}
