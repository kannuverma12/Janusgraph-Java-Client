package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.PaytmSourceDataEntity;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.enums.EntitySourceType;
import com.paytm.digital.education.serviceimpl.helper.EntitySourceMappingHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.ExploreConstants.COURSE_DATA_FIELD;
import static com.paytm.digital.education.constant.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.DOT_SEPERATOR;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_DATA_FIELD;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_DATA_FIELD;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.SchoolConstants.SCHOOL_DATA_FIELD;
import static com.paytm.digital.education.constant.SchoolConstants.SCHOOL_ID;
import static com.paytm.digital.education.enums.EducationEntity.COURSE;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;
import static com.paytm.digital.education.enums.EducationEntity.SCHOOL;

@AllArgsConstructor
@Repository
public class CommonEntityMongoDAO {

    private final CommonMongoRepository     commonMongoRepository;
    private final EntitySourceMappingHelper entitySourceMappingHelper;
    private final InstituteRepository       instituteRepository;

    public List<School> getSchoolsByIdsIn(List<Long> schoolIds,
            List<String> schoolProjectionFields) {
        Map<EntitySourceType, List<Long>> sourceAndSchoolIdsMap =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(SCHOOL, schoolIds);
        List<School> schools = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sourceAndSchoolIdsMap)) {
            List<Long> schoolIdsWithSourcePaytm = sourceAndSchoolIdsMap.get(EntitySourceType.PAYTM);
            List<Long> schoolIdsWithSourceMerchant =
                    sourceAndSchoolIdsMap.get(EntitySourceType.C360);
            if (!CollectionUtils.isEmpty(schoolIdsWithSourcePaytm)) {
                List<String> projectionFields =
                        Optional.ofNullable(schoolProjectionFields).orElse(new ArrayList<>())
                                .stream().map(f -> SCHOOL_DATA_FIELD + DOT_SEPERATOR + f).collect(
                                Collectors.toList());

                List<PaytmSourceDataEntity> paytmSourceDataEntities = commonMongoRepository
                        .getEntityFieldsFromPaytmSourceByValuesIn(INSTITUTE.name(),
                                schoolIdsWithSourcePaytm, PaytmSourceDataEntity.class,
                                projectionFields);
                schools.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getSchoolData).collect(
                                Collectors.toList()));
            }
            if (!CollectionUtils.isEmpty(schoolIdsWithSourceMerchant)) {
                schools.addAll(commonMongoRepository
                        .getEntityFieldsByValuesIn(SCHOOL_ID, schoolIds, School.class,
                                schoolProjectionFields));
            }
        }
        return schools;
    }

    public School getSchoolById(Long schoolId,
            List<String> schoolProjectionFields) {
        EntitySourceType sourceType =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(SCHOOL, schoolId);

        School school = null;
        if (EntitySourceType.PAYTM.equals(sourceType)) {
            List<String> projectionFields =
                    Optional.ofNullable(schoolProjectionFields).orElse(new ArrayList<>())
                            .stream().map(f -> SCHOOL_DATA_FIELD + DOT_SEPERATOR + f).collect(
                            Collectors.toList());
            PaytmSourceDataEntity paytmSourceDataEntity =
                    commonMongoRepository.getEntityFromPaytmSourceByFields(schoolId,
                            SCHOOL.name(), PaytmSourceDataEntity.class,
                            projectionFields);
            if (Objects.nonNull(paytmSourceDataEntity)) {
                school = paytmSourceDataEntity.getSchoolData();
            }
        } else {
            school = commonMongoRepository
                    .getEntityByFields(SCHOOL_ID, schoolId, School.class, schoolProjectionFields);
        }
        return school;
    }

    public List<Institute> getInstitutesByIdsIn(List<Long> instituteIds,
            List<String> fields) {
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(INSTITUTE, instituteIds);
        List<Institute> institutes = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(EntitySourceType.PAYTM);
            List<Long> idsWithSourceMerchant =
                    sourceAndEntityIdsMap.get(EntitySourceType.C360);
            if (!CollectionUtils.isEmpty(idsWithSourcePaytm)) {
                List<String> projectionFields =
                        Optional.ofNullable(fields).orElse(new ArrayList<>())
                                .stream().map(f -> INSTITUTE_DATA_FIELD + DOT_SEPERATOR + f)
                                .collect(
                                        Collectors.toList());

                List<PaytmSourceDataEntity> paytmSourceDataEntities = commonMongoRepository
                        .getEntityFieldsFromPaytmSourceByValuesIn(INSTITUTE.name(),
                                idsWithSourcePaytm, PaytmSourceDataEntity.class, projectionFields);
                institutes.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getInstituteData).collect(
                                Collectors.toList()));
            }
            if (!CollectionUtils.isEmpty(idsWithSourceMerchant)) {
                institutes.addAll(commonMongoRepository
                        .getEntityFieldsByValuesIn(INSTITUTE_ID, instituteIds, Institute.class,
                                fields));
            }
        }
        return institutes;
    }

    public List<Institute> getAllInstitutes(Map<String, Object> queryObject,
            List<String> instituteFields, String operator) {
        List<Long> instituteIds = (List<Long>) queryObject.get(INSTITUTE_ID);
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(INSTITUTE, instituteIds);
        List<Institute> institutes = new ArrayList<>();

        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(EntitySourceType.PAYTM);
            List<Long> idsWithSourceMerchant =
                    sourceAndEntityIdsMap.get(EntitySourceType.C360);
            if (!CollectionUtils.isEmpty(idsWithSourceMerchant)) {
                institutes.addAll(commonMongoRepository.findAll(queryObject, Institute.class,
                        instituteFields, operator));
            }

            if (!CollectionUtils.isEmpty(idsWithSourcePaytm)) {
                queryObject.put(INSTITUTE_ID, idsWithSourcePaytm);
                List<String> projectionFields =
                        Optional.ofNullable(instituteFields).orElse(new ArrayList<>())
                                .stream().map(f -> INSTITUTE_DATA_FIELD + DOT_SEPERATOR + f)
                                .collect(
                                        Collectors.toList());
                Map<String, Object> instituteQueryObject = queryObject.entrySet()
                        .stream()
                        .collect(Collectors.toMap(entry -> INSTITUTE_DATA_FIELD
                                + DOT_SEPERATOR + entry.getKey(), entry -> entry.getValue()));

                List<PaytmSourceDataEntity> paytmSourceDataEntities =
                        commonMongoRepository
                                .findAll(instituteQueryObject, PaytmSourceDataEntity.class,
                                        projectionFields, operator);
                institutes.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getInstituteData).collect(
                                Collectors.toList()));
            }

        }
        return institutes;
    }

    public Institute getInstituteByIdsIn(Long instituteId,
            List<String> projectionFields) {
        EntitySourceType sourceType =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(INSTITUTE, instituteId);

        Institute institute = null;
        if (EntitySourceType.PAYTM.equals(sourceType)) {
            List<String> fields =
                    Optional.ofNullable(projectionFields).orElse(new ArrayList<>())
                            .stream().map(f -> INSTITUTE_DATA_FIELD + DOT_SEPERATOR + f).collect(
                            Collectors.toList());
            PaytmSourceDataEntity paytmSourceDataEntity =
                    commonMongoRepository.getEntityFromPaytmSourceByFields(instituteId,
                            INSTITUTE.name(), PaytmSourceDataEntity.class,
                            fields);
            if (Objects.nonNull(paytmSourceDataEntity)) {
                institute = paytmSourceDataEntity.getInstituteData();
            }
        } else {
            institute = commonMongoRepository
                    .getEntityByFields(INSTITUTE_ID, instituteId, Institute.class,
                            projectionFields);
        }
        return institute;
    }

    public List<Institute> findAllInstitutesByNIRFOverallRanking() {
        return instituteRepository.findAllByNIRFOverallRanking();
    }

    public List<Exam> getExamsByIdsIn(List<Long> examIds,
            List<String> fields) {
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(EXAM, examIds);
        List<Exam> exams = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(EntitySourceType.PAYTM);
            List<Long> idsWithSourceMerchant =
                    sourceAndEntityIdsMap.get(EntitySourceType.C360);
            if (!CollectionUtils.isEmpty(idsWithSourcePaytm)) {
                List<String> projectionFields =
                        Optional.ofNullable(fields).orElse(new ArrayList<>())
                                .stream().map(f -> EXAM_DATA_FIELD + DOT_SEPERATOR + f)
                                .collect(
                                        Collectors.toList());

                List<PaytmSourceDataEntity> paytmSourceDataEntities = commonMongoRepository
                        .getEntityFieldsFromPaytmSourceByValuesIn(EXAM.name(),
                                idsWithSourcePaytm, PaytmSourceDataEntity.class, projectionFields);
                exams.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getExamData).collect(
                                Collectors.toList()));
            }
            if (!CollectionUtils.isEmpty(idsWithSourceMerchant)) {
                exams.addAll(commonMongoRepository
                        .getEntityFieldsByValuesIn(EXAM_ID, examIds, Exam.class,
                                fields));
            }
        }
        return exams;
    }

    public Exam getExamById(Long examId,
            List<String> projectionFields) {
        EntitySourceType sourceType =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(EXAM, examId);

        Exam exam = null;
        if (EntitySourceType.PAYTM.equals(sourceType)) {
            List<String> fields =
                    Optional.ofNullable(projectionFields).orElse(new ArrayList<>())
                            .stream().map(f -> EXAM_DATA_FIELD + DOT_SEPERATOR + f).collect(
                            Collectors.toList());
            PaytmSourceDataEntity paytmSourceDataEntity =
                    commonMongoRepository.getEntityFromPaytmSourceByFields(examId,
                            EXAM.name(), PaytmSourceDataEntity.class,
                            fields);
            if (Objects.nonNull(paytmSourceDataEntity)) {
                exam = paytmSourceDataEntity.getExamData();
            }
        } else {
            exam = commonMongoRepository
                    .getEntityByFields(EXAM_ID, examId, Exam.class,
                            projectionFields);
        }
        return exam;
    }

    public List<Exam> findAllExams(Map<String, Object> queryObject,
            List<String> fields, String operator) {
        List<Long> examIds = (List<Long>) queryObject.get(EXAM_ID);
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(EXAM, examIds);
        List<Exam> exams = new ArrayList<>();

        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {

            List<Long> idsWithSourceMerchant =
                    sourceAndEntityIdsMap.get(EntitySourceType.C360);
            if (!CollectionUtils.isEmpty(idsWithSourceMerchant)) {
                exams.addAll(commonMongoRepository.findAll(queryObject, Exam.class,
                        fields, operator));
            }

            List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(EntitySourceType.PAYTM);
            if (!CollectionUtils.isEmpty(idsWithSourcePaytm)) {
                queryObject.put(EXAM_ID, idsWithSourcePaytm);
                List<String> projectionFields =
                        Optional.ofNullable(fields).orElse(new ArrayList<>())
                                .stream().map(f -> EXAM_DATA_FIELD + DOT_SEPERATOR + f)
                                .collect(
                                        Collectors.toList());
                Map<String, Object> examQueryObject = queryObject.entrySet()
                        .stream()
                        .collect(Collectors.toMap(entry -> EXAM_DATA_FIELD
                                + DOT_SEPERATOR + entry.getKey(), entry -> entry.getValue()));

                List<PaytmSourceDataEntity> paytmSourceDataEntities =
                        commonMongoRepository
                                .findAll(examQueryObject, PaytmSourceDataEntity.class,
                                        projectionFields, operator);
                exams.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getExamData).collect(
                                Collectors.toList()));
            }

        }
        return exams;
    }

    public List<Course> getCoursesByIdsIn(List<Long> courseIds,
            List<String> fields) {
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(COURSE, courseIds);
        List<Course> courses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(EntitySourceType.PAYTM);

            if (!CollectionUtils.isEmpty(idsWithSourcePaytm)) {
                List<String> projectionFields =
                        Optional.ofNullable(fields).orElse(new ArrayList<>())
                                .stream().map(f -> COURSE_DATA_FIELD + DOT_SEPERATOR + f)
                                .collect(
                                        Collectors.toList());

                List<PaytmSourceDataEntity> paytmSourceDataEntities = commonMongoRepository
                        .getEntityFieldsFromPaytmSourceByValuesIn(COURSE.name(),
                                idsWithSourcePaytm, PaytmSourceDataEntity.class, projectionFields);
                courses.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getCourseData).collect(
                                Collectors.toList()));
            }
            List<Long> idsWithSourceMerchant =
                    sourceAndEntityIdsMap.get(EntitySourceType.C360);
            if (!CollectionUtils.isEmpty(idsWithSourceMerchant)) {
                courses.addAll(commonMongoRepository
                        .getEntityFieldsByValuesIn(COURSE_ID, courseIds, Course.class,
                                fields));
            }
        }
        return courses;
    }

    public List<Course> getAllCourses(Map<String, Object> queryObject,
            List<String> fields, String operator) {
        List<Long> courseIds = (List<Long>) queryObject.get(COURSE_ID);
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(COURSE, courseIds);
        List<Course> courses = new ArrayList<>();

        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {

            List<Long> idsWithSourceMerchant =
                    sourceAndEntityIdsMap.get(EntitySourceType.C360);
            if (!CollectionUtils.isEmpty(idsWithSourceMerchant)) {
                courses.addAll(commonMongoRepository.findAll(queryObject, Course.class,
                        fields, operator));
            }

            List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(EntitySourceType.PAYTM);
            if (!CollectionUtils.isEmpty(idsWithSourcePaytm)) {
                queryObject.put(COURSE_ID, idsWithSourcePaytm);
                List<String> projectionFields =
                        Optional.ofNullable(fields).orElse(new ArrayList<>())
                                .stream().map(f -> COURSE_DATA_FIELD + DOT_SEPERATOR + f)
                                .collect(
                                        Collectors.toList());
                Map<String, Object> courseQueryObject = queryObject.entrySet()
                        .stream()
                        .collect(Collectors.toMap(entry -> COURSE_DATA_FIELD
                                + DOT_SEPERATOR + entry.getKey(), entry -> entry.getValue()));

                List<PaytmSourceDataEntity> paytmSourceDataEntities =
                        commonMongoRepository
                                .findAll(courseQueryObject, PaytmSourceDataEntity.class,
                                        projectionFields, operator);
                courses.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getCourseData).collect(
                                Collectors.toList()));
            }

        }
        return courses;
    }

    public Course getCourseById(Long courseId,
            List<String> fields) {
        EntitySourceType sourceType =
                entitySourceMappingHelper.getSourceAndEntitiesMapping(COURSE, courseId);

        Course course = null;
        if (EntitySourceType.PAYTM.equals(sourceType)) {
            List<String> projectionFields =
                    Optional.ofNullable(fields).orElse(new ArrayList<>())
                            .stream().map(f -> COURSE_DATA_FIELD + DOT_SEPERATOR + f).collect(
                            Collectors.toList());
            PaytmSourceDataEntity paytmSourceDataEntity =
                    commonMongoRepository.getEntityFromPaytmSourceByFields(courseId,
                            COURSE.name(), PaytmSourceDataEntity.class,
                            projectionFields);
            if (Objects.nonNull(paytmSourceDataEntity)) {
                course = paytmSourceDataEntity.getCourseData();
            }
        } else {
            course = commonMongoRepository
                    .getEntityByFields(COURSE_ID, courseId, Course.class, fields);
        }
        return course;
    }

}
