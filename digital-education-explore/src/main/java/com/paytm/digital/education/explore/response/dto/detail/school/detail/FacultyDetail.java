package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.paytm.digital.education.explore.constants.SchoolConstants.STUDENT_TO_TEACHERS_IMAGE_URL;
import static com.paytm.digital.education.explore.constants.SchoolConstants.TOTAL_TEACHERS_IMAGE_URL;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class FacultyDetail {
    @JsonProperty("total_teachers")
    private Integer totalTeachers;

    @JsonProperty("total_teachers_image_url")
    private String totalTeachersImageUrl = TOTAL_TEACHERS_IMAGE_URL;

    @JsonProperty("student_to_teacher_ratio")
    private String studentToTeacherRatio;

    @JsonProperty("student_to_teacher_ratio_image_url")
    private String studentToTeacherRatioImageUrl = STUDENT_TO_TEACHERS_IMAGE_URL;

    public FacultyDetail(Integer totalTeachers, String studentToTeacherRatio) {
        this.totalTeachers = totalTeachers;
        this.studentToTeacherRatio = studentToTeacherRatio;
    }
}
