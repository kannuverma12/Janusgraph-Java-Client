package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.PaytmSourceDataEntity;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.enums.EntitySourceType;
import com.paytm.digital.education.serviceimpl.helper.EntitySourceMappingProvider;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.QueryOperators.AND;
import static com.mongodb.QueryOperators.IN;
import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.constant.ExploreConstants.COURSE_DATA_FIELD;
import static com.paytm.digital.education.constant.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.DOT_SEPERATOR;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_DATA_FIELD;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_DATA_FIELD;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.SCHOOL_DATA_FIELD;
import static com.paytm.digital.education.constant.SchoolConstants.SCHOOL_ID;
import static com.paytm.digital.education.enums.EducationEntity.COURSE;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;
import static com.paytm.digital.education.enums.EducationEntity.SCHOOL;
import static com.paytm.digital.education.enums.EntitySourceType.C360;
import static com.paytm.digital.education.enums.EntitySourceType.PAYTM;

@AllArgsConstructor
@Repository
public class CommonEntityMongoDAO {

    private static final Logger log = LoggerFactory.getLogger(CommonEntityMongoDAO.class);

    private final CommonMongoRepository       commonMongoRepository;
    private final EntitySourceMappingProvider entitySourceMappingProvider;

    public Institute getInstituteById(Long instituteId, List<String> projectionFields) {
        EntitySourceType sourceType =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(INSTITUTE, instituteId);

        if (PAYTM.equals(sourceType)) {
            return Optional.ofNullable(
                    commonMongoRepository.getEntityFromPaytmSourceByFields(instituteId,
                            INSTITUTE.name(), PaytmSourceDataEntity.class,
                            getProjectionFieldsForPaytmSource(projectionFields,
                                    INSTITUTE_DATA_FIELD))).map(
                    PaytmSourceDataEntity::getInstituteData)
                    .orElse(null);
        }
        return commonMongoRepository
                .getEntityByFields(INSTITUTE_ID, instituteId, Institute.class, projectionFields);
    }

    public Course getCourseById(Long courseId, List<String> fields) {
        EntitySourceType sourceType =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(COURSE, courseId);

        if (PAYTM.equals(sourceType)) {
            return Optional.ofNullable(
                    commonMongoRepository.getEntityFromPaytmSourceByFields(courseId,
                            COURSE.name(), PaytmSourceDataEntity.class,
                            getProjectionFieldsForPaytmSource(fields, COURSE_DATA_FIELD)))
                    .map(PaytmSourceDataEntity::getCourseData).orElse(null);
        }
        return commonMongoRepository
                .getEntityByFields(COURSE_ID, courseId, Course.class, fields);
    }

    public Exam getExamById(Long examId, List<String> projectionFields) {
        EntitySourceType sourceType =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(EXAM, examId);

        if (PAYTM.equals(sourceType)) {
            return Optional.ofNullable(
                    commonMongoRepository.getEntityFromPaytmSourceByFields(examId,
                            EXAM.name(), PaytmSourceDataEntity.class,
                            getProjectionFieldsForPaytmSource(projectionFields, EXAM_DATA_FIELD)))
                    .map(PaytmSourceDataEntity::getExamData).orElse(null);
        }
        return commonMongoRepository
                .getEntityByFields(EXAM_ID, examId, Exam.class, projectionFields);
    }

    public School getSchoolById(Long schoolId, List<String> schoolProjectionFields) {
        EntitySourceType sourceType =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(SCHOOL, schoolId);

        if (PAYTM.equals(sourceType)) {
            return Optional.ofNullable(
                    commonMongoRepository.getEntityFromPaytmSourceByFields(schoolId,
                            SCHOOL.name(), PaytmSourceDataEntity.class,
                            getProjectionFieldsForPaytmSource(schoolProjectionFields,
                                    SCHOOL_DATA_FIELD))).map(PaytmSourceDataEntity::getSchoolData)
                    .orElse(null);
        }
        return commonMongoRepository
                .getEntityByFields(SCHOOL_ID, schoolId, School.class, schoolProjectionFields);
    }

