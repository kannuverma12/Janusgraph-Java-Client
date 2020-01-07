package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paytm.digital.education.database.embedded.Faq;
import com.paytm.digital.education.database.embedded.KeyHighlight;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    private static final long serialVersionUID = -6468670960040059657L;

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

    @Field("logo")
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

    @Field("faqs")
    private List<Faq> faqs;

    @Field("course_levels")
    private List<CourseLevel> courseLevels;

    @Field("paytm_merchant_id")
    private String paytmMerchantId;

    @Field("more_info1")
    private String moreInfo1;

    @Field("more_info2")
    private String moreInfo2;

    @Field("more_info3")
    private String moreInfo3;

    @Field("more_info4")
    private String moreInfo4;
}
