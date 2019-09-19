package com.paytm.digital.education.admin.service.impl;

import com.paytm.digital.education.admin.request.EntityRankingRequest;
import com.paytm.digital.education.admin.request.RankingsRequest;
import com.paytm.digital.education.admin.response.EntityRankingResponse;
import com.paytm.digital.education.admin.response.RankingResponse;
import com.paytm.digital.education.admin.service.RankingService;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.PaytmKeys;
import com.paytm.digital.education.explore.database.entity.School;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.stream.Collectors;

import static com.mongodb.QueryOperators.EXISTS;
import static com.mongodb.QueryOperators.NE;
import static com.mongodb.QueryOperators.AND;
import static com.paytm.digital.education.admin.utility.AdminConstants.PAYTM_DESCRIPTION;
import static com.paytm.digital.education.admin.utility.AdminConstants.PAYTM_KEYS;
import static com.paytm.digital.education.admin.utility.AdminConstants.PAYTM_RANK;
import static com.paytm.digital.education.admin.utility.AdminConstants.PAYTM_PARTNER_RANK;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class RankingServiceImpl implements RankingService {

    private CommonMongoRepository commonMongoRepository;

    public RankingResponse getPaytmRankings(EducationEntity entity) {
        RankingResponse rankingResponse = new RankingResponse();

        List<EntityRankingResponse> rankingList = new ArrayList<>();

        Class type = entity.equals(EducationEntity.INSTITUTE) ? Institute.class :
                entity.equals(EducationEntity.EXAM) ? Exam.class :
                        entity.equals(EducationEntity.SCHOOL) ? School.class : null;

        if (Objects.nonNull(type)) {
            rankingResponse.setEntity(entity.name());
            if (entity.equals(EducationEntity.INSTITUTE)) {

                List<Institute> dbInstitutes = getPaytmRankedEntities(INSTITUTE_ID, type);
                if (!CollectionUtils.isEmpty(dbInstitutes)) {
                    for (Institute institute : dbInstitutes) {
                        rankingList.add(setRankingResponse(institute.getInstituteId(),
                                institute.getPaytmKeys().getPaytmRank(),
                                institute.getPaytmKeys().getPaytmPartnerRank(),
                                institute.getOfficialName()));
                    }
                }
            } else if (entity.equals(EducationEntity.EXAM)) {
                List<Exam> dbExams = getPaytmRankedEntities(EXAM_ID, type);
                if (!CollectionUtils.isEmpty(dbExams)) {
                    for (Exam exam : dbExams) {
                        rankingList.add(setRankingResponse(exam.getExamId(),
                                exam.getPaytmKeys().getPaytmRank(),
                                exam.getPaytmKeys().getPaytmPartnerRank(),
                                exam.getExamShortName()));
                    }
                }
            } else if (entity.equals(EducationEntity.SCHOOL)) {
                List<School> dbSchool = getPaytmRankedEntities(SCHOOL_ID, type);
                if (!CollectionUtils.isEmpty(dbSchool)) {
                    for (School school : dbSchool) {
                        rankingList.add(setRankingResponse(school.getSchoolId(),
                                school.getPaytmKeys().getPaytmRank(),
                                school.getPaytmKeys().getPaytmPartnerRank(),
                                school.getOfficialName()));
                    }
                }
            }
            rankingResponse.setRankings(rankingList);
        }
        return rankingResponse;
    }

    private EntityRankingResponse setRankingResponse(Long instituteId, Long paytmRank,
            Long paytmPartnerRank, String officialName) {
        EntityRankingResponse entityRankingResponse = new EntityRankingResponse();
        entityRankingResponse.setEntityId(instituteId);
        entityRankingResponse.setPaytmRank(paytmRank);
        entityRankingResponse.setPaytmPartnerRank(paytmPartnerRank);
        entityRankingResponse.setOfficialName(officialName);
        return entityRankingResponse;
    }

    public RankingResponse updateRankings(
                RankingsRequest rankRequest) {

        RankingResponse rankingResponse = new RankingResponse();
        EducationEntity entity = rankRequest.getEntity();
        Class type = entity.equals(EducationEntity.INSTITUTE) ? Institute.class :
                entity.equals(EducationEntity.EXAM) ? Exam.class :
                        entity.equals(EducationEntity.SCHOOL) ? School.class : null;

        String key = entity.equals(EducationEntity.INSTITUTE) ? INSTITUTE_ID :
            entity.equals(EducationEntity.EXAM) ? EXAM_ID :
                    entity.equals(EducationEntity.SCHOOL) ? SCHOOL_ID : null;

        if (!StringUtils.isEmpty(type) && !StringUtils.isEmpty(key)) {
            rankingResponse.setEntity(entity.name());

            List<Long> entityIds =
                    rankRequest.getRankings().stream().map(ir -> ir.getEntityId())
                            .collect(Collectors.toList());
            Set<Long> validRanks = rankRequest.getRankings().stream().map(ir -> ir.getPaytmRank())
                    .collect(Collectors.toSet());

            if (validRanks.size() != entityIds.size()) {
                log.info("No two institutes can have same rank");
                return getResponse(rankingResponse, "Two entities can not have same rank",
                        412);
            }

            switch (key) {
                case INSTITUTE_ID :
                    rankingResponse = updateRankingsForInstitutes(rankRequest, entityIds, key, type);
                    break;
                case EXAM_ID:
                    rankingResponse = updateRankingsForExam(rankRequest, entityIds, key, type);
                    break;
                case SCHOOL_ID:
                    rankingResponse = updateRankingsForSchool(rankRequest, entityIds, key, type);
                    break;
                default:
                    break;
            }
        } else {
            return getResponse(rankingResponse, "Invalid Entity", 412);
        }
        if (!CollectionUtils.isEmpty(rankingResponse.getRankings())) {
            return getResponse(rankingResponse, "Paytm Ranks updated", 200);
        }
        return rankingResponse;
    }

    private RankingResponse getResponse(RankingResponse rankingResponse, String message,
            int statusCode) {
        rankingResponse.setMessage(message);
        rankingResponse.setStatus(statusCode);
        return rankingResponse;
    }

    private RankingResponse updateRankingsForInstitutes(RankingsRequest rankRequest,
            List<Long> entityIds, String key, Class type) {
        RankingResponse rankingResponse = new RankingResponse();
        List<EntityRankingResponse> rankResponseList = new ArrayList<>();
        List<Institute> validInstitutes = validEntitites(entityIds, key, type);
        if (validInstitutes.size() == rankRequest.getRankings().size()) {
            unsetPreviousPaytmRanksForInsitute(key, type);

            Map<Long, Institute> instituteMap = validInstitutes.stream()
                    .collect(Collectors.toMap(i -> i.getInstituteId(), i -> i));

            for (EntityRankingRequest entityRankingRequest : rankRequest
                    .getRankings()) {
                Institute institute = instituteMap.get(entityRankingRequest.getEntityId());
                if (Objects.nonNull(institute)) {
                    updatePaytmRanking(entityRankingRequest, key, type,
                            institute.getInstituteId());
                }
            }
            rankResponseList = getUpdatedInstitutes(entityIds, key, type);
            rankingResponse.setRankings(rankResponseList);

        } else {
            log.info("Institute ids not valid");
            return getResponse(rankingResponse, "Invalid Institute id(s).", 412);
        }
        return rankingResponse;
    }

    private void unsetPreviousPaytmRanksForInsitute(String key, Class type) {
        List<Institute> dbInstitutes = getPaytmRankedEntities(key, type);
        for (Institute institute : dbInstitutes) {
            updatePaytmRank(key, type, institute.getInstituteId(), new Long(-10), new Long(-10),
                    "");
        }
    }

    private List<EntityRankingResponse> getUpdatedInstitutes(List<Long> instituteIds, String key,
            Class type) {
        List<Institute> validInstitutes = validEntitites(instituteIds, key, type);

        List<EntityRankingResponse> rankingResponseList = new ArrayList<>();
        for (Institute institute : validInstitutes) {
            EntityRankingResponse rankingResponse =
                    getUpdatedData(institute.getInstituteId(), institute.getPaytmKeys());
            rankingResponseList.add(rankingResponse);
        }
        return rankingResponseList;
    }

    private RankingResponse updateRankingsForExam(RankingsRequest rankRequest, List<Long> entityIds,
            String key, Class type) {
        RankingResponse rankingResponse = new RankingResponse();
        List<EntityRankingResponse> rankResponseList = new ArrayList<>();
        List<Exam> validExams = validEntitites(entityIds, key, type);
        if (validExams.size() == rankRequest.getRankings().size()) {
            unsetPreviousPaytmRanksForExam(key, type);
            Map<Long, Exam> examMap = validExams.stream()
                    .collect(Collectors.toMap(e -> e.getExamId(), e -> e));
            for (EntityRankingRequest entityRankingRequest : rankRequest
                    .getRankings()) {
                Exam exam = examMap.get(entityRankingRequest.getEntityId());
                if (Objects.nonNull(exam)) {
                    updatePaytmRanking(entityRankingRequest, key, type, exam.getExamId());
                }
            }
            rankResponseList = getUpdatedExams(entityIds, key, type);
            rankingResponse.setRankings(rankResponseList);
        } else {
            log.info("Exams ids not valid");
            return getResponse(rankingResponse, "Invalid Exam id(s).", 412);
        }
        return rankingResponse;

    }

    private void unsetPreviousPaytmRanksForExam(String key, Class type) {
        List<Exam> dbExams = getPaytmRankedEntities(key, type);
        for (Exam exam : dbExams) {
            updatePaytmRank(key, type, exam.getExamId(), new Long(-10), new Long(-10), "");
        }
    }

    private List<EntityRankingResponse> getUpdatedExams(List<Long> examIds, String key, Class type) {
        List<Exam> validExams = validEntitites(examIds, key, type);
        List<EntityRankingResponse> rankingResponseList = new ArrayList<>();
        for (Exam exam : validExams) {
            Long id = exam.getExamId();
            PaytmKeys paytmKeys = exam.getPaytmKeys();
            EntityRankingResponse rankingResponse = getUpdatedData(id, paytmKeys);
            rankingResponseList.add(rankingResponse);
        }
        return rankingResponseList;
    }

    private RankingResponse updateRankingsForSchool(RankingsRequest rankRequest,
            List<Long> entityIds, String key, Class type) {
        RankingResponse rankingResponse = new RankingResponse();
        List<EntityRankingResponse> rankResponseList = new ArrayList<>();
        List<School> validSchools = validEntitites(entityIds, key, type);
        if (validSchools.size() == rankRequest.getRankings().size()) {
            unsetPreviousPaytmRanksForSchool(key, type);
            Map<Long, School> schoolMapMap = validSchools.stream()
                    .collect(Collectors.toMap(s -> s.getSchoolId(), s -> s));
            for (EntityRankingRequest entityRankingRequest : rankRequest
                    .getRankings()) {
                School school = schoolMapMap.get(entityRankingRequest.getEntityId());
                if (Objects.nonNull(school)) {
                    updatePaytmRanking(entityRankingRequest, key, type, school.getSchoolId());
                }
            }
            rankResponseList = getUpdatedSchools(entityIds, key, type);
            rankingResponse.setRankings(rankResponseList);
        } else {
            log.info("School ids not valid");
            return getResponse(rankingResponse, "Invalid School id(s).", 412);
        }
        return rankingResponse;
    }

    private void unsetPreviousPaytmRanksForSchool(String key, Class type) {
        List<School> dbSchools = getPaytmRankedEntities(key, type);
        for (School school : dbSchools) {
            updatePaytmRank(key, type, school.getSchoolId(), new Long(-10), new Long(-10), "");
        }
    }

    private List<EntityRankingResponse> getUpdatedSchools(List<Long> schoolIds, String key, Class type) {
        List<School> validSchools = validEntitites(schoolIds, key, type);
        List<EntityRankingResponse> rankingResponseList = new ArrayList<>();
        for (School school : validSchools) {
            EntityRankingResponse rankingResponse =
                    getUpdatedData(school.getSchoolId(), school.getPaytmKeys());
            rankingResponseList.add(rankingResponse);
        }
        return rankingResponseList;
    }

    private <T> List<T> getPaytmRankedEntities(String key, Class<T> type) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put(EXISTS, true);
        queryMap.put(NE, null);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(PAYTM_RANK , queryMap);
        List<String> fields = Arrays.asList(key, OFFICIAL_NAME, PAYTM_KEYS);
        return commonMongoRepository
                .findAll(objectMap, type, fields , AND);
    }

    private <T> List<T> validEntitites(List<Long> entityIds, String key, Class<T> type) {
        return commonMongoRepository
                .getEntityFieldsByValuesIn(key, entityIds, type,
                        Arrays.asList(key, PAYTM_KEYS), PAYTM_KEYS);
    }


    private EntityRankingResponse getUpdatedData(Long entityId, PaytmKeys paytmKeys) {
        EntityRankingResponse entityRankingResponse = new EntityRankingResponse();

        entityRankingResponse.setEntityId(entityId);
        if (Objects.nonNull(paytmKeys)) {
            if (Objects.nonNull(paytmKeys.getPaytmDescription())) {
                entityRankingResponse.setDescription(paytmKeys.getPaytmDescription());
            }
            if (Objects.nonNull(paytmKeys.getPaytmRank())) {
                entityRankingResponse.setPaytmRank(paytmKeys.getPaytmRank());
            }
            if (Objects.nonNull(paytmKeys.getPaytmPartnerRank())) {
                entityRankingResponse.setPaytmPartnerRank(paytmKeys.getPaytmPartnerRank());
            }
        }
        return entityRankingResponse;
    }

    private void updatePaytmRank(String key, Class type, Long entityId, Long rank,
            Long paytmPartnerRank, String description) {
        Map<String, Object> queryObject1 = new HashMap<>();
        queryObject1.put(key, entityId);
        Update update = new Update();
        update.set(PAYTM_RANK, rank);
        update.set(PAYTM_PARTNER_RANK, paytmPartnerRank);
        update.set(PAYTM_DESCRIPTION, description);
        List<String> fields2 = Arrays.asList(key, PAYTM_KEYS);
        commonMongoRepository.upsertData(queryObject1, fields2, update, type);
    }

    private void updatePaytmRanking(
            EntityRankingRequest entityRankingRequest, String key, Class type, Long entityId) {

        if (Objects.nonNull(entityRankingRequest.getPaytmRank())) {
            updatePaytmRank(key, type, entityId, entityRankingRequest.getPaytmRank(),
                    entityRankingRequest.getPaytmPartnerRank(),
                    entityRankingRequest.getPaytmDescription());
        }
    }

}
