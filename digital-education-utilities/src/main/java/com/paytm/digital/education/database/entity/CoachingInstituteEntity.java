package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paytm.digital.education.database.embedded.Faq;
import com.paytm.digital.education.database.embedded.KeyHighlight;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@Document("coaching_institute")
public class CoachingInstituteEntity extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("institute_id")
    @Indexed(unique = true)
    private Long instituteId;

    @Field("brand_name")
    @Indexed(unique = true)
    private String brandName;

    @Field("about_institute")
    private String aboutInstitute;

    @Field("official_address")
    private OfficialAddress officialAddress;

    @Field("cover_image")
    private String coverImage;

    private String logo;

    private List<Long> streams;

    private List<Long> exams;

    @Field("course_types")
    private List<CourseType> courseTypes;

    @Field("establishment_year")
    private String establishmentYear;

    private String brochure;

    @Field("key_highlights")
    private List<KeyHighlight> keyHighlights;

    @Field("Faqs")
    private List<Faq> faqs;
}
