package com.paytm.digital.education.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.constant.ElasticSearchConstants;
import com.paytm.digital.education.database.embedded.Currency;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.DurationType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document(indexName = ElasticSearchConstants.COURSE_INDEX,
        type = ElasticSearchConstants.EDUCATION_TYPE)
public class CoachingCourseSearch implements IESDocument {

    @Id
    private Long                              courseId;
    private Long                              coachingInstituteId;
    private String                            courseName;
    private String                            courseCover;
    private String                            coachingInstituteName;
    private String                            logo;
    private Integer                           globalPriority;
    private CourseType                        courseType;
    private Integer                           courseDurationDays;
    private Integer                           duration;
    private DurationType                      durationType;
    private Double                            originalPrice;
    private Double                            discountedPrice;
    private Currency                          currency;
    private CourseLevel                       level;
    private String                            eligibility;
    private Map<String, Map<String, Long>>    streams;
    private List<Long>                        streamIds;
    private List<String>                      streamNames;
    private Map<String, Map<String, Long>>    exams;
    private List<Long>                        examIds;
    private List<String>                      examNames;
    private Integer                           validity;
    private DurationType                      validityType;
    private String                            info;
    private String                            description;
    private String                            language;
    private String                            syllabus;
    private List<CoachingCourseImportantDate> importantDates;
    private Boolean                           isCertificateAvailable;
    private Boolean                           isDoubtSolvingForumAvailable;
    private Boolean                           isProgressAnalysisAvailable;
    private Boolean                           isRankAnalysisAvailable;
    private Integer                           testCount;
    private Integer                           testDuration;
    private Integer                           testQuestionCount;
    private Integer                           testPracticePaperCount;
    private Integer                           distanceLearningBooksCount;
    private Integer                           distanceLearningSolvedPaperCount;
    private Integer                           distanceLearningAssignmentCount;
    private Integer                           elearningLectureCount;
    private Integer                           elearningLectureDuration;
    private Integer                           elearningOnlineTestCount;
    private Integer                           elearningPracticePaperCount;
    private Integer                           classroomLectureCount;
    private Integer                           classroomLectureDuration;
    private Integer                           classroomTestCount;
    private String                            classroomTeacherStudentRatio;
    private String                            mongoId;
    private Boolean                           isEnabled;
    private List<Long>                        features;

    @JsonProperty("how_to_use_1")
    private String                            howToUse1;

    @JsonProperty("how_to_use_2")
    private String                            howToUse2;

    @JsonProperty("how_to_use_3")
    private String                            howToUse3;

    @JsonProperty("how_to_use_4")
    private String                            howToUse4;

    private String                            sgst;
    private String                            cgst;
    private String                            igst;
    private String                            tcs;
    private String                            merchantProductId;
    private Long                              paytmProductId;
    private Boolean                           isDynamic;

    @Override
    public String getId() {

        return courseId.toString();
    }

    @Override
    public String getMongoId() {
        return mongoId;
    }
}
