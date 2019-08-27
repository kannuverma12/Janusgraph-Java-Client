package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.embedded.Faq;
import com.paytm.digital.education.database.embedded.KeyHighlight;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@Document("coaching_institute")
public class CoachingInstitute extends Base {

    @Id
    @Field("_id")
    private ObjectId id;

    @Field("institute_id")
    @JsonProperty("institute_id")
    @Indexed(unique = true)
    private Long instituteId;

    @Field("brand_name")
    @JsonProperty("brand_name")
    private String brandName;

    @Field("about_institute")
    @JsonProperty("about_institute")
    private String aboutInstitute;

    @Field("official_address")
    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @Field("cover_image")
    @JsonProperty("cover_image")
    private String coverImage;

    private String logo;

    private List<Long> streams;

    private List<Long> exams;

    @Field("course_types")
    @JsonProperty("course_types")
    private List<CourseType> courseTypes;

    @Field("establishment_year")
    @JsonProperty("establishment_year")
    private String establishmentYear;

    private String brochure;

    @Field("key_highlights")
    @JsonProperty("key_highlights")
    private List<KeyHighlight> keyHighlights;

    @Field("Faqs")
    private List<Faq> faqs;
}
