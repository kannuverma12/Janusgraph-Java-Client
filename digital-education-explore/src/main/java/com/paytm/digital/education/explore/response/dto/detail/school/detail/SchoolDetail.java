package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.SchoolFeeDetails;
import com.paytm.digital.education.explore.database.entity.SchoolGallery;
import com.paytm.digital.education.explore.database.entity.ShiftDetails;
import com.paytm.digital.education.explore.response.dto.detail.Attribute;
import com.paytm.digital.education.explore.response.dto.detail.ClassInfoLegend;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SchoolDetail {
    @JsonProperty("shifts")
    private List<ShiftDetails> shiftDetailsList;

    @JsonProperty("faculty")
    private FacultyDetail facultyDetail = new FacultyDetail();

    @JsonProperty("fees_data")
    private List<SchoolFeeDetails> feesDetails;

    @JsonProperty("facilities")
    private List<String> facilities;

    @JsonProperty("important_dates")
    private List<ImportantDate> importantDateSections;

    @JsonProperty("gallery")
    private SchoolGallery gallery;

    @JsonProperty("derived_attributes")
    private Map<String, List<Attribute>> derivedAttributes;

    @JsonProperty("class_info")
    private List<ClassInfoLegend> classInfoLegend = ClassInfoLegend.CLASS_INFO_STATIC_CONTENT_LIST;

    @JsonProperty("general_information")
    private GeneralInformation generalInformation;
}