    public List<School> getSchoolsByIdsIn(List<Long> schoolIds,
            List<String> schoolProjectionFields) {
        List<School> schools = new ArrayList<>();
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(SCHOOL, schoolIds);

        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            if (sourceAndEntityIdsMap.containsKey(C360)) {
                List<Long> idsWithSourceMerchant = sourceAndEntityIdsMap.get(C360);
                if (!CollectionUtils.isEmpty(idsWithSourceMerchant)) {
                    schools.addAll(Optional.ofNullable(commonMongoRepository
                            .getEntityFieldsByValuesIn(SCHOOL_ID, idsWithSourceMerchant,
                                    School.class,
                                    schoolProjectionFields)).orElse(new ArrayList<>()));
                }
            }

            if (sourceAndEntityIdsMap.containsKey(PAYTM)) {
                List<Long> schoolIdsWithSourcePaytm = sourceAndEntityIdsMap.get(PAYTM);
                schools.addAll(Optional.ofNullable(commonMongoRepository
                        .getEntityFieldsFromPaytmSourceByValuesIn(SCHOOL.name(),
                                schoolIdsWithSourcePaytm, PaytmSourceDataEntity.class,
                                getProjectionFieldsForPaytmSource(schoolProjectionFields,
                                        SCHOOL_DATA_FIELD)))
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getSchoolData).collect(
                                Collectors.toList()));
            }
        }
        return schools;
    }

    public List<Institute> getInstitutesByIdsIn(List<Long> instituteIds,
            List<String> fields) {
        List<Institute> institutes = new ArrayList<>();
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(INSTITUTE, instituteIds);

        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            if (sourceAndEntityIdsMap.containsKey(C360)) {
                List<Long> idsWithSourceMerchant = sourceAndEntityIdsMap.get(C360);
                institutes.addAll(Optional.ofNullable(commonMongoRepository
                        .getEntityFieldsByValuesIn(INSTITUTE_ID, idsWithSourceMerchant,
                                Institute.class,
                                fields)).orElse(new ArrayList<>()));
            }

            if (sourceAndEntityIdsMap.containsKey(PAYTM)) {
                List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(PAYTM);
                List<PaytmSourceDataEntity> paytmSourceDataEntities = commonMongoRepository
                        .getEntityFieldsFromPaytmSourceByValuesIn(INSTITUTE.name(),
                                idsWithSourcePaytm, PaytmSourceDataEntity.class,
                                getProjectionFieldsForPaytmSource(fields, INSTITUTE_DATA_FIELD));
                institutes.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getInstituteData).collect(
                                Collectors.toList()));
            }
        }

        return institutes;
    }

    public List<Exam> getExamsByIdsIn(List<Long> examIds,
            List<String> fields) {
        List<Exam> exams = new ArrayList<>();
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(EXAM, examIds);

        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            if (sourceAndEntityIdsMap.containsKey(C360)) {
                List<Long> idsWithSourceMerchant = sourceAndEntityIdsMap.get(C360);
                exams.addAll(Optional.ofNullable(commonMongoRepository
                        .getEntityFieldsByValuesIn(EXAM_ID, idsWithSourceMerchant, Exam.class,
                                fields)).orElse(new ArrayList<>()));
            }

            if (sourceAndEntityIdsMap.containsKey(PAYTM)) {
                List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(PAYTM);
                List<PaytmSourceDataEntity> paytmSourceDataEntities = commonMongoRepository
                        .getEntityFieldsFromPaytmSourceByValuesIn(EXAM.name(),
                                idsWithSourcePaytm, PaytmSourceDataEntity.class,
                                getProjectionFieldsForPaytmSource(fields, EXAM_DATA_FIELD));
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
        List<Course> courses = new ArrayList<>();
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(COURSE, courseIds);

        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            if (sourceAndEntityIdsMap.containsKey(C360)) {
                List<Long> idsWithSourceMerchant = sourceAndEntityIdsMap.get(C360);
                courses.addAll(Optional.ofNullable(commonMongoRepository
                        .getEntityFieldsByValuesIn(COURSE_ID, idsWithSourceMerchant, Course.class,
                                fields)).orElse(new ArrayList<>()));
            }

            if (sourceAndEntityIdsMap.containsKey(PAYTM)) {
                List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(PAYTM);
                List<PaytmSourceDataEntity> paytmSourceDataEntities = commonMongoRepository
                        .getEntityFieldsFromPaytmSourceByValuesIn(COURSE.name(),
                                idsWithSourcePaytm, PaytmSourceDataEntity.class,
                                getProjectionFieldsForPaytmSource(fields, COURSE_DATA_FIELD));
                courses.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getCourseData).collect(
                                Collectors.toList()));
            }
        }
        return courses;
    }

    public List<Institute> getAllInstitutes(Map<String, Object> queryObject,
            List<String> instituteFields, String operator) {
        List<Institute> institutes = new ArrayList<>();
        List<Long> instituteIds = null;
        try {
            instituteIds = getEntityIds(queryObject, operator, INSTITUTE_ID);
        } catch (Exception e) {
            log.error("Exception occurred while getting institutes for :", e, queryObject);
        }
        if (CollectionUtils.isEmpty(instituteIds)) {
            return institutes;
        }

        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(INSTITUTE, instituteIds);

        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            if (sourceAndEntityIdsMap.containsKey(C360)) {
                List<Long> idsWithSourceMerchant = sourceAndEntityIdsMap.get(C360);
                updateQueryObject(queryObject, operator, INSTITUTE_ID, idsWithSourceMerchant);
                institutes.addAll(Optional
                        .ofNullable(commonMongoRepository.findAll(queryObject, Institute.class,
                                instituteFields, operator)).orElse(new ArrayList<>()));
            }
            if (sourceAndEntityIdsMap.containsKey(PAYTM)) {
                List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(PAYTM);
                updateQueryObject(queryObject, operator, INSTITUTE_ID, idsWithSourcePaytm);

                List<PaytmSourceDataEntity> paytmSourceDataEntities =
                        commonMongoRepository
                                .findAll(getQueryMapForPaytmSource(queryObject,
                                        INSTITUTE_DATA_FIELD),
                                        PaytmSourceDataEntity.class,
                                        getProjectionFieldsForPaytmSource(instituteFields,
                                                INSTITUTE_DATA_FIELD), operator);
                institutes.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getInstituteData).collect(
                                Collectors.toList()));
            }
        }
        return institutes;
    }

    public List<Exam> getAllExams(Map<String, Object> queryObject,
            List<String> fields, String operator) {
        List<Exam> exams = new ArrayList<>();
        List<Long> examIds = Optional.ofNullable((List<Long>) queryObject.get(EXAM_ID))
                .orElse(new ArrayList<>());
        try {
            examIds = getEntityIds(queryObject, operator, EXAM_ID);
        } catch (Exception e) {
            log.error("Exception occurred while getting courses for :", e, queryObject);
        }
        if (CollectionUtils.isEmpty(examIds)) {
            return exams;
        }

        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(EXAM, examIds);

        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            if (sourceAndEntityIdsMap.containsKey(C360)) {
                List<Long> idsWithSourceMerchant = sourceAndEntityIdsMap.get(C360);
                updateQueryObject(queryObject, operator, EXAM_ID, idsWithSourceMerchant);

                exams.addAll(
                        Optional.ofNullable(commonMongoRepository.findAll(queryObject, Exam.class,
                                fields, operator)).orElse(new ArrayList<>()));
            }
            if (sourceAndEntityIdsMap.containsKey(PAYTM)) {
                List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(PAYTM);
                updateQueryObject(queryObject, operator, EXAM_ID, idsWithSourcePaytm);

                List<PaytmSourceDataEntity> paytmSourceDataEntities =
                        commonMongoRepository
                                .findAll(getQueryMapForPaytmSource(queryObject, EXAM_DATA_FIELD),
                                        PaytmSourceDataEntity.class,
                                        getProjectionFieldsForPaytmSource(fields, EXAM_DATA_FIELD),
                                        operator);
                exams.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getExamData).collect(
                                Collectors.toList()));
            }
        }
        return exams;
    }

    public List<Course> getAllCourses(Map<String, Object> queryObject,
            List<String> fields, String operator) {
        List<Long> instituteIds = null;
        List<Course> courses = new ArrayList<>();
        try {
            instituteIds = getEntityIds(queryObject, operator, INSTITUTE_ID);
        } catch (Exception e) {
            log.error("Exception occurred while getting courses for :", e, queryObject);
        }
        if (CollectionUtils.isEmpty(instituteIds)) {
            return courses;
        }
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap =
                entitySourceMappingProvider.getSourceAndEntitiesMapping(COURSE, instituteIds);

        if (!CollectionUtils.isEmpty(sourceAndEntityIdsMap)) {
            if (sourceAndEntityIdsMap.containsKey(C360)) {
                List<Long> idsWithSourceMerchant = sourceAndEntityIdsMap.get(C360);
                updateQueryObject(queryObject, operator, INSTITUTE_ID, idsWithSourceMerchant);
                courses.addAll(
                        Optional.ofNullable(commonMongoRepository.findAll(queryObject, Course.class,
                                fields, operator)).orElse(new ArrayList<>()));
            }

            if (sourceAndEntityIdsMap.containsKey(PAYTM)) {
                List<Long> idsWithSourcePaytm = sourceAndEntityIdsMap.get(PAYTM);
                updateQueryObject(queryObject, operator, INSTITUTE_ID, idsWithSourcePaytm);

                List<PaytmSourceDataEntity> paytmSourceDataEntities =
                        commonMongoRepository
                                .findAll(getQueryMapForPaytmSource(queryObject, COURSE_DATA_FIELD),
                                        PaytmSourceDataEntity.class,
                                        getProjectionFieldsForPaytmSource(fields,
                                                COURSE_DATA_FIELD),
                                        operator);
                courses.addAll(Optional.ofNullable(paytmSourceDataEntities)
                        .orElse(new ArrayList<>()).stream().map(
                                PaytmSourceDataEntity::getCourseData).collect(
                                Collectors.toList()));
            }
        }
        return courses;
    }

    private List<Long> getEntityIds(Map<String, Object> queryObject, String operator, String key) {
        List<Long> entityIds = null;
        if (operator.equals(OR)) {
            entityIds = (List<Long>) queryObject.get(key);
        } else if (operator.equals(AND)) {
            entityIds = (List<Long>) ((Map) queryObject.get(key)).get(IN);
        }
        return entityIds;
    }

    private void updateQueryObject(Map<String, Object> queryObject, String operator, String key, List<Long> ids) {
        if (operator.equals(AND)) {
            queryObject.put(key, Collections.singletonMap(IN, ids));
        } else {
            queryObject.put(key, ids);
        }
    }

    private List<String> getProjectionFieldsForPaytmSource(List<String> projectionFields,
            String entityDataField) {
        return Optional.ofNullable(projectionFields).orElse(new ArrayList<>())
                .stream().map(f -> entityDataField + DOT_SEPERATOR + f).collect(
                        Collectors.toList());
    }

    private Map<String, Object> getQueryMapForPaytmSource(Map<String, Object> queryObject,
            String courseDataField) {
        return queryObject.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> courseDataField
                        + DOT_SEPERATOR + entry.getKey(), Map.Entry::getValue));
    }
}
