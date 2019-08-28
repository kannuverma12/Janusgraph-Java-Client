package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.paytm.digital.education.explore.constants.ExploreConstants.STUDENT_TO_TEACHERS_IMAGE_URL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.TOTAL_TEACHERS_IMAGE_URL;

@Data
public class FacultyDetail {
    @JsonProperty("total_teachers")
    private Integer totalTeachers;

    @JsonProperty("total_teachers_image_url")
    private String totalTeachersImageUrl = TOTAL_TEACHERS_IMAGE_URL;

    @JsonProperty("student_to_teacher_ratio")
    private String studentToTeacherRatio;

    @JsonProperty("student_to_teacher_ratio_image_url")
    private String studentToTeacherRatioImageUrl = STUDENT_TO_TEACHERS_IMAGE_URL;
}
