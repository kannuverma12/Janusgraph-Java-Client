package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.ClassType;
import com.paytm.digital.education.enums.SchoolEducationLevelType;
import com.paytm.digital.education.enums.SchoolGender;
import com.paytm.digital.education.enums.SchoolOwnershipType;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static com.sun.org.apache.xml.internal.utils.LocaleUtility.EMPTY_STRING;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BoardData implements Serializable {
    private static final long serialVersionUID = -3699642149145396518L;

    @Field("affiliation_type")
    @JsonProperty("affiliation_type")
    private String affiliationType;

    @Field("affiliation_period")
    @JsonProperty("affiliation_period")
    private String affiliationPeriod;

    @Field("education_level")
    @JsonProperty("education_level")
    private SchoolEducationLevelType educationLevel;

    @Field("ownership")
    @JsonProperty("ownership")
    private SchoolOwnershipType ownership;

    @Field("contact_number_1")
    @JsonProperty("contact_number_1")
    private String contactNumberPrimary;

    @Field("contact_number_2")
    @JsonProperty("contact_number_2")
    private String contactNumberSecondary;

    @Field("email")
    @JsonProperty("email")
    private String email;

    @Field("email_2")
    @JsonProperty("email_2")
    private String emailTwo;

    @Field("no_of_teachers")
    @JsonProperty("no_of_teachers")
    private Integer noOfTeachers;

    @Field("no_of_trained_teachers")
    @JsonProperty("no_of_trained_teachers")
    private Integer noOfTrainedTeachers;

    @Field("no_of_untrained_teachers")
    @JsonProperty("no_of_untrained_teachers")
    private Integer noOfUntrainedTeachers;

    @Field("no_of_classrooms")
    @JsonProperty("no_of_classrooms")
    private Integer noOfClassrooms;

    @Field("disable_student_friendly")
    @JsonProperty("disable_student_friendly")
    private Integer disableStudentFriendly;

    @Field("school_brochure")
    @JsonProperty("school_brochure")
    private String schoolBrochure;

    @Field("school_brochure_link")
    @JsonProperty("school_brochure_link")
    private String schoolBrochureLink;

    @Field("gender")
    @JsonProperty("gender")
    private SchoolGender gender;

    @Field("affiliation_number")
    @JsonProperty("affiliation_number")
    private String affiliationNumber;

    @Field("parent_institution")
    @JsonProperty("parent_institution")
    private String parentInstitution;

    @Field("residential_status")
    @JsonProperty("residential_status")
    private List<String> residentialStatus;

    @Field("relevant_links")
    @JsonProperty("relevant_links")
    private List<RelevantLink> relevantLinks;

    @Field("school_facilities")
    @JsonProperty("school_facilities")
    private List<String> schoolFacilities;

    @Field("class_from")
    @JsonProperty("class_from")
    private ClassType classFrom;

    @Field("class_to")
    @JsonProperty("class_to")
    private ClassType classTo;

    @Field("fees_data")
    @JsonProperty("fees_data")
    private List<SchoolFeeDetails> feesDetails;

    @Field("school_admission")
    @JsonProperty("school_admission")
    private List<SchoolAdmission> schoolAdmissionList = emptyList();

    @Field("shifts")
    @JsonProperty("shifts")
    private List<ShiftDetails> shifts = emptyList();

    @Field("school_admission_tentative")
    @JsonProperty("tentative")
    private List<SchoolAdmission> schoolAdmissionTentativeList = emptyList();

    @Field("medium_of_instruction")
    @JsonProperty("medium_of_instruction")
    private List<String> mediumOfInstruction;

    @Field("streams")
    @JsonProperty("streams")
    private List<String> streams;

    @Field("enrollments")
    @JsonProperty("enrollments")
    private List<Enrollment> enrollments;

    @Field("school_area_name")
    @JsonProperty("school_area_name")
    private String schoolAreaName;

    @Field("program_type")
    @JsonProperty("program_type")
    private List<String> programType;

    public String getStudentRatio() {
        if (CollectionUtils.isEmpty(enrollments) || CommonUtils.isNullOrZero(noOfTeachers)) {
            return EMPTY_STRING;
        }
        return String.format("%d : 1", enrollments() / noOfTeachers);
    }

    public int enrollments() {
        if (CollectionUtils.isEmpty(enrollments)) {
            return 0;
        }

        return ofNullable(enrollments).orElse(emptyList())
                .stream()
                .map(x -> ofNullable(x.getEnrollment()).orElse(0))
                .mapToInt(Integer::intValue)
                .sum();
    }
}
